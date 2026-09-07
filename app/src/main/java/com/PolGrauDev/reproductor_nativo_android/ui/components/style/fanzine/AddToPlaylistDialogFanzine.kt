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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.PolGrauDev.reproductor_nativo_android.data.model.PlaylistSummary
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.FanzineColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.FanzineFonts

@Composable
fun AddToPlaylistDialogFanzine(
    playlists: List<PlaylistSummary>,
    onDismiss: () -> Unit,
    onPlaylistSelected: (playlistId: Long) -> Unit,
    onCreatePlaylist: (name: String) -> Unit,
) {
    var newPlaylistName by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Column(Modifier.rotate(-1f).background(FanzineColors.Paper)) {
            Row(Modifier.fillMaxWidth().background(FanzineColors.Red).padding(14.dp, 11.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Filled.PlaylistAdd, contentDescription = null, tint = FanzineColors.Ink)
                Spacer(Modifier.width(8.dp))
                Text("A QUÉ LISTA", fontFamily = FanzineFonts.Anton, fontSize = 20.sp, color = FanzineColors.Ink)
            }
            Column(Modifier.padding(14.dp)) {
                if (playlists.isEmpty()) {
                    Text("Todavía no tienes playlists", fontFamily = FanzineFonts.SpecialElite, fontSize = 13.sp, color = FanzineColors.Grime)
                } else {
                    LazyColumn(Modifier.heightIn(max = 220.dp)) {
                        items(playlists, key = { it.id }) { playlist ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onPlaylistSelected(playlist.id) }
                                    .padding(vertical = 11.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(playlist.name, fontFamily = FanzineFonts.SpecialElite, fontSize = 15.sp, color = FanzineColors.Ink, modifier = Modifier.weight(1f))
                                Text("${playlist.songCount}", fontFamily = FanzineFonts.Anton, fontSize = 15.sp, color = FanzineColors.Red)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text("o hazte una nueva", fontFamily = FanzineFonts.SpecialElite, fontSize = 11.sp, color = FanzineColors.Grime)
                Spacer(Modifier.height(6.dp))
                Box(Modifier.border(2.dp, FanzineColors.Ink).fillMaxWidth().padding(11.dp, 9.dp)) {
                    BasicTextField(
                        value = newPlaylistName,
                        onValueChange = { newPlaylistName = it },
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FanzineFonts.SpecialElite, fontSize = 15.sp, color = FanzineColors.Ink),
                        cursorBrush = SolidColor(FanzineColors.Ink),
                        modifier = Modifier.fillMaxWidth(),
                    )
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
                        "crear y añadir",
                        fontFamily = FanzineFonts.SpecialElite,
                        fontSize = 11.sp,
                        color = FanzineColors.Paper,
                        modifier = Modifier
                            .background(FanzineColors.Ink)
                            .clickable(enabled = newPlaylistName.isNotBlank()) {
                                onCreatePlaylist(newPlaylistName)
                                newPlaylistName = ""
                            }
                            .padding(12.dp, 7.dp),
                    )
                }
            }
        }
    }
}
