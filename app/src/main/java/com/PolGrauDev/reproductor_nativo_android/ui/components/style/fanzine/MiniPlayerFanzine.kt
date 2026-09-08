package com.PolGrauDev.reproductor_nativo_android.ui.components.style.fanzine

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.PolGrauDev.reproductor_nativo_android.data.AlbumArtRequest
import com.PolGrauDev.reproductor_nativo_android.data.model.Song
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.FanzineColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.FanzineFonts

@Composable
fun MiniPlayerFanzine(song: Song, isPlaying: Boolean, onTogglePlayPause: () -> Unit, onClick: () -> Unit) {
    Column {
        Row(Modifier.fillMaxWidth().height(3.dp).background(FanzineColors.Red)) {}
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(FanzineColors.Paper)
                .clickable(onClick = onClick)
                .padding(12.dp, 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = AlbumArtRequest(song.contentUri),
                contentDescription = null,
                modifier = Modifier.size(46.dp),
                contentScale = ContentScale.Crop,
            )
            Spacer(Modifier.width(11.dp))
            Column(Modifier.weight(1f)) {
                Text(song.title.uppercase(), fontFamily = FanzineFonts.Anton, fontSize = 16.sp, color = FanzineColors.Ink, maxLines = 1)
                Text(song.artist, fontFamily = FanzineFonts.SpecialElite, fontSize = 12.sp, color = FanzineColors.Grime, maxLines = 1)
            }
            Spacer(Modifier.width(11.dp))
            Icon(
                if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                tint = FanzineColors.Ink,
                modifier = Modifier
                    .size(40.dp)
                    .rotate(1.5f)
                    .background(FanzineColors.Red)
                    .clickable(onClick = onTogglePlayPause)
                    .padding(9.dp),
            )
        }
    }
}
