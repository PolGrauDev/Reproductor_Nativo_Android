package com.PolGrauDev.reproductor_nativo_android.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.PolGrauDev.reproductor_nativo_android.ui.screens.style.fanzine.FolderDetailScreenFanzine
import com.PolGrauDev.reproductor_nativo_android.ui.screens.style.papel.FolderDetailScreenPapel
import com.PolGrauDev.reproductor_nativo_android.ui.screens.style.sticker.FolderDetailScreenSticker
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.AppStyle
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicViewModel

/**
 * Thin dispatcher over the three bespoke visual styles — see `ui/screens/style/<style>/
 * DetailScreensX.kt` for the actual per-style implementations (shares its row component with the
 * Album detail screen, exactly like the pre-reskin `AlbumSongRow` did).
 */
@Composable
fun FolderDetailScreen(viewModel: MusicViewModel, folderPath: String, onBack: () -> Unit, onSongClick: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (uiState.appStyle) {
        AppStyle.PAPEL -> FolderDetailScreenPapel(viewModel, folderPath, onBack, onSongClick)
        AppStyle.STICKERS -> FolderDetailScreenSticker(viewModel, folderPath, onBack, onSongClick)
        AppStyle.FANZINE -> FolderDetailScreenFanzine(viewModel, folderPath, onBack, onSongClick)
    }
}
