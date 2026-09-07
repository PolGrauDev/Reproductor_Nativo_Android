package com.PolGrauDev.reproductor_nativo_android.data

import android.content.ContentUris
import android.database.MatrixCursor
import android.provider.MediaStore
import com.PolGrauDev.reproductor_nativo_android.data.model.Song
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MediaRepositoryTest {

    @Test
    fun `songFromCursor maps albumId and artistId to null when they are zero`() {
        val columns = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DATE_ADDED,
        )
        val cursor = MatrixCursor(columns)
        val rowData: Array<Any?> = arrayOf(
            123L,                      // _ID
            "Song Title",               // TITLE
            "Artist Name",              // ARTIST
            "Album Name",               // ALBUM
            2020,                       // YEAR
            180000L,                    // DURATION
            0L,                         // ALBUM_ID (null because it's 0)
            0L,                         // ARTIST_ID (null because it's 0)
            "/storage/music/song.mp3",  // DATA
            1609459200L,                // DATE_ADDED
        )
        cursor.addRow(rowData)
        cursor.moveToFirst()

        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val song = songFromCursor(
            cursor = cursor,
            collection = collection,
            idCol = 0,
            titleCol = 1,
            artistCol = 2,
            albumCol = 3,
            yearCol = 4,
            durationCol = 5,
            albumIdCol = 6,
            artistIdCol = 7,
            dataCol = 8,
            dateAddedCol = 9,
        )

        assertNull("albumId should be null when cursor value is 0", song.albumId)
        assertNull("artistId should be null when cursor value is 0", song.artistId)
        assertEquals(123L, song.id)
        assertEquals("Song Title", song.title)
        assertEquals("Artist Name", song.artist)
        assertEquals("Album Name", song.album)
        assertEquals(2020, song.year)
        assertEquals(180000L, song.durationMs)
        assertEquals("/storage/music/song.mp3", song.filePath)
        assertEquals(1609459200L, song.dateAddedSec)
        assertEquals(ContentUris.withAppendedId(collection, 123L), song.contentUri)
    }

    @Test
    fun `songFromCursor maps year to null when it is zero or negative`() {
        val columns = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DATE_ADDED,
        )
        val cursor = MatrixCursor(columns)
        val rowData: Array<Any?> = arrayOf(
            456L,                       // _ID
            "Another Song",             // TITLE
            "Another Artist",           // ARTIST
            "Another Album",            // ALBUM
            0,                          // YEAR (null because it's 0)
            240000L,                    // DURATION
            1L,                         // ALBUM_ID
            2L,                         // ARTIST_ID
            "/storage/music/another.mp3", // DATA
            1609545600L,                // DATE_ADDED
        )
        cursor.addRow(rowData)
        cursor.moveToFirst()

        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val song = songFromCursor(
            cursor = cursor,
            collection = collection,
            idCol = 0,
            titleCol = 1,
            artistCol = 2,
            albumCol = 3,
            yearCol = 4,
            durationCol = 5,
            albumIdCol = 6,
            artistIdCol = 7,
            dataCol = 8,
            dateAddedCol = 9,
        )

        assertNull("year should be null when cursor value is 0", song.year)
        assertEquals(456L, song.id)
        assertEquals("Another Song", song.title)
        assertEquals("Another Artist", song.artist)
        assertEquals("Another Album", song.album)
        assertEquals(1L, song.albumId)
        assertEquals(2L, song.artistId)
        assertEquals(240000L, song.durationMs)
        assertEquals("/storage/music/another.mp3", song.filePath)
        assertEquals(1609545600L, song.dateAddedSec)
        assertEquals(ContentUris.withAppendedId(collection, 456L), song.contentUri)
    }

    @Test
    fun `songFromCursor correctly maps a fully populated row with all non-null values`() {
        val columns = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DATE_ADDED,
        )
        val cursor = MatrixCursor(columns)
        val rowData: Array<Any?> = arrayOf(
            789L,                       // _ID
            "Complete Song",            // TITLE
            "Complete Artist",          // ARTIST
            "Complete Album",           // ALBUM
            2023,                       // YEAR
            195000L,                    // DURATION
            42L,                        // ALBUM_ID
            99L,                        // ARTIST_ID
            "/storage/music/complete.mp3", // DATA
            1609632000L,                // DATE_ADDED
        )
        cursor.addRow(rowData)
        cursor.moveToFirst()

        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val song = songFromCursor(
            cursor = cursor,
            collection = collection,
            idCol = 0,
            titleCol = 1,
            artistCol = 2,
            albumCol = 3,
            yearCol = 4,
            durationCol = 5,
            albumIdCol = 6,
            artistIdCol = 7,
            dataCol = 8,
            dateAddedCol = 9,
        )

        assertEquals(789L, song.id)
        assertEquals("Complete Song", song.title)
        assertEquals("Complete Artist", song.artist)
        assertEquals("Complete Album", song.album)
        assertEquals(2023, song.year)
        assertEquals(195000L, song.durationMs)
        assertEquals(42L, song.albumId)
        assertEquals(99L, song.artistId)
        assertEquals("/storage/music/complete.mp3", song.filePath)
        assertEquals(1609632000L, song.dateAddedSec)
        assertEquals(ContentUris.withAppendedId(collection, 789L), song.contentUri)
    }
}
