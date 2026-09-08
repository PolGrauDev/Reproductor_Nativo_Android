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
/**
 * Ninguna carátula embebida se muestra a más de esto en la app (el arte grande de Now Playing
 * ocupa como mucho ~70% del ancho de pantalla, las miniaturas de lista 40-50dp) — decodificar a
 * resolución completa una portada de p.ej. 3000×3000 para una miniatura desperdicia memoria sin
 * ninguna ganancia visual.
 */
private const val MAX_DECODED_DIMENSION_PX = 720

class AlbumArtFetcher(
    private val request: AlbumArtRequest,
    private val options: Options,
) : Fetcher {

    override suspend fun fetch(): FetchResult? {
        val bytes = AlbumArtExtractor.extractEmbeddedArt(options.context, request.songUri) ?: return null

        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
        val sampleSize = calculateInSampleSize(bounds.outWidth, bounds.outHeight, MAX_DECODED_DIMENSION_PX)

        val decodeOptions = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, decodeOptions) ?: return null
        val drawable = BitmapDrawable(options.context.resources, bitmap)
        return ImageFetchResult(
            image = drawable.asImage(),
            isSampled = sampleSize > 1,
            dataSource = DataSource.DISK,
        )
    }

    class Factory : Fetcher.Factory<AlbumArtRequest> {
        override fun create(data: AlbumArtRequest, options: Options, imageLoader: ImageLoader): Fetcher =
            AlbumArtFetcher(data, options)
    }
}

/** Potencia de 2 más grande que deja ambas dimensiones por encima de [maxDimension] tras dividir. */
internal fun calculateInSampleSize(width: Int, height: Int, maxDimension: Int): Int {
    if (width <= 0 || height <= 0) return 1
    var sampleSize = 1
    var w = width
    var h = height
    while (w / 2 >= maxDimension && h / 2 >= maxDimension) {
        w /= 2
        h /= 2
        sampleSize *= 2
    }
    return sampleSize
}
