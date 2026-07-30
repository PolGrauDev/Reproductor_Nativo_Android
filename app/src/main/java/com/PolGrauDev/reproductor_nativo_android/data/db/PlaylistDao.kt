package com.PolGrauDev.reproductor_nativo_android.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.PolGrauDev.reproductor_nativo_android.data.model.PlaylistSummary
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Query(
        """
        SELECT p.id AS id, p.name AS name, COUNT(x.songId) AS songCount
        FROM playlists p
        LEFT JOIN playlist_song_cross_ref x ON x.playlistId = p.id
        GROUP BY p.id
        ORDER BY p.createdAt DESC
        """,
    )
    fun observePlaylistSummaries(): Flow<List<PlaylistSummary>>

    @Insert
    suspend fun insert(playlist: PlaylistEntity): Long

    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun delete(playlistId: Long)

    @Query("UPDATE playlists SET name = :name WHERE id = :playlistId")
    suspend fun rename(playlistId: Long, name: String)

    @Query(
        "SELECT songId FROM playlist_song_cross_ref WHERE playlistId = :playlistId ORDER BY position ASC",
    )
    fun observeSongIds(playlistId: Long): Flow<List<Long>>

    @Query("SELECT COALESCE(MAX(position), -1) + 1 FROM playlist_song_cross_ref WHERE playlistId = :playlistId")
    suspend fun nextPosition(playlistId: Long): Int

    @Insert
    suspend fun addSong(ref: PlaylistSongCrossRef)

    @Query("DELETE FROM playlist_song_cross_ref WHERE playlistId = :playlistId AND songId = :songId")
    suspend fun removeSong(playlistId: Long, songId: Long)

    @Query(
        "UPDATE playlist_song_cross_ref SET position = :position WHERE playlistId = :playlistId AND songId = :songId",
    )
    suspend fun updatePosition(playlistId: Long, songId: Long, position: Int)
}
