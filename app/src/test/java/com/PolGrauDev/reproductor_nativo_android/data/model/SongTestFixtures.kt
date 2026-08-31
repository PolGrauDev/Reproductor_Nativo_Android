package com.PolGrauDev.reproductor_nativo_android.data.model

import android.net.Uri

fun testSong(
    id: Long = 1L,
    title: String = "Title",
    artist: String = "Artist",
    album: String = "Album",
    year: Int? = 2024,
    durationMs: Long = 200_000L,
    albumId: Long? = 1L,
    artistId: Long? = 1L,
    contentUri: Uri = Uri.parse("content://media/external/audio/media/$id"),
    filePath: String = "/storage/emulated/0/Music/song.mp3",
    dateAddedSec: Long = 0L,
): Song = Song(
    id = id,
    title = title,
    artist = artist,
    album = album,
    year = year,
    durationMs = durationMs,
    albumId = albumId,
    artistId = artistId,
    contentUri = contentUri,
    filePath = filePath,
    dateAddedSec = dateAddedSec,
)
