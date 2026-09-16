package com.PolGrauDev.reproductor_nativo_android.ui.components.style.papel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.PolGrauDev.reproductor_nativo_android.data.model.Song
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelRowDivider
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelSectionLabel
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelType

@Composable
fun EditSongDialogPapel(song: Song, onDismiss: () -> Unit, onSave: (String, String, String) -> Unit) {
    var title by remember(song.id) { mutableStateOf(song.title) }
    var artist by remember(song.id) { mutableStateOf(song.artist) }
    var album by remember(song.id) { mutableStateOf(song.album) }

    Dialog(onDismissRequest = onDismiss) {
        Column(Modifier.background(PapelColors.Surface).padding(22.dp)) {
            PapelSectionLabel("Editar")
            Text("Información de la canción", style = PapelType.TitleLarge, color = PapelColors.OnSurface)
            Spacer(Modifier.height(14.dp))

            PapelSectionLabel("Título")
            BasicTextField(
                value = title,
                onValueChange = { title = it },
                singleLine = true,
                textStyle = PapelType.BodyMedium.copy(color = PapelColors.OnSurface),
                cursorBrush = SolidColor(PapelColors.Primary),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            PapelRowDivider()
            Spacer(Modifier.height(14.dp))

            PapelSectionLabel("Artista")
            BasicTextField(
                value = artist,
                onValueChange = { artist = it },
                singleLine = true,
                textStyle = PapelType.BodyMedium.copy(color = PapelColors.OnSurface),
                cursorBrush = SolidColor(PapelColors.Primary),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            PapelRowDivider()
            Spacer(Modifier.height(14.dp))

            PapelSectionLabel("Álbum")
            BasicTextField(
                value = album,
                onValueChange = { album = it },
                singleLine = true,
                textStyle = PapelType.BodyMedium.copy(color = PapelColors.OnSurface),
                cursorBrush = SolidColor(PapelColors.Primary),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            PapelRowDivider()

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
                    "GUARDAR",
                    style = PapelType.SectionLabel,
                    color = if (title.isNotBlank()) PapelColors.OnSurface else PapelColors.Faint,
                    modifier = Modifier
                        .clickable(enabled = title.isNotBlank()) { onSave(title, artist, album) }
                        .padding(8.dp),
                )
            }
        }
    }
}
