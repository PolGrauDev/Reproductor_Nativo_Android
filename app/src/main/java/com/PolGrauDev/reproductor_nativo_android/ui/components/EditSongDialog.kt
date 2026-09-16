package com.PolGrauDev.reproductor_nativo_android.ui.components

import androidx.compose.runtime.Composable
import com.PolGrauDev.reproductor_nativo_android.data.model.Song
import com.PolGrauDev.reproductor_nativo_android.ui.components.style.fanzine.EditSongDialogFanzine
import com.PolGrauDev.reproductor_nativo_android.ui.components.style.papel.EditSongDialogPapel
import com.PolGrauDev.reproductor_nativo_android.ui.components.style.sticker.EditSongDialogSticker
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.AppStyle

/**
 * Diálogo para editar título/artista/álbum de una canción. Los cambios se guardan como overrides
 * locales (no se modifica el archivo de audio real). Thin dispatcher over the three bespoke
 * visual styles — see `ui/components/style/<style>/EditSongDialogX.kt`.
 */
@Composable
fun EditSongDialog(
    appStyle: AppStyle,
    song: Song,
    onDismiss: () -> Unit,
    onSave: (title: String, artist: String, album: String) -> Unit,
) {
    when (appStyle) {
        AppStyle.PAPEL -> EditSongDialogPapel(song, onDismiss, onSave)
        AppStyle.STICKERS -> EditSongDialogSticker(song, onDismiss, onSave)
        AppStyle.FANZINE -> EditSongDialogFanzine(song, onDismiss, onSave)
    }
}
