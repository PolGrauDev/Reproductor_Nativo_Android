package com.PolGrauDev.reproductor_nativo_android.player

import androidx.media3.common.Player
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PlaybackServiceLogicTest {

    private lateinit var service: PlaybackService

    @Before
    fun setUp() {
        service = PlaybackService()
    }

    @Test
    fun `nextRepeatMode cycles from OFF to ALL`() {
        val next = service.nextRepeatMode(Player.REPEAT_MODE_OFF)

        assertEquals(Player.REPEAT_MODE_ALL, next)
    }

    @Test
    fun `nextRepeatMode cycles from ALL to ONE`() {
        val next = service.nextRepeatMode(Player.REPEAT_MODE_ALL)

        assertEquals(Player.REPEAT_MODE_ONE, next)
    }

    @Test
    fun `nextRepeatMode cycles from ONE back to OFF`() {
        val next = service.nextRepeatMode(Player.REPEAT_MODE_ONE)

        assertEquals(Player.REPEAT_MODE_OFF, next)
    }

    @Test
    fun `nextRepeatMode handles any other mode as OFF and cycles to ALL`() {
        // Any mode other than REPEAT_MODE_OFF or REPEAT_MODE_ALL should cycle back to OFF
        val next = service.nextRepeatMode(999)

        assertEquals(Player.REPEAT_MODE_OFF, next)
    }
}
