package com.PolGrauDev.reproductor_nativo_android.ui.components

import androidx.compose.runtime.Composable
import com.PolGrauDev.reproductor_nativo_android.data.model.PlaylistSummary
import com.PolGrauDev.reproductor_nativo_android.ui.components.style.fanzine.AddToPlaylistDialogFanzine
import com.PolGrauDev.reproductor_nativo_android.ui.components.style.papel.AddToPlaylistDialogPapel
import com.PolGrauDev.reproductor_nativo_android.ui.components.style.sticker.AddToPlaylistDialogSticker
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.AppStyle

/**
 * Diálogo compartido: elegir una playlist existente o crear una nueva, para añadir una
 * canción concreta. La canción a añadir la conoce quien invoca el diálogo (vía los callbacks),
 * no este composable. Thin dispatcher over the three bespoke visual styles — see
 * `ui/components/style/<style>/AddToPlaylistDialogX.kt` for the actual per-style implementations
 * (built from the design canvas's `4a`/`4b`/`4c` playlist-dialog mockup).
 */
@Composable
fun AddToPlaylistDialog(
    appStyle: AppStyle,
    playlists: List<PlaylistSummary>,
    onDismiss: () -> Unit,
    onPlaylistSelected: (playlistId: Long) -> Unit,
    onCreatePlaylist: (name: String) -> Unit,
) {
    when (appStyle) {
        AppStyle.PAPEL -> AddToPlaylistDialogPapel(playlists, onDismiss, onPlaylistSelected, onCreatePlaylist)
        AppStyle.STICKERS -> AddToPlaylistDialogSticker(playlists, onDismiss, onPlaylistSelected, onCreatePlaylist)
        AppStyle.FANZINE -> AddToPlaylistDialogFanzine(playlists, onDismiss, onPlaylistSelected, onCreatePlaylist)
    }
}
