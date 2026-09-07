package com.PolGrauDev.reproductor_nativo_android.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.PolGrauDev.reproductor_nativo_android.ui.screens.style.fanzine.FavoritesScreenFanzine
import com.PolGrauDev.reproductor_nativo_android.ui.screens.style.papel.FavoritesScreenPapel
import com.PolGrauDev.reproductor_nativo_android.ui.screens.style.sticker.FavoritesScreenSticker
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.AppStyle
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicViewModel

/**
 * Thin dispatcher over the three bespoke visual styles — see `ui/screens/style/<style>/
 * FavoritesScreenX.kt` for the actual per-style implementations (built from the design canvas's
 * `4a`/`4b`/`4c` Favoritos mockups).
 */
@Composable
fun FavoritesScreen(viewModel: MusicViewModel, onBack: () -> Unit, onSongClick: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (uiState.appStyle) {
        AppStyle.PAPEL -> FavoritesScreenPapel(viewModel, onBack, onSongClick)
        AppStyle.STICKERS -> FavoritesScreenSticker(viewModel, onBack, onSongClick)
        AppStyle.FANZINE -> FavoritesScreenFanzine(viewModel, onBack, onSongClick)
    }
}
