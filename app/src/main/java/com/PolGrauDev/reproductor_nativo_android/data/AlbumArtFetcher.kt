package com.PolGrauDev.reproductor_nativo_android.data

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import coil3.ImageLoader
import coil3.asImage
import coil3.decode.DataSource
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.ImageFetchResult
import coil3.key.Keyer
import coil3.request.Options

/** Modelo propio para pedirle a Coil la carátula embebida de una canción por su content Uri.
 * No se usa [Uri] directamente como modelo: Coil3 mapea android.net.Uri a su propio tipo
 * interno antes de despachar a los Fetcher.Factory, así que un Fetcher.Factory<Uri> nunca
 * llega a invocarse. Con un modelo propio evitamos esa colisión. */
data class AlbumArtRequest(val songUri: Uri)

class AlbumArtKeyer : Keyer<AlbumArtRequest> {
    override fun key(data: AlbumArtRequest, options: Options): String = data.songUri.toString()
}

/**
 * Carga perezosa (por canción, con caché de Coil) de la carátula embebida vía
 * [AlbumArtExtractor], en vez de extraer el arte de toda la biblioteca por adelantado.
 */
class AlbumArtFetcher(
    private val request: AlbumArtRequest,
    private val options: Options,
) : Fetcher {

    override suspend fun fetch(): FetchResult? {
        val bytes = AlbumArtExtractor.extractEmbeddedArt(options.context, request.songUri) ?: return null
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return null
        val drawable = BitmapDrawable(options.context.resources, bitmap)
        return ImageFetchResult(
            image = drawable.asImage(),
            isSampled = false,
            dataSource = DataSource.DISK,
        )
    }

    class Factory : Fetcher.Factory<AlbumArtRequest> {
        override fun create(data: AlbumArtRequest, options: Options, imageLoader: ImageLoader): Fetcher =
            AlbumArtFetcher(data, options)
    }
}
