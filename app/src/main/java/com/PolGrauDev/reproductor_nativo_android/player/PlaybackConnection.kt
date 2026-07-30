package com.PolGrauDev.reproductor_nativo_android.player

import android.content.ComponentName
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
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
)

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

    private val _state = MutableStateFlow(PlaybackUiState())
    val state: StateFlow<PlaybackUiState> = _state.asStateFlow()

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _state.update { it.copy(isPlaying = isPlaying) }
            togglePositionPolling(isPlaying)
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            _state.update {
                it.copy(currentMediaId = mediaItem?.mediaId, durationMs = safeDuration())
            }
            enrichCurrentItemArtwork()
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            _state.update { it.copy(playbackState = playbackState, durationMs = safeDuration()) }
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
    }

    fun skipNext() {
        controller?.seekToNextMediaItem()
    }

    fun skipPrevious() {
        controller?.seekToPreviousMediaItem()
    }

    fun release() {
        positionJob?.cancel()
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
            )
        }
        togglePositionPolling(c.isPlaying)
    }

    private fun safeDuration(): Long = controller?.duration?.coerceAtLeast(0) ?: 0L

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
