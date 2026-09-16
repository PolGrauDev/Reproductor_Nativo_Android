package com.PolGrauDev.reproductor_nativo_android.data

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import java.io.File

/**
 * Extrae la carátula embebida de un archivo de audio vía [MediaMetadataRetriever].
 * Compartido entre el pipeline de imágenes (Coil) y la sesión de reproducción (Media3),
 * para no abrir el retriever dos veces por canción.
 */
object AlbumArtExtractor {

    fun extractEmbeddedArt(context: Context, uri: Uri): ByteArray? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(context, uri)
            retriever.embeddedPicture
        } catch (e: Exception) {
            Log.w("AlbumArtExtractor", "No se pudo extraer la carátula de $uri", e)
            null
        } finally {
            retriever.release()
        }
    }

    fun customArtFile(context: Context, songId: Long): File =
        File(context.filesDir, "song_art/$songId.jpg")

    fun extractCustomArt(context: Context, songId: Long): ByteArray? {
        val file = customArtFile(context, songId)
        return if (file.exists()) file.readBytes() else null
    }
}
