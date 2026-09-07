package com.PolGrauDev.reproductor_nativo_android.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.PolGrauDev.reproductor_nativo_android.ui.screens.style.fanzine.ArtistDetailScreenFanzine
import com.PolGrauDev.reproductor_nativo_android.ui.screens.style.papel.ArtistDetailScreenPapel
import com.PolGrauDev.reproductor_nativo_android.ui.screens.style.sticker.ArtistDetailScreenSticker
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.AppStyle
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicViewModel

/**
 * Thin dispatcher over the three bespoke visual styles — see `ui/screens/style/<style>/
 * DetailScreensX.kt` for the actual per-style implementations (same structure as Album/Playlist,
 * retthemed per style — no bespoke mockup exists for this screen).
 */
@Composable
fun ArtistDetailScreen(viewModel: MusicViewModel, artistId: Long?, onBack: () -> Unit, onSongClick: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (uiState.appStyle) {
        AppStyle.PAPEL -> ArtistDetailScreenPapel(viewModel, artistId, onBack, onSongClick)
        AppStyle.STICKERS -> ArtistDetailScreenSticker(viewModel, artistId, onBack, onSongClick)
        AppStyle.FANZINE -> ArtistDetailScreenFanzine(viewModel, artistId, onBack, onSongClick)
    }
}
