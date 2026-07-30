package com.PolGrauDev.reproductor_nativo_android.data.model

data class ArtistGroup(
    val artistId: Long?,
    val name: String,
    val songs: List<Song>,
)

fun List<Song>.toArtistGroups(): List<ArtistGroup> = groupBy { it.artistId }
    .map { (artistId, songs) ->
        ArtistGroup(
            artistId = artistId,
            name = songs.first().artist.ifBlank { "Artista desconocido" },
            songs = songs,
        )
    }
    .sortedBy { it.name }
