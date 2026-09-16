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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.PolGrauDev.reproductor_nativo_android.data.model.Song
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.FanzineColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.FanzineFonts

@Composable
fun EditSongDialogFanzine(song: Song, onDismiss: () -> Unit, onSave: (String, String, String) -> Unit) {
    var title by remember(song.id) { mutableStateOf(song.title) }
    var artist by remember(song.id) { mutableStateOf(song.artist) }
    var album by remember(song.id) { mutableStateOf(song.album) }

    Dialog(onDismissRequest = onDismiss) {
        Column(Modifier.rotate(-1f).background(FanzineColors.Paper)) {
            Row(Modifier.fillMaxWidth().background(FanzineColors.Red).padding(14.dp, 11.dp)) {
                Text("EDITAR CANCIÓN", fontFamily = FanzineFonts.Anton, fontSize = 20.sp, color = FanzineColors.Ink)
            }
            Column(Modifier.padding(14.dp)) {
                FanzineEditField("Título", title) { title = it }
                Spacer(Modifier.height(14.dp))
                FanzineEditField("Artista", artist) { artist = it }
                Spacer(Modifier.height(14.dp))
                FanzineEditField("Álbum", album) { album = it }
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
                        "guardar",
                        fontFamily = FanzineFonts.SpecialElite,
                        fontSize = 11.sp,
                        color = FanzineColors.Paper,
                        modifier = Modifier
                            .background(FanzineColors.Ink)
                            .clickable(enabled = title.isNotBlank()) { onSave(title, artist, album) }
                            .padding(12.dp, 7.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun FanzineEditField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(label, fontFamily = FanzineFonts.SpecialElite, fontSize = 11.sp, color = FanzineColors.Grime)
        Spacer(Modifier.height(6.dp))
        Box(Modifier.border(2.dp, FanzineColors.Ink).fillMaxWidth().padding(11.dp, 9.dp)) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = TextStyle(fontFamily = FanzineFonts.SpecialElite, fontSize = 15.sp, color = FanzineColors.Ink),
                cursorBrush = SolidColor(FanzineColors.Ink),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
