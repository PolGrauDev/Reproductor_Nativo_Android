package com.PolGrauDev.reproductor_nativo_android.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.PolGrauDev.reproductor_nativo_android.ui.screens.style.fanzine.NowPlayingScreenFanzine
import com.PolGrauDev.reproductor_nativo_android.ui.screens.style.papel.NowPlayingScreenPapel
import com.PolGrauDev.reproductor_nativo_android.ui.screens.style.sticker.NowPlayingScreenSticker
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.AppStyle
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicViewModel

/**
 * Thin dispatcher over the three bespoke visual styles — see `ui/screens/style/<style>/
 * NowPlayingScreenX.kt` for the actual per-style implementations (built from the design
 * canvas's `1c`/`2a`/`3a` mockups).
 */
@Composable
fun NowPlayingScreen(viewModel: MusicViewModel, onBack: () -> Unit, onQueueClick: () -> Unit, onSearchClick: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState.appStyle) {
        AppStyle.PAPEL -> NowPlayingScreenPapel(viewModel, uiState, onBack, onQueueClick, onSearchClick)
        AppStyle.STICKERS -> NowPlayingScreenSticker(viewModel, uiState, onBack, onQueueClick, onSearchClick)
        AppStyle.FANZINE -> NowPlayingScreenFanzine(viewModel, uiState, onBack, onQueueClick, onSearchClick)
    }
}
