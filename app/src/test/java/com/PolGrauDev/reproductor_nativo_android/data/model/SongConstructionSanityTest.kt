package com.PolGrauDev.reproductor_nativo_android.data.model

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SongConstructionSanityTest {

    @Test
    fun `testSong builds with a Uri field without throwing`() {
        val song = testSong(id = 42L, title = "Sanity")

        assertEquals(42L, song.id)
        assertEquals("Sanity", song.title)
    }
}
