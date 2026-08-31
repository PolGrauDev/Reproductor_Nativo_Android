package com.PolGrauDev.reproductor_nativo_android.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.PolGrauDev.reproductor_nativo_android.data.db.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PlaylistRepositoryTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: PlaylistRepository

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).allowMainThreadQueries().build()
        repository = PlaylistRepository(database.favoriteDao(), database.playlistDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `setFavorite adds and removes a song from favoriteSongIds`() = runTest {
        repository.setFavorite(songId = 1L, isFavorite = true)

        assertEquals(listOf(1L), repository.favoriteSongIds.first())

        repository.setFavorite(songId = 1L, isFavorite = false)

        assertTrue(repository.favoriteSongIds.first().isEmpty())
    }

    @Test
    fun `createPlaylist creates a playlist visible in playlists with songCount 0`() = runTest {
        val playlistId = repository.createPlaylist("Favoritas del verano")

        val summary = repository.playlists.first().single { it.id == playlistId }
        assertEquals("Favoritas del verano", summary.name)
        assertEquals(0, summary.songCount)
    }

    @Test
    fun `renamePlaylist updates the name`() = runTest {
        val playlistId = repository.createPlaylist("Nombre original")

        repository.renamePlaylist(playlistId, "Nombre nuevo")

        val summary = repository.playlists.first().single { it.id == playlistId }
        assertEquals("Nombre nuevo", summary.name)
    }

    @Test
    fun `deletePlaylist removes it from playlists`() = runTest {
        val playlistId = repository.createPlaylist("Temporal")

        repository.deletePlaylist(playlistId)

        assertTrue(repository.playlists.first().none { it.id == playlistId })
    }

    @Test
    fun `addSongToPlaylist assigns increasing positions and observeSongIds respects order`() = runTest {
        val playlistId = repository.createPlaylist("Orden")

        repository.addSongToPlaylist(playlistId, songId = 10L)
        repository.addSongToPlaylist(playlistId, songId = 20L)
        repository.addSongToPlaylist(playlistId, songId = 30L)

        assertEquals(listOf(10L, 20L, 30L), repository.observeSongIds(playlistId).first())
    }

    @Test
    fun `removeSongFromPlaylist only removes that song, the rest keep their order`() = runTest {
        val playlistId = repository.createPlaylist("Orden")
        repository.addSongToPlaylist(playlistId, songId = 10L)
        repository.addSongToPlaylist(playlistId, songId = 20L)
        repository.addSongToPlaylist(playlistId, songId = 30L)

        repository.removeSongFromPlaylist(playlistId, songId = 20L)

        assertEquals(listOf(10L, 30L), repository.observeSongIds(playlistId).first())
    }

    @Test
    fun `moveSong reorders positions correctly when moving a song`() = runTest {
        val playlistId = repository.createPlaylist("Orden")
        repository.addSongToPlaylist(playlistId, songId = 10L)
        repository.addSongToPlaylist(playlistId, songId = 20L)
        repository.addSongToPlaylist(playlistId, songId = 30L)
        val songIds = repository.observeSongIds(playlistId).first()

        repository.moveSong(playlistId, songIds, from = 0, to = 2)

        assertEquals(listOf(20L, 30L, 10L), repository.observeSongIds(playlistId).first())
    }

    @Test
    fun `moveSong does nothing when from equals to or the index is out of range`() = runTest {
        val playlistId = repository.createPlaylist("Orden")
        repository.addSongToPlaylist(playlistId, songId = 10L)
        repository.addSongToPlaylist(playlistId, songId = 20L)
        val songIds = repository.observeSongIds(playlistId).first()

        repository.moveSong(playlistId, songIds, from = 0, to = 0)
        repository.moveSong(playlistId, songIds, from = 0, to = 5)
        repository.moveSong(playlistId, songIds, from = -1, to = 1)

        assertEquals(listOf(10L, 20L), repository.observeSongIds(playlistId).first())
    }
}
