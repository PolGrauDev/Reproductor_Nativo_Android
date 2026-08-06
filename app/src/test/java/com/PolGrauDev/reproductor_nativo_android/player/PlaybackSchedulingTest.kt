package com.PolGrauDev.reproductor_nativo_android.player

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PlaybackSchedulingTest {

    @Test
    fun `sleepTimerRemainingMs returns full remaining time before the deadline`() {
        val remaining = sleepTimerRemainingMs(endAtElapsedRealtime = 10_000L, nowElapsedRealtime = 4_000L)

        assertEquals(6_000L, remaining)
    }

    @Test
    fun `sleepTimerRemainingMs clamps to zero once the deadline has passed`() {
        val remaining = sleepTimerRemainingMs(endAtElapsedRealtime = 10_000L, nowElapsedRealtime = 15_000L)

        assertEquals(0L, remaining)
    }

    @Test
    fun `fadeOutDelayMs is null when track duration is not known yet`() {
        val delay = fadeOutDelayMs(durationMs = 0L, positionMs = 0L, fadeDurationMs = 1_000)

        assertNull(delay)
    }

    @Test
    fun `fadeOutDelayMs is null when fade is disabled`() {
        val delay = fadeOutDelayMs(durationMs = 180_000L, positionMs = 0L, fadeDurationMs = 0)

        assertNull(delay)
    }

    @Test
    fun `fadeOutDelayMs schedules the fade to start fadeDurationMs before the end of the track`() {
        val delay = fadeOutDelayMs(durationMs = 180_000L, positionMs = 0L, fadeDurationMs = 2_000)

        assertEquals(178_000L, delay)
    }

    @Test
    fun `fadeOutDelayMs clamps to zero when already inside the fade window`() {
        val delay = fadeOutDelayMs(durationMs = 180_000L, positionMs = 179_500L, fadeDurationMs = 2_000)

        assertEquals(0L, delay)
    }
}
