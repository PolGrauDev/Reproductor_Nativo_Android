package com.PolGrauDev.reproductor_nativo_android.ui.screens.style.papel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.PolGrauDev.reproductor_nativo_android.data.AlbumArtRequest
import com.PolGrauDev.reproductor_nativo_android.data.model.Song
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelRowDivider
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelType
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicViewModel

@Composable
fun QueueScreenPapel(viewModel: MusicViewModel, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val queue = uiState.queue
    val isSearching = uiState.queueSearchQuery.isNotBlank()
    val displayQueue = if (isSearching) uiState.filteredQueue else queue
    val canReorder = !isSearching && !uiState.playback.shuffleModeEnabled

    Scaffold(containerColor = PapelColors.Background) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Row(Modifier.fillMaxWidth().padding(top = 10.dp, start = 4.dp, end = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = PapelColors.OnSurfaceVariant)
                }
            }
            Column(Modifier.fillMaxWidth().padding(20.dp, 2.dp, 20.dp, 16.dp)) {
                Text("A continuación", style = PapelType.HeadlineLarge, color = PapelColors.OnSurface)
                Text(
                    "${queue.size} pistas en cola",
                    style = PapelType.SectionLabel,
                    color = PapelColors.OnSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
            PapelQueueSearchField(query = uiState.queueSearchQuery, onQueryChange = viewModel::setQueueSearchQuery)
            LazyColumn(Modifier.fillMaxSize()) {
                itemsIndexed(displayQueue, key = { index, song -> "$index-${song.id}" }) { index, song ->
                    val realIndex = if (isSearching) queue.indexOf(song).coerceAtLeast(0) else index
                    PapelQueueRow(
                        song = song,
                        isCurrent = song.id.toString() == uiState.playback.currentMediaId,
                        canReorder = canReorder,
                        canMoveUp = index > 0,
                        canMoveDown = index < displayQueue.lastIndex,
                        onClick = { viewModel.playQueueItem(song.id.toString()) },
                        onMoveUp = { viewModel.moveQueueItem(realIndex, realIndex - 1) },
                        onMoveDown = { viewModel.moveQueueItem(realIndex, realIndex + 1) },
                        onRemove = { viewModel.removeFromQueue(song.id.toString()) },
                    )
                }
            }
        }
    }
}

@Composable
private fun PapelQueueSearchField(query: String, onQueryChange: (String) -> Unit) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 0.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Filled.Search, contentDescription = null, tint = PapelColors.OnSurfaceVariant, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(10.dp))
            Box(Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text("Buscar en la cola", style = PapelType.BodyMedium, color = PapelColors.OnSurfaceVariant)
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = PapelType.BodyMedium.copy(color = PapelColors.OnSurface),
                    cursorBrush = SolidColor(PapelColors.Primary),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (query.isNotEmpty()) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Limpiar búsqueda",
                    tint = PapelColors.OnSurfaceVariant,
                    modifier = Modifier.size(18.dp).clickable { onQueryChange("") },
                )
            }
        }
        PapelRowDivider()
    }
}

@Composable
private fun PapelQueueRow(
    song: Song,
    isCurrent: Boolean,
    canReorder: Boolean,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onClick: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onRemove: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (isCurrent) PapelColors.SurfaceVariant else PapelColors.Background)
                .clickable(onClick = onClick)
                .padding(20.dp, 14.dp, 20.dp, 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = AlbumArtRequest(song.contentUri),
                contentDescription = null,
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(2.dp)),
                contentScale = ContentScale.Crop,
            )
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(song.title, style = PapelType.TitleMedium, color = PapelColors.OnSurface, maxLines = 1)
                if (isCurrent) {
                    Text("Sonando ahora", style = PapelType.SectionLabel, color = PapelColors.Accent)
                } else {
                    Text(song.artist, style = PapelType.BodySmall, color = PapelColors.OnSurfaceVariant, maxLines = 1)
                }
            }
            if (canReorder) {
                Icon(
                    Icons.Filled.KeyboardArrowUp,
                    contentDescription = "Subir",
                    tint = if (canMoveUp) PapelColors.OnSurface else PapelColors.Faint,
                    modifier = Modifier.clickable(enabled = canMoveUp, onClick = onMoveUp).padding(6.dp),
                )
                Icon(
                    Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Bajar",
                    tint = if (canMoveDown) PapelColors.OnSurface else PapelColors.Faint,
                    modifier = Modifier.clickable(enabled = canMoveDown, onClick = onMoveDown).padding(6.dp),
                )
            }
            Icon(
                Icons.Filled.Close,
                contentDescription = "Quitar de la cola",
                tint = PapelColors.OnSurfaceVariant,
                modifier = Modifier.clickable(onClick = onRemove).padding(6.dp),
            )
        }
        PapelRowDivider()
    }
}
