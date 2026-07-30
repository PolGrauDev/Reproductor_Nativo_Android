package com.PolGrauDev.reproductor_nativo_android.player

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.PolGrauDev.reproductor_nativo_android.data.model.Song

/**
 * Metadatos ligeros (sin carátula). La carátula se inyecta bajo demanda por
 * [PlaybackConnection] solo para la pista que está sonando, para no pagar el coste de
 * [android.media.MediaMetadataRetriever] en toda la cola de golpe.
 */
fun Song.toMediaItem(): MediaItem {
    val metadata = MediaMetadata.Builder()
        .setTitle(title)
        .setArtist(artist)
        .setAlbumTitle(album)
        .apply { year?.let { setReleaseYear(it) } }
        .setIsBrowsable(false)
        .setIsPlayable(true)
        .build()

    return MediaItem.Builder()
        .setMediaId(id.toString())
        .setUri(contentUri)
        .setMediaMetadata(metadata)
        .build()
}
