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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
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
import com.PolGrauDev.reproductor_nativo_android.data.model.Song
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.StickerColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.StickerHardShadowBox
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.StickerType

@Composable
fun EditSongDialogSticker(song: Song, onDismiss: () -> Unit, onSave: (String, String, String) -> Unit) {
    var title by remember(song.id) { mutableStateOf(song.title) }
    var artist by remember(song.id) { mutableStateOf(song.artist) }
    var album by remember(song.id) { mutableStateOf(song.album) }

    Dialog(onDismissRequest = onDismiss) {
        StickerHardShadowBox(shape = RoundedCornerShape(26.dp), borderWidth = 4.dp, shadowOffsetX = 7.dp, shadowOffsetY = 7.dp) {
            Column(Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth().background(StickerColors.Grape, RoundedCornerShape(22.dp, 22.dp, 0.dp, 0.dp)).padding(16.dp, 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = null, tint = StickerColors.Ink)
                    Spacer(Modifier.width(9.dp))
                    Text("Editar canción", style = StickerType.TitleMedium.let { it.copy(fontSize = 20.sp) }, color = StickerColors.Ink)
                }
                Column(Modifier.padding(16.dp)) {
                    StickerEditField("Título", title) { title = it }
                    Spacer(Modifier.height(12.dp))
                    StickerEditField("Artista", artist) { artist = it }
                    Spacer(Modifier.height(12.dp))
                    StickerEditField("Álbum", album) { album = it }
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
                                "guardar",
                                style = StickerType.TitleMedium.let { it.copy(fontSize = 15.sp) },
                                color = Color.White,
                                modifier = Modifier
                                    .clickable(enabled = title.isNotBlank()) { onSave(title, artist, album) }
                                    .padding(13.dp, 8.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StickerEditField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(label, style = StickerType.HandwrittenSmall, color = StickerColors.Faded)
        Spacer(Modifier.height(4.dp))
        Box(Modifier.background(Color.White, RoundedCornerShape(16.dp)).fillMaxWidth().padding(12.dp, 10.dp)) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = StickerType.HandwrittenSmall.copy(color = StickerColors.Ink),
                cursorBrush = SolidColor(StickerColors.Ink),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
