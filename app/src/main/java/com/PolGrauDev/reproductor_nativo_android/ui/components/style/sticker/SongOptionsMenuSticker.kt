package com.PolGrauDev.reproductor_nativo_android.ui.components.style.sticker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.StickerColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.StickerHardShadowBox
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.StickerType

@Composable
fun SongOptionsMenuSticker(
    expanded: Boolean,
    isFavorite: Boolean,
    onDismiss: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onToggleFavorite: () -> Unit,
    onChangeImage: () -> Unit,
    onEditInfo: () -> Unit,
    onShare: () -> Unit,
) {
    if (!expanded) return
    Dialog(onDismissRequest = onDismiss) {
        StickerHardShadowBox(shape = RoundedCornerShape(26.dp), borderWidth = 4.dp, shadowOffsetX = 7.dp, shadowOffsetY = 7.dp) {
            Column(Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth().background(StickerColors.Grape, RoundedCornerShape(22.dp, 22.dp, 0.dp, 0.dp)).padding(16.dp, 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Filled.MoreVert, contentDescription = null, tint = StickerColors.Ink)
                    Spacer(Modifier.width(9.dp))
                    Text("Opciones", style = StickerType.TitleMedium.let { it.copy(fontSize = 20.sp) }, color = StickerColors.Ink)
                }
                Column(Modifier.padding(16.dp)) {
                    StickerOptionRow("Añadir canción a una lista de reproducción") { onDismiss(); onAddToPlaylist() }
                    StickerOptionRow(if (isFavorite) "Quitar de favoritos" else "Añadir a favoritos") { onDismiss(); onToggleFavorite() }
                    StickerOptionRow("Cambiar imagen de la canción") { onDismiss(); onChangeImage() }
                    StickerOptionRow("Editar información de la canción") { onDismiss(); onEditInfo() }
                    StickerOptionRow("Compartir") { onDismiss(); onShare() }
                }
            }
        }
    }
}

@Composable
private fun StickerOptionRow(text: String, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().background(StickerColors.Paper, RoundedCornerShape(16.dp)).clickable(onClick = onClick).padding(12.dp, 11.dp),
    ) { Text(text, style = StickerType.TitleMedium, color = StickerColors.Ink) }
    Spacer(Modifier.height(9.dp))
}
