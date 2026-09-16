package com.PolGrauDev.reproductor_nativo_android.data.model

import com.PolGrauDev.reproductor_nativo_android.data.db.SongOverrideEntity

fun List<Song>.withOverrides(overrides: Map<Long, SongOverrideEntity>): List<Song> {
    if (overrides.isEmpty()) return this
    return map { song ->
        val override = overrides[song.id] ?: return@map song
        song.copy(
            title = override.title ?: song.title,
            artist = override.artist ?: song.artist,
            album = override.album ?: song.album,
        )
    }
}
