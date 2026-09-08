package com.PolGrauDev.reproductor_nativo_android.player

import androidx.media3.common.C
import androidx.media3.common.PlaybackException
import androidx.media3.common.Timeline
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PlaybackConnectionLogicTest {

    @Test
    fun `playbackOrderIndices returns empty list for an empty timeline`() {
        val timeline = FakeShuffleTimeline(windowCount = 0, shuffledOrder = emptyList())

        val order = timeline.playbackOrderIndices(shuffleModeEnabled = true)

        assertEquals(emptyList<Int>(), order)
    }

    @Test
    fun `playbackOrderIndices returns natural order when shuffle is disabled`() {
        val timeline = FakeShuffleTimeline(windowCount = 4, shuffledOrder = listOf(2, 0, 3, 1))

        val order = timeline.playbackOrderIndices(shuffleModeEnabled = false)

        assertEquals(listOf(0, 1, 2, 3), order)
    }

    @Test
    fun `playbackOrderIndices returns the shuffled order when shuffle is enabled`() {
        val timeline = FakeShuffleTimeline(windowCount = 4, shuffledOrder = listOf(2, 0, 3, 1))

        val order = timeline.playbackOrderIndices(shuffleModeEnabled = true)

        assertEquals(listOf(2, 0, 3, 1), order)
    }

    @Test
    fun `toUserMessage maps ERROR_CODE_IO_FILE_NOT_FOUND to file not found message`() {
        val exception = PlaybackException("Test", null, PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND)

        val message = exception.toUserMessage(null)

        assertEquals("no se encuentra el archivo", message)
    }

    @Test
    fun `toUserMessage maps ERROR_CODE_IO_NO_PERMISSION to permission denied message`() {
        val exception = PlaybackException("Test", null, PlaybackException.ERROR_CODE_IO_NO_PERMISSION)

        val message = exception.toUserMessage(null)

        assertEquals("sin permiso para acceder al archivo", message)
    }

    @Test
    fun `toUserMessage maps ERROR_CODE_PARSING_CONTAINER_MALFORMED to corrupted message`() {
        val exception = PlaybackException("Test", null, PlaybackException.ERROR_CODE_PARSING_CONTAINER_MALFORMED)

        val message = exception.toUserMessage(null)

        assertEquals("el archivo está dañado o no es compatible", message)
    }

    @Test
    fun `toUserMessage maps ERROR_CODE_PARSING_MANIFEST_MALFORMED to corrupted message`() {
        val exception = PlaybackException("Test", null, PlaybackException.ERROR_CODE_PARSING_MANIFEST_MALFORMED)

        val message = exception.toUserMessage(null)

        assertEquals("el archivo está dañado o no es compatible", message)
    }

    @Test
    fun `toUserMessage maps ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED to corrupted message`() {
        val exception = PlaybackException("Test", null, PlaybackException.ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED)

        val message = exception.toUserMessage(null)

        assertEquals("el archivo está dañado o no es compatible", message)
    }

    @Test
    fun `toUserMessage maps ERROR_CODE_DECODER_INIT_FAILED to decode error message`() {
        val exception = PlaybackException("Test", null, PlaybackException.ERROR_CODE_DECODER_INIT_FAILED)

        val message = exception.toUserMessage(null)

        assertEquals("no se pudo decodificar el audio", message)
    }

    @Test
    fun `toUserMessage maps ERROR_CODE_DECODING_FAILED to decode error message`() {
        val exception = PlaybackException("Test", null, PlaybackException.ERROR_CODE_DECODING_FAILED)

        val message = exception.toUserMessage(null)

        assertEquals("no se pudo decodificar el audio", message)
    }

    @Test
    fun `toUserMessage uses default message for unmapped error codes`() {
        val exception = PlaybackException("Test", null, -1)

        val message = exception.toUserMessage(null)

        assertEquals("no se pudo reproducir", message)
    }

    @Test
    fun `toUserMessage includes song title prefix when title is present`() {
        val exception = PlaybackException("Test", null, PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND)

        val message = exception.toUserMessage("Mi Canción")

        assertEquals("\"Mi Canción\": no se encuentra el archivo", message)
    }

    @Test
    fun `toUserMessage omits prefix when title is empty string`() {
        val exception = PlaybackException("Test", null, PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND)

        val message = exception.toUserMessage("")

        assertEquals("no se encuentra el archivo", message)
    }

    @Test
    fun `toUserMessage omits prefix when title is whitespace only`() {
        val exception = PlaybackException("Test", null, PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND)

        val message = exception.toUserMessage("   ")

        assertEquals("no se encuentra el archivo", message)
    }

    @Test
    fun `toUserMessage omits prefix when title is null`() {
        val exception = PlaybackException("Test", null, PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND)

        val message = exception.toUserMessage(null)

        assertEquals("no se encuentra el archivo", message)
    }
}

/**
 * Minimal [Timeline] fake that lets tests fix an explicit shuffled window order, independent of
 * ExoPlayer's real [androidx.media3.exoplayer.source.ShuffleOrder] machinery. Only the methods
 * [playbackOrderIndices] actually calls ([getFirstWindowIndex]/[getNextWindowIndex]) honor
 * [shuffledOrder]; the rest are unused stubs required by the abstract [Timeline] contract.
 */
private class FakeShuffleTimeline(
    private val windowCount: Int,
    private val shuffledOrder: List<Int>,
) : Timeline() {
    override fun getWindowCount(): Int = windowCount

    override fun getWindow(windowIndex: Int, window: Window, defaultPositionProjectionUs: Long): Window =
        window.set(
            windowIndex,
            null,
            null,
            C.TIME_UNSET,
            C.TIME_UNSET,
            C.TIME_UNSET,
            false,
            false,
            null,
            0L,
            0L,
            windowIndex,
            windowIndex,
            0L,
        )

    override fun getPeriodCount(): Int = windowCount

    override fun getPeriod(periodIndex: Int, period: Period, setIds: Boolean): Period =
        period.set(null, null, periodIndex, 0L, 0L)

    override fun getIndexOfPeriod(uid: Any): Int = C.INDEX_UNSET

    override fun getUidOfPeriod(periodIndex: Int): Any = periodIndex

    override fun getFirstWindowIndex(shuffleModeEnabled: Boolean): Int = when {
        windowCount == 0 -> C.INDEX_UNSET
        shuffleModeEnabled -> shuffledOrder.first()
        else -> 0
    }

    override fun getLastWindowIndex(shuffleModeEnabled: Boolean): Int = when {
        windowCount == 0 -> C.INDEX_UNSET
        shuffleModeEnabled -> shuffledOrder.last()
        else -> windowCount - 1
    }

    override fun getNextWindowIndex(windowIndex: Int, repeatMode: Int, shuffleModeEnabled: Boolean): Int {
        if (!shuffleModeEnabled) {
            return if (windowIndex == windowCount - 1) C.INDEX_UNSET else windowIndex + 1
        }
        val position = shuffledOrder.indexOf(windowIndex)
        return if (position == shuffledOrder.lastIndex) C.INDEX_UNSET else shuffledOrder[position + 1]
    }
}
