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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.PolGrauDev.reproductor_nativo_android.data.model.Song
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.StickerColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.StickerHardShadowBox
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.StickerType

@Composable
fun AddSongsToPlaylistDialogSticker(
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
        StickerHardShadowBox(shape = RoundedCornerShape(26.dp), borderWidth = 4.dp, shadowOffsetX = 7.dp, shadowOffsetY = 7.dp) {
            Column(Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth().background(StickerColors.Grape, RoundedCornerShape(22.dp, 22.dp, 0.dp, 0.dp)).padding(16.dp, 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.AutoMirrored.Filled.PlaylistAdd, contentDescription = null, tint = StickerColors.Ink)
                    Spacer(Modifier.width(9.dp))
                    Text("Añadir canciones", style = StickerType.TitleMedium.let { it.copy(fontSize = 20.sp) }, color = StickerColors.Ink)
                }
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Search, contentDescription = null, tint = StickerColors.Pink)
                        Spacer(Modifier.width(10.dp))
                        Box(Modifier.weight(1f)) {
                            if (query.isEmpty()) {
                                Text("busca lo que te apetezca…", style = StickerType.HandwrittenSmall, color = StickerColors.Faded)
                            }
                            BasicTextField(
                                value = query,
                                onValueChange = { query = it },
                                singleLine = true,
                                textStyle = StickerType.HandwrittenSmall.copy(color = StickerColors.Ink),
                                cursorBrush = SolidColor(StickerColors.Ink),
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    if (visible.isEmpty()) {
                        Text("No hay canciones para añadir", style = StickerType.HandwrittenSmall, color = StickerColors.Faded)
                    } else {
                        LazyColumn(Modifier.heightIn(max = 260.dp)) {
                            items(visible, key = { it.id }) { song ->
                                val isSelected = song.id in selected
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(StickerColors.Paper, RoundedCornerShape(16.dp))
                                        .clickable { selected = if (isSelected) selected - song.id else selected + song.id }
                                        .padding(12.dp, 9.dp)
                                        .padding(bottom = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Column(Modifier.weight(1f)) {
                                        Text(song.title, style = StickerType.TitleMedium, color = StickerColors.Ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(song.artist, style = StickerType.HandwrittenSmall, color = StickerColors.Faded, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                    Icon(
                                        if (isSelected) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (isSelected) StickerColors.Pink else StickerColors.Faded,
                                    )
                                }
                                Spacer(Modifier.height(9.dp))
                            }
                        }
                    }
                    Spacer(Modifier.height(6.dp))
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
                                "añadir (${selected.size})",
                                style = StickerType.TitleMedium.let { it.copy(fontSize = 15.sp) },
                                color = Color.White,
                                modifier = Modifier
                                    .clickable(enabled = selected.isNotEmpty()) { onAddSongs(selected) }
                                    .padding(13.dp, 8.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
