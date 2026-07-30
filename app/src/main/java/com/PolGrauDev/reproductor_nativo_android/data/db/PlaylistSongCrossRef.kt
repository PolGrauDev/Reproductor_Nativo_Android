package com.PolGrauDev.reproductor_nativo_android.data.db

import androidx.room.Entity

@Entity(tableName = "playlist_song_cross_ref", primaryKeys = ["playlistId", "songId"])
data class PlaylistSongCrossRef(
    val playlistId: Long,
    val songId: Long,
    val position: Int,
)
