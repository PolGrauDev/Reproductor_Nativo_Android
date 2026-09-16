package com.PolGrauDev.reproductor_nativo_android.ui.components.style.fanzine

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.FanzineColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.FanzineFonts

@Composable
fun SongOptionsMenuFanzine(
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
        Column(Modifier.rotate(-1f).background(FanzineColors.Paper)) {
            Row(Modifier.fillMaxWidth().background(FanzineColors.Red).padding(14.dp, 11.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.MoreVert, contentDescription = null, tint = FanzineColors.Ink)
                Spacer(Modifier.width(8.dp))
                Text("OPCIONES", fontFamily = FanzineFonts.Anton, fontSize = 20.sp, color = FanzineColors.Ink)
            }
            Column(Modifier.padding(14.dp)) {
                FanzineOptionRow("Añadir canción a una lista de reproducción") { onDismiss(); onAddToPlaylist() }
                FanzineOptionRow(if (isFavorite) "Quitar de favoritos" else "Añadir a favoritos") { onDismiss(); onToggleFavorite() }
                FanzineOptionRow("Cambiar imagen de la canción") { onDismiss(); onChangeImage() }
                FanzineOptionRow("Editar información de la canción") { onDismiss(); onEditInfo() }
                FanzineOptionRow("Compartir") { onDismiss(); onShare() }
            }
        }
    }
}

@Composable
private fun FanzineOptionRow(text: String, onClick: () -> Unit) {
    Text(
        text,
        fontFamily = FanzineFonts.SpecialElite,
        fontSize = 15.sp,
        color = FanzineColors.Ink,
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 11.dp),
    )
}
