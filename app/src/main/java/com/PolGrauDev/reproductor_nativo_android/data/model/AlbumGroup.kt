package com.PolGrauDev.reproductor_nativo_android.data.model

data class AlbumGroup(
    val albumId: Long?,
    val title: String,
    val artist: String,
    val songs: List<Song>,
)

fun List<Song>.toAlbumGroups(): List<AlbumGroup> = groupBy { it.albumId }
    .map { (albumId, songs) ->
        AlbumGroup(
            albumId = albumId,
            title = songs.first().album.ifBlank { "Álbum desconocido" },
            artist = songs.first().artist.ifBlank { "Artista desconocido" },
            songs = songs,
        )
    }
    .sortedBy { it.title }
