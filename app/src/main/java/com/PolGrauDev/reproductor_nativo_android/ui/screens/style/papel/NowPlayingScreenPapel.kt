package com.PolGrauDev.reproductor_nativo_android.ui.screens.style.papel

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import coil3.compose.AsyncImage
import com.PolGrauDev.reproductor_nativo_android.data.AlbumArtRequest
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelSectionLabel
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelType
import com.PolGrauDev.reproductor_nativo_android.ui.util.formatMillis
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicUiState
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicViewModel

@Composable
fun NowPlayingScreenPapel(viewModel: MusicViewModel, uiState: MusicUiState, onBack: () -> Unit, onQueueClick: () -> Unit) {
    val song = uiState.currentSong
    var isUserSeeking by remember { mutableStateOf(false) }
    var seekFraction by remember { mutableFloatStateOf(0f) }

    Scaffold(containerColor = PapelColors.Background) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp, start = 4.dp, end = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = PapelColors.OnSurfaceVariant)
                }
                PapelSectionLabel("Reproduciendo", modifier = Modifier.weight(1f).padding(start = 0.dp))
                Icon(
                    Icons.AutoMirrored.Filled.QueueMusic,
                    contentDescription = "Ver cola",
                    tint = PapelColors.OnSurfaceVariant,
                    modifier = Modifier.clickable(onClick = onQueueClick),
                )
            }

            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp, 12.dp, 32.dp, 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val rotation = rememberInfiniteTransition(label = "vinyl")
                val angle by rotation.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(tween(11_000, easing = LinearEasing)),
                    label = "vinylAngle",
                )
                Box(
                    modifier = Modifier.fillMaxWidth(0.7f).aspectRatio(1f).background(PapelColors.SurfaceVariant, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    AsyncImage(
                        model = song?.contentUri?.let { AlbumArtRequest(it) },
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .aspectRatio(1f)
                            .clip(CircleShape)
                            .rotate(if (uiState.playback.isPlaying) angle else 0f),
                        contentScale = ContentScale.Crop,
                    )
                }

                Spacer(Modifier.height(30.dp))
                Text(song?.title ?: "-", style = PapelType.DisplaySmall, color = PapelColors.OnSurface, maxLines = 1)
                Text(
                    (song?.artist ?: "-").uppercase(),
                    style = PapelType.SectionLabel,
                    color = PapelColors.OnSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp),
                )
                Spacer(Modifier.height(26.dp))

                val durationMs = uiState.playback.durationMs.coerceAtLeast(1L)
                val playedFraction = if (isUserSeeking) seekFraction else (uiState.playback.positionMs.toFloat() / durationMs).coerceIn(0f, 1f)
                Slider(
                    value = playedFraction,
                    onValueChange = { isUserSeeking = true; seekFraction = it },
                    onValueChangeFinished = {
                        viewModel.seekTo((seekFraction * durationMs).toLong())
                        isUserSeeking = false
                    },
                    colors = SliderDefaults.colors(thumbColor = PapelColors.Primary, activeTrackColor = PapelColors.Primary, inactiveTrackColor = PapelColors.Outline),
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(formatMillis(uiState.playback.positionMs), style = PapelType.Mono, color = PapelColors.OnSurfaceVariant)
                    Text(formatMillis(uiState.playback.durationMs), style = PapelType.Mono, color = PapelColors.OnSurfaceVariant)
                }

                Spacer(Modifier.height(30.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.SkipPrevious,
                        contentDescription = "Anterior",
                        tint = PapelColors.OnSurface,
                        modifier = Modifier.size(28.dp).clickable(onClick = viewModel::skipPrevious),
                    )
                    Spacer(Modifier.width(26.dp))
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(PapelColors.Background, CircleShape)
                            .border(BorderStroke(1.dp, PapelColors.Primary), CircleShape)
                            .clickable(onClick = viewModel::togglePlayPause),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            if (uiState.playback.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (uiState.playback.isPlaying) "Pausar" else "Reproducir",
                            tint = PapelColors.Primary,
                        )
                    }
                    Spacer(Modifier.width(26.dp))
                    Icon(
                        Icons.Filled.SkipNext,
                        contentDescription = "Siguiente",
                        tint = PapelColors.OnSurface,
                        modifier = Modifier.size(28.dp).clickable(onClick = viewModel::skipNext),
                    )
                }

                Spacer(Modifier.height(26.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(28.dp)) {
                    PapelPlaybackToggle(
                        icon = Icons.Filled.Shuffle,
                        label = "Aleatorio",
                        active = uiState.playback.shuffleModeEnabled,
                        onClick = viewModel::toggleShuffle,
                    )
                    PapelPlaybackToggle(
                        icon = if (uiState.playback.repeatMode == Player.REPEAT_MODE_ONE) Icons.Filled.RepeatOne else Icons.Filled.Repeat,
                        label = "Repetir",
                        active = uiState.playback.repeatMode != Player.REPEAT_MODE_OFF,
                        onClick = viewModel::cycleRepeatMode,
                    )
                    val isFavorite = song != null && song.id in uiState.favoriteSongIds
                    PapelPlaybackToggle(
                        icon = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        label = "Favorito",
                        active = isFavorite,
                        onClick = { song?.let { viewModel.toggleFavorite(it.id) } },
                    )
                }
            }
        }
    }
}

@Composable
private fun PapelPlaybackToggle(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, active: Boolean, onClick: () -> Unit) {
    val color = if (active) PapelColors.Accent else PapelColors.Faint
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick)) {
        Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(20.dp))
        Spacer(Modifier.height(5.dp))
        Text(label.uppercase(), style = PapelType.SectionLabel, color = color)
    }
}
