package com.PolGrauDev.reproductor_nativo_android.ui.screens.style.sticker

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.PolGrauDev.reproductor_nativo_android.data.AlbumArtRequest
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.StickerColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.StickerHardShadowBox
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.StickerType
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.stickerTilt
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicViewModel

@Composable
fun QueueScreenSticker(viewModel: MusicViewModel, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val queue = uiState.queue
    val isSearching = uiState.queueSearchQuery.isNotBlank()
    val displayQueue = if (isSearching) uiState.filteredQueue else queue
    val currentIndex = uiState.playback.currentIndex

    Column(Modifier.fillMaxSize().background(StickerColors.Paper)) {
        Row(Modifier.fillMaxWidth().padding(16.dp, 14.dp, 16.dp, 0.dp), verticalAlignment = Alignment.CenterVertically) {
            StickerHardShadowBox(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(14.dp)) {
                Box(Modifier.fillMaxSize().clickable(onClick = onBack), contentAlignment = Alignment.Center) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = StickerColors.Ink)
                }
            }
            Spacer(Modifier.width(10.dp))
            Text("La cola", style = StickerType.HeadlineLarge, color = StickerColors.Ink)
        }
        Text(
            "${queue.size} esperando su turno ～",
            style = StickerType.Handwritten,
            color = StickerColors.Faded,
            modifier = Modifier.padding(16.dp, 8.dp, 16.dp, 4.dp),
        )
        StickerQueueSearchField(query = uiState.queueSearchQuery, onQueryChange = viewModel::setQueueSearchQuery)
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            itemsIndexed(displayQueue, key = { index, song -> "$index-${song.id}" }) { index, song ->
                val realIndex = if (isSearching) queue.indexOf(song).coerceAtLeast(0) else index
                val isCurrent = realIndex == currentIndex
                StickerHardShadowBox(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    backgroundColor = if (isCurrent) StickerColors.Pink else Color.White,
                    rotationDegrees = stickerTilt(index),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { viewModel.playQueueItem(realIndex) }.padding(9.dp, 9.dp, 6.dp, 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AsyncImage(model = AlbumArtRequest(song.contentUri), contentDescription = null, modifier = Modifier.size(40.dp).clip(RoundedCornerShape(13.dp)), contentScale = ContentScale.Crop)
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(song.title, style = StickerType.TitleMedium.let { it.copy(fontSize = 16.sp) }, color = if (isCurrent) Color.White else StickerColors.Ink, maxLines = 1)
                            if (isCurrent) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.MusicNote, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("¡suena ahora!", style = StickerType.HandwrittenSmall, color = Color.White)
                                }
                            } else {
                                Text(song.artist, style = StickerType.HandwrittenSmall, color = StickerColors.Faded, maxLines = 1)
                            }
                        }
                        if (!isSearching) {
                            val iconTint = if (isCurrent) Color.White else StickerColors.Ink
                            Icon(
                                Icons.Filled.KeyboardArrowUp,
                                contentDescription = "Subir",
                                tint = if (index > 0) iconTint else iconTint.copy(alpha = 0.4f),
                                modifier = Modifier.clickable(enabled = index > 0) { viewModel.moveQueueItem(realIndex, realIndex - 1) }.padding(4.dp),
                            )
                            Icon(
                                Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Bajar",
                                tint = if (index < displayQueue.lastIndex) iconTint else iconTint.copy(alpha = 0.4f),
                                modifier = Modifier.clickable(enabled = index < displayQueue.lastIndex) { viewModel.moveQueueItem(realIndex, realIndex + 1) }.padding(4.dp),
                            )
                        }
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Quitar de la cola",
                            tint = if (isCurrent) Color.White else StickerColors.Pink,
                            modifier = Modifier.clickable { viewModel.removeFromQueue(realIndex) }.padding(4.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StickerQueueSearchField(query: String, onQueryChange: (String) -> Unit) {
    StickerHardShadowBox(
        modifier = Modifier.fillMaxWidth().padding(16.dp, 10.dp, 16.dp, 4.dp),
        shape = RoundedCornerShape(25.dp),
    ) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Search, contentDescription = null, tint = StickerColors.Pink)
            Spacer(Modifier.width(10.dp))
            Box(Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text("busca en la cola…", style = StickerType.HandwrittenSmall, color = StickerColors.Faded)
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = StickerType.HandwrittenSmall.copy(color = StickerColors.Ink),
                    cursorBrush = SolidColor(StickerColors.Ink),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (query.isNotEmpty()) {
                Icon(Icons.Filled.Close, contentDescription = "Limpiar búsqueda", tint = StickerColors.Faded, modifier = Modifier.clickable { onQueryChange("") })
            }
        }
    }
}
