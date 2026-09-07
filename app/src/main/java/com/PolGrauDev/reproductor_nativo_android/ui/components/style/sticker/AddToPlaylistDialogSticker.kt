package com.PolGrauDev.reproductor_nativo_android.ui.components.style.sticker

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.PolGrauDev.reproductor_nativo_android.data.model.PlaylistSummary
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.StickerColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.StickerHardShadowBox
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.StickerType

@Composable
fun AddToPlaylistDialogSticker(
    playlists: List<PlaylistSummary>,
    onDismiss: () -> Unit,
    onPlaylistSelected: (playlistId: Long) -> Unit,
    onCreatePlaylist: (name: String) -> Unit,
) {
    var newPlaylistName by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        StickerHardShadowBox(shape = RoundedCornerShape(26.dp), borderWidth = 4.dp, shadowOffsetX = 7.dp, shadowOffsetY = 7.dp) {
            Column(Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth().background(StickerColors.Grape, RoundedCornerShape(22.dp, 22.dp, 0.dp, 0.dp)).padding(16.dp, 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.AutoMirrored.Filled.PlaylistAdd, contentDescription = null, tint = StickerColors.Ink)
                    Spacer(Modifier.width(9.dp))
                    Text("¿A qué lista?", style = StickerType.TitleMedium.let { it.copy(fontSize = 20.sp) }, color = StickerColors.Ink)
                }
                Column(Modifier.padding(16.dp)) {
                    if (playlists.isEmpty()) {
                        Text("Todavía no tienes playlists", style = StickerType.HandwrittenSmall, color = StickerColors.Faded)
                    } else {
                        LazyColumn(Modifier.heightIn(max = 220.dp)) {
                            items(playlists, key = { it.id }) { playlist ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(StickerColors.Paper, RoundedCornerShape(16.dp))
                                        .clickable { onPlaylistSelected(playlist.id) }
                                        .padding(12.dp, 9.dp)
                                        .padding(bottom = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(playlist.name, style = StickerType.TitleMedium, color = StickerColors.Ink, modifier = Modifier.weight(1f))
                                    Box(
                                        Modifier
                                            .background(StickerColors.Butter, RoundedCornerShape(11.dp))
                                            .padding(horizontal = 9.dp, vertical = 2.dp),
                                    ) { Text("${playlist.songCount}", style = StickerType.HandwrittenSmall, color = StickerColors.Ink) }
                                }
                                Spacer(Modifier.height(9.dp))
                            }
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Text("o inventa una nueva ✎", style = StickerType.HandwrittenSmall, color = StickerColors.Faded)
                    Spacer(Modifier.height(6.dp))
                    Box(Modifier.background(Color.White, RoundedCornerShape(16.dp)).fillMaxWidth().padding(12.dp, 10.dp)) {
                        BasicTextField(
                            value = newPlaylistName,
                            onValueChange = { newPlaylistName = it },
                            singleLine = true,
                            textStyle = StickerType.HandwrittenSmall.copy(color = StickerColors.Ink),
                            cursorBrush = SolidColor(StickerColors.Ink),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        StickerHardShadowBox(shape = RoundedCornerShape(15.dp)) {
                            Text(
                                "cancelar",
                                style = StickerType.HandwrittenSmall,
                                color = StickerColors.Faded,
                                modifier = Modifier.clickable(onClick = onDismiss).padding(13.dp, 8.dp),
                            )
                        }
                        Spacer(Modifier.width(9.dp))
                        StickerHardShadowBox(shape = RoundedCornerShape(15.dp), backgroundColor = StickerColors.Pink) {
                            Text(
                                "crear y añadir",
                                style = StickerType.TitleMedium.let { it.copy(fontSize = 15.sp) },
                                color = Color.White,
                                modifier = Modifier
                                    .clickable(enabled = newPlaylistName.isNotBlank()) {
                                        onCreatePlaylist(newPlaylistName)
                                        newPlaylistName = ""
                                    }
                                    .padding(13.dp, 8.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
