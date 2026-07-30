package com.PolGrauDev.reproductor_nativo_android.data

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.PolGrauDev.reproductor_nativo_android.data.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

/**
 * Fuente de verdad de la biblioteca de canciones del dispositivo.
 * Escanea [MediaStore.Audio.Media] una vez y cachea el resultado en memoria.
 */
class MediaRepository(private val context: Context) {

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs.asStateFlow()

    suspend fun scanLibrary(): List<Song> = withContext(Dispatchers.IO) {
        val result = queryMediaStore()
        _songs.value = result
        result
    }

    private fun queryMediaStore(): List<Song> {
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST_ID,
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        val songs = mutableListOf<Song>()
        context.contentResolver.query(collection, projection, selection, null, sortOrder)
            ?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val yearCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
                val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                val artistIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idCol)
                    val year = cursor.getInt(yearCol).takeIf { it > 0 }
                    songs += Song(
                        id = id,
                        title = cursor.getString(titleCol) ?: "",
                        artist = cursor.getString(artistCol) ?: "",
                        album = cursor.getString(albumCol) ?: "",
                        year = year,
                        durationMs = cursor.getLong(durationCol),
                        albumId = cursor.getLong(albumIdCol).takeIf { it > 0 },
                        artistId = cursor.getLong(artistIdCol).takeIf { it > 0 },
                        contentUri = ContentUris.withAppendedId(collection, id),
                    )
                }
            }
        return songs
    }
}
