package com.PolGrauDev.reproductor_nativo_android.ui.components.style.papel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.PolGrauDev.reproductor_nativo_android.data.model.Song
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelRowDivider
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelSectionLabel
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelType

@Composable
fun AddSongsToPlaylistDialogPapel(
    allSongs: List<Song>,
    songIdsAlreadyInPlaylist: Set<Long>,
    onDismiss: () -> Unit,
    onAddSongs: (Set<Long>) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf(setOf<Long>()) }
    val visible = remember(allSongs, songIdsAlreadyInPlaylist, query) {
        allSongs.filter {
            it.id !in songIdsAlreadyInPlaylist &&
                (query.isBlank() || it.title.contains(query, ignoreCase = true) || it.artist.contains(query, ignoreCase = true))
        }.sortedBy { it.title }
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(Modifier.background(PapelColors.Surface).padding(22.dp)) {
            PapelSectionLabel("Añadir canciones")
            Text("Elige de tu biblioteca", style = PapelType.TitleLarge, color = PapelColors.OnSurface)
            Spacer(Modifier.height(14.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Search, contentDescription = null, tint = PapelColors.OnSurfaceVariant, modifier = Modifier.height(20.dp))
                Spacer(Modifier.width(10.dp))
                Box(Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text("Buscar en la colección", style = PapelType.BodyMedium, color = PapelColors.OnSurfaceVariant)
                    }
                    BasicTextField(
                        value = query,
                        onValueChange = { query = it },
                        singleLine = true,
                        textStyle = PapelType.BodyMedium.copy(color = PapelColors.OnSurface),
                        cursorBrush = SolidColor(PapelColors.Primary),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            PapelRowDivider()
            Spacer(Modifier.height(10.dp))
            if (visible.isEmpty()) {
                Text("No hay canciones para añadir", style = PapelType.BodyMedium, color = PapelColors.OnSurfaceVariant)
            } else {
                LazyColumn(Modifier.heightIn(max = 280.dp)) {
                    items(visible, key = { it.id }) { song ->
                        val isSelected = song.id in selected
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .clickable { selected = if (isSelected) selected - song.id else selected + song.id }
                                    .padding(vertical = 11.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(song.title, style = PapelType.TitleMedium, color = PapelColors.OnSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(song.artist, style = PapelType.BodySmall, color = PapelColors.OnSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                Icon(
                                    if (isSelected) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (isSelected) PapelColors.Accent else PapelColors.Faint,
                                )
                            }
                            PapelRowDivider()
                        }
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(
                    "CANCELAR",
                    style = PapelType.SectionLabel,
                    color = PapelColors.OnSurfaceVariant,
                    modifier = Modifier.clickable(onClick = onDismiss).padding(8.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "AÑADIR (${selected.size})",
                    style = PapelType.SectionLabel,
                    color = if (selected.isNotEmpty()) PapelColors.OnSurface else PapelColors.Faint,
                    modifier = Modifier
                        .clickable(enabled = selected.isNotEmpty()) { onAddSongs(selected) }
                        .padding(8.dp),
                )
            }
        }
    }
}
