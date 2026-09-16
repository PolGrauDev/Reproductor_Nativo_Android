package com.PolGrauDev.reproductor_nativo_android.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "song_overrides")
data class SongOverrideEntity(
    @PrimaryKey val songId: Long,
    val title: String? = null,
    val artist: String? = null,
    val album: String? = null,
    val customArtUpdatedAt: Long? = null,
)
