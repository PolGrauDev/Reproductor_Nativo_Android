package com.PolGrauDev.reproductor_nativo_android.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.PolGrauDev.reproductor_nativo_android.data.MediaRepository
import com.PolGrauDev.reproductor_nativo_android.data.PlaylistRepository
import com.PolGrauDev.reproductor_nativo_android.data.model.AlbumGroup
import com.PolGrauDev.reproductor_nativo_android.data.model.ArtistGroup
import com.PolGrauDev.reproductor_nativo_android.data.model.FolderGroup
import com.PolGrauDev.reproductor_nativo_android.data.model.PlaylistSummary
import com.PolGrauDev.reproductor_nativo_android.data.model.Song
import com.PolGrauDev.reproductor_nativo_android.data.model.SortOrder
import com.PolGrauDev.reproductor_nativo_android.data.model.toAlbumGroups
import com.PolGrauDev.reproductor_nativo_android.data.model.toArtistGroups
import com.PolGrauDev.reproductor_nativo_android.data.model.toFolderGroups
import com.PolGrauDev.reproductor_nativo_android.player.PlaybackConnection
import com.PolGrauDev.reproductor_nativo_android.player.PlaybackUiState
import kotlinx.coroutines.flow.Flow
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
    val searchQuery: String = "",
    val favoriteSongIds: Set<Long> = emptySet(),
    val playlists: List<PlaylistSummary> = emptyList(),
    val sortOrder: SortOrder = SortOrder.TITLE,
) {
    val currentSong: Song?
        get() = songs.firstOrNull { it.id.toString() == playback.currentMediaId }

    val queue: List<Song>
        get() {
            val byId = songs.associateBy { it.id.toString() }
            return playback.queueMediaIds.mapNotNull { byId[it] }
        }

    val filteredSongs: List<Song>
        get() {
            val matching = if (searchQuery.isBlank()) {
                songs
            } else {
                songs.filter {
                    it.title.contains(searchQuery, ignoreCase = true) ||
                        it.artist.contains(searchQuery, ignoreCase = true) ||
                        it.album.contains(searchQuery, ignoreCase = true)
                }
            }
            return when (sortOrder) {
                SortOrder.TITLE -> matching.sortedBy { it.title }
                SortOrder.DATE_ADDED -> matching.sortedByDescending { it.dateAddedSec }
                SortOrder.DURATION -> matching.sortedByDescending { it.durationMs }
            }
        }

    val albums: List<AlbumGroup>
        get() = filteredSongs.toAlbumGroups()

    val artists: List<ArtistGroup>
        get() = filteredSongs.toArtistGroups()

    val folders: List<FolderGroup>
        get() = filteredSongs.toFolderGroups()

    val favoriteSongs: List<Song>
        get() = songs.filter { it.id in favoriteSongIds }
}

private data class LibraryExtras(
    val favoriteSongIds: Set<Long>,
    val playlists: List<PlaylistSummary>,
    val sortOrder: SortOrder,
)

/**
 * Conecta [MediaRepository] (biblioteca) y [PlaybackConnection] (estado de reproducción,
 * gobernado por el Service, no por esta ViewModel) en un único StateFlow para la UI.
 */
class MusicViewModel(
    private val repository: MediaRepository,
    private val playlistRepository: PlaylistRepository,
    applicationContext: Context,
) : ViewModel() {

    private val playbackConnection = PlaybackConnection(applicationContext)
    private val isLoadingLibrary = MutableStateFlow(true)
    private val searchQuery = MutableStateFlow("")
    private val sortOrder = MutableStateFlow(SortOrder.TITLE)

    private val libraryExtras = combine(
        playlistRepository.favoriteSongIds,
        playlistRepository.playlists,
        sortOrder,
    ) { favoriteIds, playlists, order -> LibraryExtras(favoriteIds.toSet(), playlists, order) }

    val uiState: StateFlow<MusicUiState> = combine(
        repository.songs,
        playbackConnection.state,
        isLoadingLibrary,
        searchQuery,
        libraryExtras,
    ) { songs, playback, loading, query, extras ->
        MusicUiState(
            songs = songs,
            isLoadingLibrary = loading,
            playback = playback,
            searchQuery = query,
            favoriteSongIds = extras.favoriteSongIds,
            playlists = extras.playlists,
            sortOrder = extras.sortOrder,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MusicUiState())

    init {
        playbackConnection.connect()
        viewModelScope.launch {
            repository.scanLibrary()
            isLoadingLibrary.value = false
        }
    }

    fun playSong(song: Song, fromList: List<Song> = uiState.value.songs) {
        val index = fromList.indexOf(song).coerceAtLeast(0)
        playbackConnection.playQueue(fromList, index)
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun setSortOrder(order: SortOrder) {
        sortOrder.value = order
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

    fun clearPlaybackError() = playbackConnection.clearError()

    fun refreshLibrary() {
        viewModelScope.launch { repository.scanLibrary() }
    }

    fun toggleFavorite(songId: Long) {
        val isFavorite = songId in uiState.value.favoriteSongIds
        viewModelScope.launch { playlistRepository.setFavorite(songId, !isFavorite) }
    }

    fun createPlaylist(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch { playlistRepository.createPlaylist(name.trim()) }
    }

    /** Crea la playlist y añade [songId] de una vez — usado desde el diálogo "Añadir a playlist". */
    fun createPlaylistAndAddSong(name: String, songId: Long) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val playlistId = playlistRepository.createPlaylist(name.trim())
            playlistRepository.addSongToPlaylist(playlistId, songId)
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch { playlistRepository.deletePlaylist(playlistId) }
    }

    fun renamePlaylist(playlistId: Long, name: String) {
        if (name.isBlank()) return
        viewModelScope.launch { playlistRepository.renamePlaylist(playlistId, name.trim()) }
    }

    fun addSongToPlaylist(playlistId: Long, songId: Long) {
        viewModelScope.launch { playlistRepository.addSongToPlaylist(playlistId, songId) }
    }

    fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
        viewModelScope.launch { playlistRepository.removeSongFromPlaylist(playlistId, songId) }
    }

    fun moveSongInPlaylist(playlistId: Long, songIds: List<Long>, from: Int, to: Int) {
        viewModelScope.launch { playlistRepository.moveSong(playlistId, songIds, from, to) }
    }

    /**
     * Colectado directamente por la pantalla de detalle de playlist; no vive en [MusicUiState]
     * porque está parametrizado por playlist.
     */
    fun playlistSongsFlow(playlistId: Long): Flow<List<Song>> = combine(
        repository.songs,
        playlistRepository.observeSongIds(playlistId),
    ) { songs, songIds ->
        val byId = songs.associateBy { it.id }
        songIds.mapNotNull { byId[it] }
    }

    override fun onCleared() {
        playbackConnection.release()
        super.onCleared()
    }
}
