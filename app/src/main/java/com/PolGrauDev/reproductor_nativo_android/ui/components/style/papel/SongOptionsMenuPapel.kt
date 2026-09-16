package com.PolGrauDev.reproductor_nativo_android.ui.components.style.papel

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun SongOptionsMenuPapel(
    expanded: Boolean,
    isFavorite: Boolean,
    onDismiss: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onToggleFavorite: () -> Unit,
    onChangeImage: () -> Unit,
    onEditInfo: () -> Unit,
    onShare: () -> Unit,
) {
    DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
        DropdownMenuItem(
            text = { Text("Añadir canción a una lista de reproducción") },
            onClick = { onDismiss(); onAddToPlaylist() },
        )
        DropdownMenuItem(
            text = { Text(if (isFavorite) "Quitar de favoritos" else "Añadir a favoritos") },
            onClick = { onDismiss(); onToggleFavorite() },
        )
        DropdownMenuItem(
            text = { Text("Cambiar imagen de la canción") },
            onClick = { onDismiss(); onChangeImage() },
        )
        DropdownMenuItem(
            text = { Text("Editar información de la canción") },
            onClick = { onDismiss(); onEditInfo() },
        )
        DropdownMenuItem(
            text = { Text("Compartir") },
            onClick = { onDismiss(); onShare() },
        )
    }
}
