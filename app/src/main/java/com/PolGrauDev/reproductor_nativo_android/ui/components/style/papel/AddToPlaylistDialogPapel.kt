package com.PolGrauDev.reproductor_nativo_android.ui.components.style.papel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.PolGrauDev.reproductor_nativo_android.data.model.PlaylistSummary
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelRowDivider
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelSectionLabel
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelType

@Composable
fun AddToPlaylistDialogPapel(
    playlists: List<PlaylistSummary>,
    onDismiss: () -> Unit,
    onPlaylistSelected: (playlistId: Long) -> Unit,
    onCreatePlaylist: (name: String) -> Unit,
) {
    var newPlaylistName by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Column(Modifier.background(PapelColors.Surface).padding(22.dp)) {
            PapelSectionLabel("Añadir a")
            Text("Tus listas", style = PapelType.TitleLarge, color = PapelColors.OnSurface)
            Spacer(Modifier.height(14.dp))
            if (playlists.isEmpty()) {
                Text("Todavía no tienes playlists", style = PapelType.BodyMedium, color = PapelColors.OnSurfaceVariant)
            } else {
                LazyColumn(Modifier.heightIn(max = 240.dp)) {
                    items(playlists, key = { it.id }) { playlist ->
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { onPlaylistSelected(playlist.id) }.padding(vertical = 13.dp),
                            ) {
                                Text(playlist.name, style = PapelType.TitleMedium, color = PapelColors.OnSurface, modifier = Modifier.weight(1f))
                                Text("${playlist.songCount}", style = PapelType.Mono, color = PapelColors.OnSurfaceVariant)
                            }
                            PapelRowDivider()
                        }
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
            PapelSectionLabel("Nueva lista")
            Column {
                BasicTextField(
                    value = newPlaylistName,
                    onValueChange = { newPlaylistName = it },
                    singleLine = true,
                    textStyle = PapelType.BodyMedium.copy(color = PapelColors.OnSurface),
                    cursorBrush = SolidColor(PapelColors.Primary),
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                PapelRowDivider()
            }
            Spacer(Modifier.height(22.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(
                    "CANCELAR",
                    style = PapelType.SectionLabel,
                    color = PapelColors.OnSurfaceVariant,
                    modifier = Modifier.clickable(onClick = onDismiss).padding(8.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "CREAR Y AÑADIR",
                    style = PapelType.SectionLabel,
                    color = if (newPlaylistName.isNotBlank()) PapelColors.OnSurface else PapelColors.Faint,
                    modifier = Modifier
                        .clickable(enabled = newPlaylistName.isNotBlank()) {
                            onCreatePlaylist(newPlaylistName)
                            newPlaylistName = ""
                        }
                        .padding(8.dp),
                )
            }
        }
    }
}
