package com.PolGrauDev.reproductor_nativo_android.viewmodel

import com.PolGrauDev.reproductor_nativo_android.data.model.Song
import com.PolGrauDev.reproductor_nativo_android.data.model.SortOrder
import com.PolGrauDev.reproductor_nativo_android.data.model.testSong
import com.PolGrauDev.reproductor_nativo_android.player.PlaybackUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MusicUiStateTest {

    // ==================== filteredSongs tests ====================

    @Test
    fun `filteredSongs returns all songs when searchQuery is blank`() {
        val song1 = testSong(id = 1L, title = "Song One")
        val song2 = testSong(id = 2L, title = "Song Two")
        val state = MusicUiState(
            songs = listOf(song1, song2),
            searchQuery = "",
            sortOrder = SortOrder.TITLE,
        )

        assertEquals(listOf(song1, song2), state.filteredSongs)
    }

    @Test
    fun `filteredSongs filters by title case-insensitive`() {
        val song1 = testSong(id = 1L, title = "Bohemian Rhapsody")
        val song2 = testSong(id = 2L, title = "Stairway to Heaven")
        val state = MusicUiState(
            songs = listOf(song1, song2),
            searchQuery = "bohemian",
            sortOrder = SortOrder.TITLE,
        )

        assertEquals(listOf(song1), state.filteredSongs)
    }

    @Test
    fun `filteredSongs filters by artist case-insensitive`() {
        val song1 = testSong(id = 1L, artist = "The Beatles")
        val song2 = testSong(id = 2L, artist = "Pink Floyd")
        val state = MusicUiState(
            songs = listOf(song1, song2),
            searchQuery = "beatles",
            sortOrder = SortOrder.TITLE,
        )

        assertEquals(listOf(song1), state.filteredSongs)
    }

    @Test
    fun `filteredSongs filters by album case-insensitive`() {
        val song1 = testSong(id = 1L, album = "Dark Side of the Moon")
        val song2 = testSong(id = 2L, album = "The Wall")
        val state = MusicUiState(
            songs = listOf(song1, song2),
            searchQuery = "dark",
            sortOrder = SortOrder.TITLE,
        )

        assertEquals(listOf(song1), state.filteredSongs)
    }

    @Test
    fun `filteredSongs filters by partial match`() {
        val song1 = testSong(id = 1L, title = "Something")
        val song2 = testSong(id = 2L, title = "Somewhere")
        val state = MusicUiState(
            songs = listOf(song1, song2),
            searchQuery = "some",
            sortOrder = SortOrder.TITLE,
        )

        assertEquals(listOf(song1, song2), state.filteredSongs)
    }

    @Test
    fun `filteredSongs applies TITLE sort order`() {
        val song1 = testSong(id = 1L, title = "Zebra")
        val song2 = testSong(id = 2L, title = "Apple")
        val song3 = testSong(id = 3L, title = "Banana")
        val state = MusicUiState(
            songs = listOf(song1, song2, song3),
            searchQuery = "",
            sortOrder = SortOrder.TITLE,
        )

        assertEquals(
            listOf(song2, song3, song1),  // Apple, Banana, Zebra
            state.filteredSongs,
        )
    }

    @Test
    fun `filteredSongs applies DATE_ADDED sort order descending`() {
        val song1 = testSong(id = 1L, dateAddedSec = 100L)
        val song2 = testSong(id = 2L, dateAddedSec = 300L)
        val song3 = testSong(id = 3L, dateAddedSec = 200L)
        val state = MusicUiState(
            songs = listOf(song1, song2, song3),
            searchQuery = "",
            sortOrder = SortOrder.DATE_ADDED,
        )

        assertEquals(
            listOf(song2, song3, song1),  // 300, 200, 100
            state.filteredSongs,
        )
    }

    @Test
    fun `filteredSongs applies DURATION sort order descending`() {
        val song1 = testSong(id = 1L, durationMs = 100_000L)
        val song2 = testSong(id = 2L, durationMs = 300_000L)
        val song3 = testSong(id = 3L, durationMs = 200_000L)
        val state = MusicUiState(
            songs = listOf(song1, song2, song3),
            searchQuery = "",
            sortOrder = SortOrder.DURATION,
        )

        assertEquals(
            listOf(song2, song3, song1),  // 300_000, 200_000, 100_000
            state.filteredSongs,
        )
    }

    @Test
    fun `filteredSongs combines search filter and sort order`() {
        val song1 = testSong(id = 1L, title = "Zebra Song", artist = "Artist A")
        val song2 = testSong(id = 2L, title = "Apple Song", artist = "Artist B")
        val song3 = testSong(id = 3L, title = "Banana Track", artist = "Artist C")
        val state = MusicUiState(
            songs = listOf(song1, song2, song3),
            searchQuery = "song",
            sortOrder = SortOrder.TITLE,
        )

        // Only songs with "song" in title, sorted by title
        assertEquals(
            listOf(song2, song1),  // Apple Song, Zebra Song
            state.filteredSongs,
        )
    }

    // ==================== currentSong tests ====================

    @Test
    fun `currentSong returns matching song when currentMediaId exists`() {
        val song1 = testSong(id = 1L)
        val song2 = testSong(id = 2L)
        val state = MusicUiState(
            songs = listOf(song1, song2),
            playback = PlaybackUiState(currentMediaId = "2"),
        )

        assertEquals(song2, state.currentSong)
    }

    @Test
    fun `currentSong returns null when currentMediaId is null`() {
        val song1 = testSong(id = 1L)
        val state = MusicUiState(
            songs = listOf(song1),
            playback = PlaybackUiState(currentMediaId = null),
        )

        assertNull(state.currentSong)
    }

    @Test
    fun `currentSong returns null when currentMediaId does not match any song`() {
        val song1 = testSong(id = 1L)
        val state = MusicUiState(
            songs = listOf(song1),
            playback = PlaybackUiState(currentMediaId = "999"),
        )

        assertNull(state.currentSong)
    }

    // ==================== queue tests ====================

    @Test
    fun `queue maps queueMediaIds to songs in order`() {
        val song1 = testSong(id = 1L)
        val song2 = testSong(id = 2L)
        val song3 = testSong(id = 3L)
        val state = MusicUiState(
            songs = listOf(song1, song2, song3),
            playback = PlaybackUiState(queueMediaIds = listOf("2", "1", "3")),
        )

        assertEquals(listOf(song2, song1, song3), state.queue)
    }

    @Test
    fun `queue returns empty list when queueMediaIds is empty`() {
        val song1 = testSong(id = 1L)
        val state = MusicUiState(
            songs = listOf(song1),
            playback = PlaybackUiState(queueMediaIds = emptyList()),
        )

        assertEquals(emptyList<Song>(), state.queue)
    }

    @Test
    fun `queue skips missing songs when queueMediaId no longer exists in songs`() {
        val song1 = testSong(id = 1L)
        val song3 = testSong(id = 3L)
        val state = MusicUiState(
            songs = listOf(song1, song3),
            // Queue includes song 2 which no longer exists
            playback = PlaybackUiState(queueMediaIds = listOf("1", "2", "3")),
        )

        // Song with id 2 is not in the queue result
        assertEquals(listOf(song1, song3), state.queue)
    }

    @Test
    fun `queue handles queue with all deleted songs`() {
        val song1 = testSong(id = 1L)
        val state = MusicUiState(
            songs = listOf(song1),
            // Queue references songs that don't exist
            playback = PlaybackUiState(queueMediaIds = listOf("999", "888")),
        )

        assertEquals(emptyList<Song>(), state.queue)
    }

    // ==================== favoriteSongs tests ====================

    @Test
    fun `favoriteSongs returns songs matching favoriteSongIds`() {
        val song1 = testSong(id = 1L)
        val song2 = testSong(id = 2L)
        val song3 = testSong(id = 3L)
        val state = MusicUiState(
            songs = listOf(song1, song2, song3),
            favoriteSongIds = setOf(1L, 3L),
        )

        assertEquals(listOf(song1, song3), state.favoriteSongs)
    }

    @Test
    fun `favoriteSongs returns empty list when no favorites`() {
        val song1 = testSong(id = 1L)
        val state = MusicUiState(
            songs = listOf(song1),
            favoriteSongIds = emptySet(),
        )

        assertEquals(emptyList<Song>(), state.favoriteSongs)
    }

    @Test
    fun `favoriteSongs returns empty list when favoriteSongIds references non-existent songs`() {
        val song1 = testSong(id = 1L)
        val state = MusicUiState(
            songs = listOf(song1),
            favoriteSongIds = setOf(999L, 888L),
        )

        assertEquals(emptyList<Song>(), state.favoriteSongs)
    }

    @Test
    fun `favoriteSongs maintains song order from songs list not from favoriteSongIds order`() {
        val song1 = testSong(id = 1L)
        val song2 = testSong(id = 2L)
        val song3 = testSong(id = 3L)
        val state = MusicUiState(
            songs = listOf(song1, song2, song3),
            // Favorite IDs in reverse order
            favoriteSongIds = setOf(3L, 1L),
        )

        // Result should maintain the order from songs list (1, 3), not favorites set order
        assertEquals(listOf(song1, song3), state.favoriteSongs)
    }

    // ==================== Complex scenarios ====================

    @Test
    fun `search and sort work together correctly with multiple criteria`() {
        val song1 = testSong(
            id = 1L,
            title = "Track One",
            artist = "Artist A",
            dateAddedSec = 100L,
        )
        val song2 = testSong(
            id = 2L,
            title = "Track Two",
            artist = "Artist B",
            dateAddedSec = 300L,
        )
        val song3 = testSong(
            id = 3L,
            title = "Song Three",
            artist = "Artist A",
            dateAddedSec = 200L,
        )
        val state = MusicUiState(
            songs = listOf(song1, song2, song3),
            searchQuery = "Artist A",
            sortOrder = SortOrder.DATE_ADDED,
        )

        // Songs 1 and 3 match "Artist A", sorted by date descending
        assertEquals(listOf(song3, song1), state.filteredSongs)
    }

    @Test
    fun `currentSong returns correct song even with filtered state`() {
        val song1 = testSong(id = 1L, title = "Track A")
        val song2 = testSong(id = 2L, title = "Track B")
        val state = MusicUiState(
            songs = listOf(song1, song2),
            searchQuery = "Track B",  // Only song2 in filtered results
            playback = PlaybackUiState(currentMediaId = "1"),  // But current is song1
        )

        // currentSong should still return song1, regardless of filteredSongs
        assertEquals(song1, state.currentSong)
    }
}
