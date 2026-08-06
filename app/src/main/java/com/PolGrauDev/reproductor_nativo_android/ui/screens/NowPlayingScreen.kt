package com.PolGrauDev.reproductor_nativo_android.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import coil3.compose.AsyncImage
import com.PolGrauDev.reproductor_nativo_android.data.AlbumArtRequest
import com.PolGrauDev.reproductor_nativo_android.ui.util.formatMillis
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreen(viewModel: MusicViewModel, onBack: () -> Unit, onQueueClick: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val song = uiState.currentSong

    var isUserSeeking by remember { mutableStateOf(false) }
    var seekFraction by remember { mutableFloatStateOf(0f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(song?.title ?: "Reproductor") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (song != null) {
                        val isFavorite = song.id in uiState.favoriteSongIds
                        IconButton(onClick = { viewModel.toggleFavorite(song.id) }) {
                            Icon(
                                if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = if (isFavorite) "Quitar de favoritos" else "Añadir a favoritos",
                                tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    IconButton(onClick = onQueueClick) {
                        Icon(Icons.AutoMirrored.Filled.QueueMusic, contentDescription = "Ver cola")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AsyncImage(
                model = song?.contentUri?.let { AlbumArtRequest(it) },
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
            )

            Spacer(Modifier.height(24.dp))
            Text(song?.title ?: "-", style = MaterialTheme.typography.headlineSmall, maxLines = 1)
            Text(
                song?.artist ?: "-",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
            Spacer(Modifier.height(16.dp))

            val durationMs = uiState.playback.durationMs.coerceAtLeast(1L)
            val playedFraction = if (isUserSeeking) {
                seekFraction
            } else {
                (uiState.playback.positionMs.toFloat() / durationMs).coerceIn(0f, 1f)
            }

            Slider(
                value = playedFraction,
                onValueChange = {
                    isUserSeeking = true
                    seekFraction = it
                },
                onValueChangeFinished = {
                    viewModel.seekTo((seekFraction * durationMs).toLong())
                    isUserSeeking = false
                },
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(formatMillis(uiState.playback.positionMs))
                Text(formatMillis(uiState.playback.durationMs))
            }

            Spacer(Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = viewModel::skipPrevious) {
                    Icon(Icons.Filled.SkipPrevious, contentDescription = "Anterior")
                }
                FilledIconButton(
                    onClick = viewModel::togglePlayPause,
                    modifier = Modifier.size(64.dp),
                ) {
                    Icon(
                        imageVector = if (uiState.playback.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (uiState.playback.isPlaying) "Pausar" else "Reproducir",
                    )
                }
                IconButton(onClick = viewModel::skipNext) {
                    Icon(Icons.Filled.SkipNext, contentDescription = "Siguiente")
                }
            }

            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                val activeColor = MaterialTheme.colorScheme.primary
                val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant

                IconButton(onClick = viewModel::toggleShuffle) {
                    Icon(
                        Icons.Filled.Shuffle,
                        contentDescription = "Aleatorio",
                        tint = if (uiState.playback.shuffleModeEnabled) activeColor else inactiveColor,
                    )
                }
                IconButton(onClick = viewModel::cycleRepeatMode) {
                    Icon(
                        imageVector = if (uiState.playback.repeatMode == Player.REPEAT_MODE_ONE) {
                            Icons.Filled.RepeatOne
                        } else {
                            Icons.Filled.Repeat
                        },
                        contentDescription = "Repetir",
                        tint = if (uiState.playback.repeatMode == Player.REPEAT_MODE_OFF) inactiveColor else activeColor,
                    )
                }
            }
        }
    }
}
