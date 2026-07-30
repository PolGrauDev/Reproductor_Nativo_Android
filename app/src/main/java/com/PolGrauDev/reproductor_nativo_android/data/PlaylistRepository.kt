package com.PolGrauDev.reproductor_nativo_android.data

import com.PolGrauDev.reproductor_nativo_android.data.db.FavoriteDao
import com.PolGrauDev.reproductor_nativo_android.data.db.FavoriteEntity
import com.PolGrauDev.reproductor_nativo_android.data.db.PlaylistDao
import com.PolGrauDev.reproductor_nativo_android.data.db.PlaylistEntity
import com.PolGrauDev.reproductor_nativo_android.data.db.PlaylistSongCrossRef
import com.PolGrauDev.reproductor_nativo_android.data.model.PlaylistSummary
import kotlinx.coroutines.flow.Flow

/**
 * Envuelve [FavoriteDao]/[PlaylistDao] (Room) para que el resto de la app no dependa de Room
 * directamente. Solo guarda `songId`s — los metadatos de cada canción se resuelven en memoria
 * contra [MediaRepository.songs], igual que ya hace la cola de reproducción.
 */
class PlaylistRepository(
    private val favoriteDao: FavoriteDao,
    private val playlistDao: PlaylistDao,
) {
    val favoriteSongIds: Flow<List<Long>> = favoriteDao.observeFavoriteIds()
    val playlists: Flow<List<PlaylistSummary>> = playlistDao.observePlaylistSummaries()

    suspend fun setFavorite(songId: Long, isFavorite: Boolean) {
        if (isFavorite) favoriteDao.add(FavoriteEntity(songId)) else favoriteDao.remove(songId)
    }

    suspend fun createPlaylist(name: String): Long = playlistDao.insert(PlaylistEntity(name = name))

    suspend fun deletePlaylist(playlistId: Long) = playlistDao.delete(playlistId)

    suspend fun renamePlaylist(playlistId: Long, name: String) = playlistDao.rename(playlistId, name)

    fun observeSongIds(playlistId: Long): Flow<List<Long>> = playlistDao.observeSongIds(playlistId)

    suspend fun addSongToPlaylist(playlistId: Long, songId: Long) {
        val position = playlistDao.nextPosition(playlistId)
        playlistDao.addSong(PlaylistSongCrossRef(playlistId, songId, position))
    }

    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long) =
        playlistDao.removeSong(playlistId, songId)

    /** Reasigna las posiciones tras mover una canción de [from] a [to] dentro de [songIds]. */
    suspend fun moveSong(playlistId: Long, songIds: List<Long>, from: Int, to: Int) {
        if (from == to || from !in songIds.indices || to !in songIds.indices) return
        val reordered = songIds.toMutableList().apply { add(to, removeAt(from)) }
        reordered.forEachIndexed { index, songId -> playlistDao.updatePosition(playlistId, songId, index) }
    }
}
