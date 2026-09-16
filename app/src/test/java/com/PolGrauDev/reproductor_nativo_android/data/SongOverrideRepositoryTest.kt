package com.PolGrauDev.reproductor_nativo_android.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.PolGrauDev.reproductor_nativo_android.data.db.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SongOverrideRepositoryTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: SongOverrideRepository

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).allowMainThreadQueries().build()
        repository = SongOverrideRepository(database.songOverrideDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `setTextOverride is visible in overrides keyed by songId`() = runTest {
        repository.setTextOverride(songId = 1L, title = "Nuevo título", artist = "Nuevo artista", album = "Nuevo álbum")

        val override = repository.overrides.first()[1L]
        assertEquals("Nuevo título", override?.title)
        assertEquals("Nuevo artista", override?.artist)
        assertEquals("Nuevo álbum", override?.album)
        assertNull(override?.customArtUpdatedAt)
    }

    @Test
    fun `setCustomArtUpdatedAt does not clobber a previously saved text override`() = runTest {
        repository.setTextOverride(songId = 1L, title = "Título", artist = "Artista", album = "Álbum")

        repository.setCustomArtUpdatedAt(songId = 1L, updatedAt = 12345L)

        val override = repository.overrides.first()[1L]
        assertEquals("Título", override?.title)
        assertEquals(12345L, override?.customArtUpdatedAt)
    }

    @Test
    fun `setTextOverride does not clobber a previously saved custom art timestamp`() = runTest {
        repository.setCustomArtUpdatedAt(songId = 1L, updatedAt = 12345L)

        repository.setTextOverride(songId = 1L, title = "Título", artist = "Artista", album = "Álbum")

        val override = repository.overrides.first()[1L]
        assertEquals(12345L, override?.customArtUpdatedAt)
        assertEquals("Título", override?.title)
    }
}
