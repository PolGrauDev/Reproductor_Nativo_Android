package com.PolGrauDev.reproductor_nativo_android.ui.screens

import androidx.compose.runtime.Composable
import com.PolGrauDev.reproductor_nativo_android.ui.screens.style.fanzine.PermissionRationaleScreenFanzine
import com.PolGrauDev.reproductor_nativo_android.ui.screens.style.papel.PermissionRationaleScreenPapel
import com.PolGrauDev.reproductor_nativo_android.ui.screens.style.sticker.PermissionRationaleScreenSticker
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.AppStyle

/**
 * Thin dispatcher over the three bespoke visual styles — see `ui/screens/style/<style>/
 * PermissionRationaleScreenX.kt` for the actual per-style implementations.
 */
@Composable
fun PermissionRationaleScreen(style: AppStyle, message: String, actionLabel: String, onAction: () -> Unit) {
    when (style) {
        AppStyle.PAPEL -> PermissionRationaleScreenPapel(message, actionLabel, onAction)
        AppStyle.STICKERS -> PermissionRationaleScreenSticker(message, actionLabel, onAction)
        AppStyle.FANZINE -> PermissionRationaleScreenFanzine(message, actionLabel, onAction)
    }
}
