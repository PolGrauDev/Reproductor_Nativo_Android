package com.PolGrauDev.reproductor_nativo_android.ui.components

import androidx.compose.runtime.Composable
import com.PolGrauDev.reproductor_nativo_android.ui.components.style.fanzine.SongOptionsMenuFanzine
import com.PolGrauDev.reproductor_nativo_android.ui.components.style.papel.SongOptionsMenuPapel
import com.PolGrauDev.reproductor_nativo_android.ui.components.style.sticker.SongOptionsMenuSticker
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.AppStyle

/**
 * Menú de opciones de una canción ("Añadir a lista", favoritos, cambiar imagen, editar info,
 * compartir), mostrado desde el botón de tres puntos de cada fila. Thin dispatcher over the
 * three bespoke visual styles — see `ui/components/style/<style>/SongOptionsMenuX.kt`.
 */
@Composable
fun SongOptionsMenu(
    appStyle: AppStyle,
    expanded: Boolean,
    isFavorite: Boolean,
    onDismiss: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onToggleFavorite: () -> Unit,
    onChangeImage: () -> Unit,
    onEditInfo: () -> Unit,
    onShare: () -> Unit,
) {
    when (appStyle) {
        AppStyle.PAPEL -> SongOptionsMenuPapel(expanded, isFavorite, onDismiss, onAddToPlaylist, onToggleFavorite, onChangeImage, onEditInfo, onShare)
        AppStyle.STICKERS -> SongOptionsMenuSticker(expanded, isFavorite, onDismiss, onAddToPlaylist, onToggleFavorite, onChangeImage, onEditInfo, onShare)
        AppStyle.FANZINE -> SongOptionsMenuFanzine(expanded, isFavorite, onDismiss, onAddToPlaylist, onToggleFavorite, onChangeImage, onEditInfo, onShare)
    }
}
