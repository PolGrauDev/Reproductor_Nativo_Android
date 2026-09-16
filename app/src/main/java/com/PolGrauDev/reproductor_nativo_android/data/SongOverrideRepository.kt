package com.PolGrauDev.reproductor_nativo_android.data

import com.PolGrauDev.reproductor_nativo_android.data.db.SongOverrideDao
import com.PolGrauDev.reproductor_nativo_android.data.db.SongOverrideEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Envuelve [SongOverrideDao] (Room) para guardar ediciones locales de título/artista/álbum y
 * carátula personalizada por canción. Estas ediciones no tocan el archivo de audio real ni
 * MediaStore — solo se ven dentro de esta app, igual que favoritos/playlists.
 */
class SongOverrideRepository(private val dao: SongOverrideDao) {

    val overrides: Flow<Map<Long, SongOverrideEntity>> =
        dao.observeAll().map { it.associateBy(SongOverrideEntity::songId) }

    suspend fun setTextOverride(songId: Long, title: String, artist: String, album: String) {
        val current = dao.get(songId) ?: SongOverrideEntity(songId)
        dao.upsert(current.copy(title = title, artist = artist, album = album))
    }

    suspend fun setCustomArtUpdatedAt(songId: Long, updatedAt: Long) {
        val current = dao.get(songId) ?: SongOverrideEntity(songId)
        dao.upsert(current.copy(customArtUpdatedAt = updatedAt))
    }
}
