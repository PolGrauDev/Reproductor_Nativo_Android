package com.PolGrauDev.reproductor_nativo_android.ui.components.style.fanzine

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.PolGrauDev.reproductor_nativo_android.data.model.Song
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.FanzineColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.FanzineFonts

@Composable
fun AddSongsToPlaylistDialogFanzine(
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
        Column(Modifier.rotate(-1f).background(FanzineColors.Paper)) {
            Row(Modifier.fillMaxWidth().background(FanzineColors.Red).padding(14.dp, 11.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Filled.PlaylistAdd, contentDescription = null, tint = FanzineColors.Ink)
                Spacer(Modifier.width(8.dp))
                Text("AÑADIR CANCIONES", fontFamily = FanzineFonts.Anton, fontSize = 20.sp, color = FanzineColors.Ink)
            }
            Column(Modifier.padding(14.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Search, contentDescription = null, tint = FanzineColors.Red)
                    Spacer(Modifier.width(9.dp))
                    Box(Modifier.weight(1f)) {
                        if (query.isEmpty()) {
                            Text("buscar entre el ruido…", fontFamily = FanzineFonts.SpecialElite, fontSize = 13.sp, color = FanzineColors.Grime)
                        }
                        BasicTextField(
                            value = query,
                            onValueChange = { query = it },
                            singleLine = true,
                            textStyle = TextStyle(fontFamily = FanzineFonts.SpecialElite, fontSize = 13.sp, color = FanzineColors.Ink),
                            cursorBrush = SolidColor(FanzineColors.Ink),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                if (visible.isEmpty()) {
                    Text("No hay canciones para añadir", fontFamily = FanzineFonts.SpecialElite, fontSize = 13.sp, color = FanzineColors.Grime)
                } else {
                    LazyColumn(Modifier.heightIn(max = 260.dp)) {
                        items(visible, key = { it.id }) { song ->
                            val isSelected = song.id in selected
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selected = if (isSelected) selected - song.id else selected + song.id }
                                    .padding(vertical = 11.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(song.title, fontFamily = FanzineFonts.SpecialElite, fontSize = 15.sp, color = FanzineColors.Ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(song.artist, fontFamily = FanzineFonts.SpecialElite, fontSize = 11.sp, color = FanzineColors.Grime, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                Icon(
                                    if (isSelected) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (isSelected) FanzineColors.Red else FanzineColors.Grime,
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Text(
                        "cancelar",
                        fontFamily = FanzineFonts.SpecialElite,
                        fontSize = 11.sp,
                        color = FanzineColors.Grime,
                        modifier = Modifier.border(2.dp, FanzineColors.Ink).clickable(onClick = onDismiss).padding(11.dp, 5.dp),
                    )
                    Spacer(Modifier.width(9.dp))
                    Text(
                        "añadir (${selected.size})",
                        fontFamily = FanzineFonts.SpecialElite,
                        fontSize = 11.sp,
                        color = FanzineColors.Paper,
                        modifier = Modifier
                            .background(FanzineColors.Ink)
                            .clickable(enabled = selected.isNotEmpty()) { onAddSongs(selected) }
                            .padding(12.dp, 7.dp),
                    )
                }
            }
        }
    }
}
