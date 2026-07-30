package com.PolGrauDev.reproductor_nativo_android.data.model

import android.net.Uri

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val year: Int?,
    val durationMs: Long,
    val albumId: Long?,
    val contentUri: Uri,
)
