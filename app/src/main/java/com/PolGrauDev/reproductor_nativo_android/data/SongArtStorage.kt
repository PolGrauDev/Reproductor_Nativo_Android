package com.PolGrauDev.reproductor_nativo_android.data

import android.content.Context
import android.net.Uri
import coil3.SingletonImageLoader
import coil3.memory.MemoryCache
import com.PolGrauDev.reproductor_nativo_android.data.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Copia una imagen elegida por el usuario (Photo Picker) como carátula personalizada de una
 * canción, y limpia la caché de Coil para que se vea de inmediato en toda la app. */
object SongArtStorage {

    suspend fun copyPickedArt(context: Context, songId: Long, source: Uri) = withContext(Dispatchers.IO) {
        val target = AlbumArtExtractor.customArtFile(context, songId)
        target.parentFile?.mkdirs()
        context.contentResolver.openInputStream(source)?.use { input ->
            target.outputStream().use { output -> input.copyTo(output) }
        }
    }

    fun invalidateCache(context: Context, song: Song) {
        val imageLoader = SingletonImageLoader.get(context)
        val key = song.contentUri.toString()
        imageLoader.memoryCache?.remove(MemoryCache.Key(key))
        imageLoader.diskCache?.remove(key)
    }
}
