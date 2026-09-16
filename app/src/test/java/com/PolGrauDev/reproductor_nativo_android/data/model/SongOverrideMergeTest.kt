package com.PolGrauDev.reproductor_nativo_android.data.model

import com.PolGrauDev.reproductor_nativo_android.data.db.SongOverrideEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SongOverrideMergeTest {

    @Test
    fun `withOverrides returns the same list instance when there are no overrides`() {
        val songs = listOf(testSong(id = 1L))

        val result = songs.withOverrides(emptyMap())

        assertSame(songs, result)
    }

    @Test
    fun `withOverrides replaces title, artist and album for the matching song only`() {
        val songs = listOf(
            testSong(id = 1L, title = "Original 1", artist = "Artista 1", album = "Álbum 1"),
            testSong(id = 2L, title = "Original 2", artist = "Artista 2", album = "Álbum 2"),
        )
        val overrides = mapOf(
            1L to SongOverrideEntity(songId = 1L, title = "Editado", artist = "Artista editado", album = "Álbum editado"),
        )

        val result = songs.withOverrides(overrides)

        assertEquals("Editado", result[0].title)
        assertEquals("Artista editado", result[0].artist)
        assertEquals("Álbum editado", result[0].album)
        assertEquals("Original 2", result[1].title)
    }

    @Test
    fun `withOverrides falls back to the original field when an override field is null`() {
        val songs = listOf(testSong(id = 1L, title = "Original", artist = "Artista original", album = "Álbum original"))
        val overrides = mapOf(1L to SongOverrideEntity(songId = 1L, title = "Solo título editado"))

        val result = songs.withOverrides(overrides)

        assertEquals("Solo título editado", result[0].title)
        assertEquals("Artista original", result[0].artist)
        assertEquals("Álbum original", result[0].album)
    }

    @Test
    fun `withOverrides does not change albumId or artistId`() {
        val songs = listOf(testSong(id = 1L, albumId = 42L, artistId = 7L))
        val overrides = mapOf(1L to SongOverrideEntity(songId = 1L, album = "Álbum editado", artist = "Artista editado"))

        val result = songs.withOverrides(overrides)

        assertEquals(42L, result[0].albumId)
        assertEquals(7L, result[0].artistId)
    }
}
