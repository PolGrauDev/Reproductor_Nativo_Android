package com.PolGrauDev.reproductor_nativo_android.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.PolGrauDev.reproductor_nativo_android.data.model.AlbumGroup
import com.PolGrauDev.reproductor_nativo_android.data.model.ArtistGroup
import com.PolGrauDev.reproductor_nativo_android.data.model.FolderGroup
import com.PolGrauDev.reproductor_nativo_android.ui.screens.style.fanzine.LibraryScreenFanzine
import com.PolGrauDev.reproductor_nativo_android.ui.screens.style.papel.LibraryScreenPapel
import com.PolGrauDev.reproductor_nativo_android.ui.screens.style.sticker.LibraryScreenSticker
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.AppStyle
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicViewModel

/**
 * Thin dispatcher over the three bespoke visual styles — see `ui/screens/style/<style>/
 * LibraryScreenX.kt` for the actual per-style implementations (built from the design canvas's
 * `1c`/`2a`/`3a` mockups).
 */
@Composable
fun LibraryScreen(
    viewModel: MusicViewModel,
    onSongClick: () -> Unit,
    onAlbumClick: (AlbumGroup) -> Unit,
    onArtistClick: (ArtistGroup) -> Unit,
    onFolderClick: (FolderGroup) -> Unit,
    onFavoritesClick: () -> Unit,
    onPlaylistClick: (Long) -> Unit,
    onSettingsClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState.appStyle) {
        AppStyle.PAPEL -> LibraryScreenPapel(
            viewModel, uiState, onSongClick, onAlbumClick, onArtistClick, onFolderClick,
            onFavoritesClick, onPlaylistClick, onSettingsClick,
        )

        AppStyle.STICKERS -> LibraryScreenSticker(
            viewModel, uiState, onSongClick, onAlbumClick, onArtistClick, onFolderClick,
            onFavoritesClick, onPlaylistClick, onSettingsClick,
        )

        AppStyle.FANZINE -> LibraryScreenFanzine(
            viewModel, uiState, onSongClick, onAlbumClick, onArtistClick, onFolderClick,
            onFavoritesClick, onPlaylistClick, onSettingsClick,
        )
    }
}
