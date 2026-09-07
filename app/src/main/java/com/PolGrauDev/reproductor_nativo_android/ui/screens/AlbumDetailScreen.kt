package com.PolGrauDev.reproductor_nativo_android.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.PolGrauDev.reproductor_nativo_android.ui.screens.style.fanzine.AlbumDetailScreenFanzine
import com.PolGrauDev.reproductor_nativo_android.ui.screens.style.papel.AlbumDetailScreenPapel
import com.PolGrauDev.reproductor_nativo_android.ui.screens.style.sticker.AlbumDetailScreenSticker
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.AppStyle
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicViewModel

/**
 * Thin dispatcher over the three bespoke visual styles — see `ui/screens/style/<style>/
 * DetailScreensX.kt` for the actual per-style implementations (built from the design canvas's
 * `4a`/`4b`/`4c` Album mockups; shares its row component with the Folder detail screen, exactly
 * like the pre-reskin `AlbumSongRow` did).
 */
@Composable
fun AlbumDetailScreen(viewModel: MusicViewModel, albumId: Long?, onBack: () -> Unit, onSongClick: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (uiState.appStyle) {
        AppStyle.PAPEL -> AlbumDetailScreenPapel(viewModel, albumId, onBack, onSongClick)
        AppStyle.STICKERS -> AlbumDetailScreenSticker(viewModel, albumId, onBack, onSongClick)
        AppStyle.FANZINE -> AlbumDetailScreenFanzine(viewModel, albumId, onBack, onSongClick)
    }
}
