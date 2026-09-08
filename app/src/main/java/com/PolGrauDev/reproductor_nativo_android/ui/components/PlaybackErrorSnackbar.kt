package com.PolGrauDev.reproductor_nativo_android.ui.components

import androidx.compose.material3.SnackbarData
import androidx.compose.runtime.Composable
import com.PolGrauDev.reproductor_nativo_android.ui.components.style.fanzine.PlaybackErrorSnackbarFanzine
import com.PolGrauDev.reproductor_nativo_android.ui.components.style.papel.PlaybackErrorSnackbarPapel
import com.PolGrauDev.reproductor_nativo_android.ui.components.style.sticker.PlaybackErrorSnackbarSticker
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.AppStyle

/**
 * Thin dispatcher over the three bespoke visual styles — see `ui/components/style/<style>/
 * PlaybackErrorSnackbarX.kt` for the actual per-style implementations. Used as the custom
 * `SnackbarHost` content in `MainActivity` so playback-error toasts match the current app style.
 */
@Composable
fun PlaybackErrorSnackbar(appStyle: AppStyle, data: SnackbarData) {
    when (appStyle) {
        AppStyle.PAPEL -> PlaybackErrorSnackbarPapel(data)
        AppStyle.STICKERS -> PlaybackErrorSnackbarSticker(data)
        AppStyle.FANZINE -> PlaybackErrorSnackbarFanzine(data)
    }
}
