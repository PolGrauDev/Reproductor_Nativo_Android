package com.PolGrauDev.reproductor_nativo_android.player

import androidx.media3.common.PlaybackException
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PlaybackConnectionLogicTest {

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
