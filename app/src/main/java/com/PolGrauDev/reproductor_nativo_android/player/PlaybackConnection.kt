package com.PolGrauDev.reproductor_nativo_android.player

import android.content.ComponentName
import android.content.Context
import android.os.SystemClock
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.PolGrauDev.reproductor_nativo_android.data.AlbumArtExtractor
import com.PolGrauDev.reproductor_nativo_android.data.model.Song
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class PlaybackUiState(
    val currentMediaId: String? = null,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val playbackState: Int = Player.STATE_IDLE,
    val shuffleModeEnabled: Boolean = false,
    val repeatMode: Int = Player.REPEAT_MODE_OFF,
    val queueMediaIds: List<String> = emptyList(),
    val currentIndex: Int = -1,
    val errorMessage: String? = null,
    val sleepTimerActive: Boolean = false,
    val sleepTimerRemainingMs: Long = 0L,
)

private const val MAX_CONSECUTIVE_ERRORS = 3

/**
 * Puente entre la UI/ViewModel y [PlaybackService]. La UI nunca habla con el Service
 * directamente: se conecta vía [MediaController], que sigue funcionando aunque la
 * Activity/pantalla no esté visible.
 */
class PlaybackConnection(private val context: Context) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var controller: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var positionJob: Job? = null
    private var consecutiveErrorCount = 0

    private var sleepTimerJob: Job? = null

    private var fadeDurationMs: Int = 0
    private var fadeOutJob: Job? = null
    private var fadeInJob: Job? = null

    private val _state = MutableStateFlow(PlaybackUiState())
    val state: StateFlow<PlaybackUiState> = _state.asStateFlow()

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (isPlaying) consecutiveErrorCount = 0
            _state.update { it.copy(isPlaying = isPlaying) }
            togglePositionPolling(isPlaying)
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            _state.update {
                it.copy(currentMediaId = mediaItem?.mediaId, durationMs = safeDuration())
            }
            refreshQueue()
            enrichCurrentItemArtwork()
            startFadeIn()
            rescheduleFadeOut()
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            _state.update { it.copy(playbackState = playbackState, durationMs = safeDuration()) }
            rescheduleFadeOut()
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            _state.update { it.copy(shuffleModeEnabled = shuffleModeEnabled) }
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            _state.update { it.copy(repeatMode = repeatMode) }
        }

        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            refreshQueue()
        }

        override fun onPlayerError(error: PlaybackException) {
            val failedTitle = controller?.currentMediaItem?.mediaMetadata?.title?.toString()
            consecutiveErrorCount++

            val message = if (consecutiveErrorCount > MAX_CONSECUTIVE_ERRORS) {
                "Varias canciones seguidas no se han podido reproducir. Reproducción detenida."
            } else {
                error.toUserMessage(failedTitle)
            }
            _state.update { it.copy(errorMessage = message) }

            val c = controller
            if (c != null && consecutiveErrorCount <= MAX_CONSECUTIVE_ERRORS && c.hasNextMediaItem()) {
                c.seekToNextMediaItem()
                c.prepare()
                c.play()
            }
        }
    }

    fun connect() {
        if (controllerFuture != null) return
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val future = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture = future
        future.addListener(
            {
                val mediaController = future.get()
                controller = mediaController
                mediaController.addListener(playerListener)
                syncState(mediaController)
            },
            ContextCompat.getMainExecutor(context),
        )
    }

    fun playQueue(songs: List<Song>, startIndex: Int) {
        val items = songs.map { it.toMediaItem() }
        controller?.apply {
            setMediaItems(items, startIndex, 0L)
            prepare()
            play()
        }
    }

    fun togglePlayPause() {
        val c = controller ?: return
        if (c.isPlaying) c.pause() else c.play()
    }

    fun seekTo(positionMs: Long) {
        controller?.seekTo(positionMs)
        rescheduleFadeOut()
    }

    fun skipNext() {
        controller?.seekToNextMediaItem()
    }

    fun skipPrevious() {
        controller?.seekToPreviousMediaItem()
    }

    fun toggleShuffle() {
        controller?.let { it.shuffleModeEnabled = !it.shuffleModeEnabled }
    }

    /** Ciclo estándar: sin repetir -> repetir todo -> repetir una -> sin repetir. */
    fun cycleRepeatMode() {
        val c = controller ?: return
        c.repeatMode = when (c.repeatMode) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            else -> Player.REPEAT_MODE_OFF
        }
    }

    fun moveQueueItem(from: Int, to: Int) {
        controller?.moveMediaItem(from, to)
    }

    fun removeQueueItem(index: Int) {
        controller?.removeMediaItem(index)
    }

    fun playQueueItem(index: Int) {
        controller?.seekTo(index, 0L)
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }

    fun startSleepTimer(minutes: Int) {
        sleepTimerJob?.cancel()
        val endAt = SystemClock.elapsedRealtime() + minutes * 60_000L
        sleepTimerJob = scope.launch {
            while (isActive) {
                val remaining = sleepTimerRemainingMs(endAt, SystemClock.elapsedRealtime())
                if (remaining <= 0L) {
                    controller?.pause()
                    _state.update { it.copy(sleepTimerActive = false, sleepTimerRemainingMs = 0L) }
                    break
                }
                _state.update { it.copy(sleepTimerActive = true, sleepTimerRemainingMs = remaining) }
                delay(1_000)
            }
        }
    }

    fun cancelSleepTimer() {
        sleepTimerJob?.cancel()
        _state.update { it.copy(sleepTimerActive = false, sleepTimerRemainingMs = 0L) }
    }

    /**
     * Fundido secuencial de volumen entre pistas (no crossfade real: ExoPlayer no expone
     * solapamiento de audio entre dos reproductores sin una arquitectura de doble player).
     * `ms` = 0 desactiva el fundido.
     */
    fun setFadeDurationMs(ms: Int) {
        fadeDurationMs = ms
        if (ms <= 0) {
            fadeOutJob?.cancel()
            fadeInJob?.cancel()
            controller?.volume = 1f
        } else {
            rescheduleFadeOut()
        }
    }

    fun release() {
        positionJob?.cancel()
        sleepTimerJob?.cancel()
        fadeOutJob?.cancel()
        fadeInJob?.cancel()
        controller?.removeListener(playerListener)
        controllerFuture?.let { MediaController.releaseFuture(it) }
        controller = null
        controllerFuture = null
        _state.value = PlaybackUiState()
    }

    private fun syncState(c: MediaController) {
        _state.update {
            it.copy(
                currentMediaId = c.currentMediaItem?.mediaId,
                isPlaying = c.isPlaying,
                positionMs = c.currentPosition.coerceAtLeast(0),
                durationMs = c.duration.coerceAtLeast(0),
                playbackState = c.playbackState,
                shuffleModeEnabled = c.shuffleModeEnabled,
                repeatMode = c.repeatMode,
            )
        }
        refreshQueue()
        togglePositionPolling(c.isPlaying)
    }

    private fun safeDuration(): Long = controller?.duration?.coerceAtLeast(0) ?: 0L

    private fun refreshQueue() {
        val c = controller ?: return
        val ids = (0 until c.mediaItemCount).map { c.getMediaItemAt(it).mediaId }
        _state.update { it.copy(queueMediaIds = ids, currentIndex = c.currentMediaItemIndex) }
    }

    /** Player no emite la posición de forma continua; se sondea mientras suena. */
    private fun togglePositionPolling(active: Boolean) {
        positionJob?.cancel()
        if (!active) return
        positionJob = scope.launch {
            while (isActive) {
                controller?.let { c ->
                    _state.update { it.copy(positionMs = c.currentPosition.coerceAtLeast(0)) }
                }
                delay(500)
            }
        }
    }

    private fun startFadeIn() {
        fadeInJob?.cancel()
        if (fadeDurationMs <= 0) return
        controller?.volume = 0f
        fadeInJob = scope.launch { rampVolume(to = 1f, durationMs = fadeDurationMs) }
    }

    /** Reprograma el fundido de salida según la posición/duración actuales de la pista. */
    private fun rescheduleFadeOut() {
        fadeOutJob?.cancel()
        val c = controller ?: return
        val delayMs = fadeOutDelayMs(
            durationMs = c.duration.coerceAtLeast(0),
            positionMs = c.currentPosition.coerceAtLeast(0),
            fadeDurationMs = fadeDurationMs,
        ) ?: return
        fadeOutJob = scope.launch {
            delay(delayMs)
            rampVolume(to = 0f, durationMs = fadeDurationMs)
        }
    }

    private suspend fun rampVolume(to: Float, durationMs: Int) {
        val steps = 20
        val stepDelayMs = (durationMs / steps).toLong().coerceAtLeast(1L)
        val from = controller?.volume ?: return
        repeat(steps) { step ->
            val fraction = (step + 1).toFloat() / steps
            controller?.volume = from + (to - from) * fraction
            delay(stepDelayMs)
        }
    }

    /**
     * Extrae la carátula embebida solo para la pista actual (no para toda la cola) y
     * actualiza su MediaItem para que la notificación/lock screen la muestren.
     */
    private fun enrichCurrentItemArtwork() {
        val c = controller ?: return
        val index = c.currentMediaItemIndex
        val item = c.currentMediaItem ?: return
        if (item.mediaMetadata.artworkData != null) return
        val uri = item.localConfiguration?.uri ?: return

        scope.launch {
            val bytes = withContext(Dispatchers.IO) {
                AlbumArtExtractor.extractEmbeddedArt(context, uri)
            } ?: return@launch

            val updatedMetadata = item.mediaMetadata.buildUpon()
                .setArtworkData(bytes, MediaMetadata.PICTURE_TYPE_FRONT_COVER)
                .build()
            controller?.replaceMediaItem(index, item.buildUpon().setMediaMetadata(updatedMetadata).build())
        }
    }
}

private fun PlaybackException.toUserMessage(songTitle: String?): String {
    val prefix = songTitle?.takeIf { it.isNotBlank() }?.let { "\"$it\": " } ?: ""
    val reason = when (errorCode) {
        PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND -> "no se encuentra el archivo"
        PlaybackException.ERROR_CODE_IO_NO_PERMISSION -> "sin permiso para acceder al archivo"
        PlaybackException.ERROR_CODE_PARSING_CONTAINER_MALFORMED,
        PlaybackException.ERROR_CODE_PARSING_MANIFEST_MALFORMED,
        PlaybackException.ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED -> "el archivo está dañado o no es compatible"
        PlaybackException.ERROR_CODE_DECODER_INIT_FAILED,
        PlaybackException.ERROR_CODE_DECODING_FAILED -> "no se pudo decodificar el audio"
        else -> "no se pudo reproducir"
    }
    return "$prefix$reason"
}
