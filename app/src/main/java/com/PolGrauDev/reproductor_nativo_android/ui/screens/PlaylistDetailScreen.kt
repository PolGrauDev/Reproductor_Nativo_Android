package com.PolGrauDev.reproductor_nativo_android.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.PolGrauDev.reproductor_nativo_android.ui.screens.style.fanzine.PlaylistDetailScreenFanzine
import com.PolGrauDev.reproductor_nativo_android.ui.screens.style.papel.PlaylistDetailScreenPapel
import com.PolGrauDev.reproductor_nativo_android.ui.screens.style.sticker.PlaylistDetailScreenSticker
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.AppStyle
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicViewModel

/**
 * Thin dispatcher over the three bespoke visual styles — see `ui/screens/style/<style>/
 * DetailScreensX.kt` for the actual per-style implementations (built from the design canvas's
 * `4a`/`4b`/`4c` playlist-dialog mockup's list-row language, adapted for the full playlist
 * screen's reorder/remove actions).
 */
@Composable
fun PlaylistDetailScreen(
    viewModel: MusicViewModel,
    playlistId: Long,
    onBack: () -> Unit,
    onSongClick: () -> Unit,
    onPlaylistDeleted: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (uiState.appStyle) {
        AppStyle.PAPEL -> PlaylistDetailScreenPapel(viewModel, playlistId, onBack, onSongClick, onPlaylistDeleted)
        AppStyle.STICKERS -> PlaylistDetailScreenSticker(viewModel, playlistId, onBack, onSongClick, onPlaylistDeleted)
        AppStyle.FANZINE -> PlaylistDetailScreenFanzine(viewModel, playlistId, onBack, onSongClick, onPlaylistDeleted)
    }
}
