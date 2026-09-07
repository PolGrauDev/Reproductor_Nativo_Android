package com.PolGrauDev.reproductor_nativo_android.ui.screens.style.fanzine

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.PolGrauDev.reproductor_nativo_android.data.AlbumArtRequest
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.FanzineColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.FanzineFonts
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.fanzineTilt
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.photocopyGrain
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicViewModel

@Composable
fun QueueScreenFanzine(viewModel: MusicViewModel, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val queue = uiState.queue
    val isSearching = uiState.queueSearchQuery.isNotBlank()
    val displayQueue = if (isSearching) uiState.filteredQueue else queue
    val currentIndex = uiState.playback.currentIndex

    Column(Modifier.fillMaxSize().background(FanzineColors.Slate).photocopyGrain()) {
        Row(Modifier.fillMaxWidth().padding(14.dp, 12.dp, 14.dp, 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = FanzineColors.Ink,
                modifier = Modifier.size(36.dp).rotate(-2f).background(FanzineColors.Paper).clickable(onClick = onBack).padding(7.dp),
            )
            Spacer(Modifier.width(9.dp))
            Row {
                Text("La", fontFamily = FanzineFonts.Anton, fontSize = 22.sp, color = FanzineColors.Ink, modifier = Modifier.rotate(-2f).background(FanzineColors.Paper).padding(horizontal = 4.dp))
                Spacer(Modifier.width(2.dp))
                Text("cola", fontFamily = FanzineFonts.Anton, fontSize = 20.sp, color = FanzineColors.Paper, modifier = Modifier.rotate(2.5f).background(FanzineColors.Red).padding(horizontal = 4.dp))
            }
            Spacer(Modifier.width(9.dp))
            Text("${queue.size} en fila", fontFamily = FanzineFonts.SpecialElite, fontSize = 11.sp, color = FanzineColors.Faded)
        }
        FanzineQueueSearchField(query = uiState.queueSearchQuery, onQueryChange = viewModel::setQueueSearchQuery)
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            itemsIndexed(displayQueue, key = { index, song -> "$index-${song.id}" }) { index, song ->
                val realIndex = if (isSearching) queue.indexOf(song).coerceAtLeast(0) else index
                val isCurrent = realIndex == currentIndex
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .rotate(fanzineTilt(index))
                        .then(if (isCurrent) Modifier.background(FanzineColors.Red) else Modifier.border(2.dp, FanzineColors.Hair))
                        .clickable { viewModel.playQueueItem(realIndex) }
                        .padding(11.dp, 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AsyncImage(model = AlbumArtRequest(song.contentUri), contentDescription = null, modifier = Modifier.size(40.dp), contentScale = ContentScale.Crop)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(song.title.uppercase(), fontFamily = FanzineFonts.Anton, fontSize = 15.sp, color = if (isCurrent) FanzineColors.Ink else FanzineColors.Paper, maxLines = 1)
                        if (isCurrent) {
                            Text("suena ahora", fontFamily = FanzineFonts.SpecialElite, fontSize = 11.sp, color = FanzineColors.Ink)
                        } else {
                            Text(song.artist, fontFamily = FanzineFonts.SpecialElite, fontSize = 12.sp, color = FanzineColors.Faded, maxLines = 1)
                        }
                    }
                    if (isCurrent) {
                        Icon(Icons.Filled.MusicNote, contentDescription = null, tint = FanzineColors.Ink, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                    }
                    if (!isSearching) {
                        val tint = if (isCurrent) FanzineColors.Ink else FanzineColors.Paper
                        Icon(
                            Icons.Filled.KeyboardArrowUp,
                            contentDescription = "Subir",
                            tint = if (index > 0) tint else tint.copy(alpha = 0.4f),
                            modifier = Modifier.clickable(enabled = index > 0) { viewModel.moveQueueItem(realIndex, realIndex - 1) }.padding(4.dp),
                        )
                        Icon(
                            Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Bajar",
                            tint = if (index < displayQueue.lastIndex) tint else tint.copy(alpha = 0.4f),
                            modifier = Modifier.clickable(enabled = index < displayQueue.lastIndex) { viewModel.moveQueueItem(realIndex, realIndex + 1) }.padding(4.dp),
                        )
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Quitar de la cola",
                            tint = if (isCurrent) FanzineColors.Ink else FanzineColors.Red,
                            modifier = Modifier.clickable { viewModel.removeFromQueue(realIndex) }.padding(4.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FanzineQueueSearchField(query: String, onQueryChange: (String) -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(14.dp, 4.dp, 14.dp, 8.dp)
            .rotate(-0.7f)
            .background(FanzineColors.Paper),
    ) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 11.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Search, contentDescription = null, tint = FanzineColors.Red)
            Spacer(Modifier.width(9.dp))
            Box(Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text("buscar en la cola…", fontFamily = FanzineFonts.SpecialElite, fontSize = 13.sp, color = FanzineColors.Grime)
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = TextStyle(fontFamily = FanzineFonts.SpecialElite, fontSize = 13.sp, color = FanzineColors.Ink),
                    cursorBrush = SolidColor(FanzineColors.Ink),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (query.isNotEmpty()) {
                Icon(Icons.Filled.Close, contentDescription = "Limpiar búsqueda", tint = FanzineColors.Grime, modifier = Modifier.clickable { onQueryChange("") })
            }
        }
    }
}
