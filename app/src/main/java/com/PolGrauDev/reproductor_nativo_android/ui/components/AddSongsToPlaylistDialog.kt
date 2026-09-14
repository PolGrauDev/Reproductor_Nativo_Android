package com.PolGrauDev.reproductor_nativo_android.ui.components

import androidx.compose.runtime.Composable
import com.PolGrauDev.reproductor_nativo_android.data.model.Song
import com.PolGrauDev.reproductor_nativo_android.ui.components.style.fanzine.AddSongsToPlaylistDialogFanzine
import com.PolGrauDev.reproductor_nativo_android.ui.components.style.papel.AddSongsToPlaylistDialogPapel
import com.PolGrauDev.reproductor_nativo_android.ui.components.style.sticker.AddSongsToPlaylistDialogSticker
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.AppStyle

/**
 * Diálogo compartido: elegir canciones de la biblioteca para añadirlas a una playlist ya
 * conocida por quien invoca el diálogo (vía [onAddSongs]). Complementa a [AddToPlaylistDialog],
 * que va en la dirección contraria (canción conocida → elegir playlist). Thin dispatcher over
 * the three bespoke visual styles — see `ui/components/style/<style>/AddSongsToPlaylistDialogX.kt`
 * for the actual per-style implementations.
 */
@Composable
fun AddSongsToPlaylistDialog(
    appStyle: AppStyle,
    allSongs: List<Song>,
    songIdsAlreadyInPlaylist: Set<Long>,
    onDismiss: () -> Unit,
    onAddSongs: (Set<Long>) -> Unit,
) {
    when (appStyle) {
        AppStyle.PAPEL -> AddSongsToPlaylistDialogPapel(allSongs, songIdsAlreadyInPlaylist, onDismiss, onAddSongs)
        AppStyle.STICKERS -> AddSongsToPlaylistDialogSticker(allSongs, songIdsAlreadyInPlaylist, onDismiss, onAddSongs)
        AppStyle.FANZINE -> AddSongsToPlaylistDialogFanzine(allSongs, songIdsAlreadyInPlaylist, onDismiss, onAddSongs)
    }
}
