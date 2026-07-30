package com.PolGrauDev.reproductor_nativo_android.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.PolGrauDev.reproductor_nativo_android.data.MediaRepository
import com.PolGrauDev.reproductor_nativo_android.data.model.Song
import com.PolGrauDev.reproductor_nativo_android.player.PlaybackConnection
import com.PolGrauDev.reproductor_nativo_android.player.PlaybackUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MusicUiState(
    val songs: List<Song> = emptyList(),
    val isLoadingLibrary: Boolean = true,
    val playback: PlaybackUiState = PlaybackUiState(),
) {
    val currentSong: Song?
        get() = songs.firstOrNull { it.id.toString() == playback.currentMediaId }

    val queue: List<Song>
        get() {
            val byId = songs.associateBy { it.id.toString() }
            return playback.queueMediaIds.mapNotNull { byId[it] }
        }
}

/**
 * Conecta [MediaRepository] (biblioteca) y [PlaybackConnection] (estado de reproducción,
 * gobernado por el Service, no por esta ViewModel) en un único StateFlow para la UI.
 */
class MusicViewModel(
    private val repository: MediaRepository,
    applicationContext: Context,
) : ViewModel() {

    private val playbackConnection = PlaybackConnection(applicationContext)
    private val isLoadingLibrary = MutableStateFlow(true)

    val uiState: StateFlow<MusicUiState> = combine(
        repository.songs,
        playbackConnection.state,
        isLoadingLibrary,
    ) { songs, playback, loading ->
        MusicUiState(songs = songs, isLoadingLibrary = loading, playback = playback)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MusicUiState())

    init {
        playbackConnection.connect()
        viewModelScope.launch {
            repository.scanLibrary()
            isLoadingLibrary.value = false
        }
    }

    fun playSong(song: Song) {
        val songs = uiState.value.songs
        val index = songs.indexOf(song).coerceAtLeast(0)
        playbackConnection.playQueue(songs, index)
    }

    fun togglePlayPause() = playbackConnection.togglePlayPause()

    fun seekTo(positionMs: Long) = playbackConnection.seekTo(positionMs)

    fun skipNext() = playbackConnection.skipNext()

    fun skipPrevious() = playbackConnection.skipPrevious()

    fun toggleShuffle() = playbackConnection.toggleShuffle()

    fun cycleRepeatMode() = playbackConnection.cycleRepeatMode()

    fun moveQueueItem(from: Int, to: Int) = playbackConnection.moveQueueItem(from, to)

    fun removeFromQueue(index: Int) = playbackConnection.removeQueueItem(index)

    fun playQueueItem(index: Int) = playbackConnection.playQueueItem(index)

    override fun onCleared() {
        playbackConnection.release()
        super.onCleared()
    }
}
