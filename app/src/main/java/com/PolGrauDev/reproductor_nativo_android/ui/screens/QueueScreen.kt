package com.PolGrauDev.reproductor_nativo_android.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.PolGrauDev.reproductor_nativo_android.ui.screens.style.fanzine.QueueScreenFanzine
import com.PolGrauDev.reproductor_nativo_android.ui.screens.style.papel.QueueScreenPapel
import com.PolGrauDev.reproductor_nativo_android.ui.screens.style.sticker.QueueScreenSticker
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.AppStyle
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicViewModel

/**
 * Thin dispatcher over the three bespoke visual styles — see `ui/screens/style/<style>/
 * QueueScreenX.kt` for the actual per-style implementations (built from the design canvas's
 * `4a`/`4b`/`4c` Cola mockups).
 */
@Composable
fun QueueScreen(viewModel: MusicViewModel, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (uiState.appStyle) {
        AppStyle.PAPEL -> QueueScreenPapel(viewModel, onBack)
        AppStyle.STICKERS -> QueueScreenSticker(viewModel, onBack)
        AppStyle.FANZINE -> QueueScreenFanzine(viewModel, onBack)
    }
}
