package com.PolGrauDev.reproductor_nativo_android.ui.screens.style.fanzine

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.sp
import androidx.media3.common.Player
import coil3.compose.AsyncImage
import com.PolGrauDev.reproductor_nativo_android.data.AlbumArtRequest
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.FanzineColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.FanzineFonts
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.photocopyGrain
import com.PolGrauDev.reproductor_nativo_android.ui.util.formatMillis
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicUiState
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicViewModel

@Composable
fun NowPlayingScreenFanzine(viewModel: MusicViewModel, uiState: MusicUiState, onBack: () -> Unit, onQueueClick: () -> Unit, onSearchClick: () -> Unit) {
    val song = uiState.currentSong
    var isUserSeeking by remember { mutableStateOf(false) }
    var seekFraction by remember { mutableFloatStateOf(0f) }

    Column(Modifier.fillMaxSize().background(FanzineColors.Slate).photocopyGrain()) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp, 12.dp, 14.dp, 0.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = FanzineColors.Ink,
                modifier = Modifier.size(36.dp).rotate(-2f).background(FanzineColors.Paper).clickable(onClick = onBack).padding(7.dp),
            )
            Text(
                "suena / a todo trapo",
                fontFamily = FanzineFonts.SpecialElite,
                fontSize = 12.sp,
                color = FanzineColors.Faded,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            Icon(
                Icons.Filled.Search,
                contentDescription = "Buscar",
                tint = FanzineColors.Ink,
                modifier = Modifier.size(36.dp).rotate(-2f).background(FanzineColors.Paper).clickable(onClick = onSearchClick).padding(7.dp),
            )
            Spacer(Modifier.width(10.dp))
            Icon(
                Icons.AutoMirrored.Filled.QueueMusic,
                contentDescription = "Ver cola",
                tint = FanzineColors.Ink,
                modifier = Modifier.size(36.dp).rotate(2f).background(FanzineColors.Paper).clickable(onClick = onQueueClick).padding(7.dp),
            )
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(28.dp, 14.dp, 28.dp, 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val rotation = rememberInfiniteTransition(label = "vinyl")
            val angle by rotation.animateFloat(0f, 360f, infiniteRepeatable(tween(3_600, easing = LinearEasing)), label = "vinylAngle")
            Box(modifier = Modifier.fillMaxWidth(0.72f).aspectRatio(1f), contentAlignment = Alignment.Center) {
                Box(
                    Modifier
                        .fillMaxWidth(0.92f)
                        .aspectRatio(1f)
                        .background(FanzineColors.Grime, CircleShape),
                )
                AsyncImage(
                    model = song?.contentUri?.let { AlbumArtRequest(it) },
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .rotate(if (uiState.playback.isPlaying) angle else 0f),
                    contentScale = ContentScale.Crop,
                )
                Text(
                    "A TODO VOLUMEN",
                    fontFamily = FanzineFonts.Anton,
                    fontSize = 12.sp,
                    color = FanzineColors.Ink,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .rotate(-5f)
                        .background(FanzineColors.Red)
                        .padding(horizontal = 7.dp, vertical = 3.dp),
                )
            }

            Spacer(Modifier.height(20.dp))
            Text((song?.title ?: "-").uppercase(), fontFamily = FanzineFonts.Anton, fontSize = 32.sp, color = FanzineColors.Ink, maxLines = 1)
            Text((song?.artist ?: "-").uppercase(), fontFamily = FanzineFonts.SpecialElite, fontSize = 13.sp, color = FanzineColors.Faded, maxLines = 1, modifier = Modifier.padding(top = 6.dp))
            Spacer(Modifier.height(18.dp))

            val durationMs = uiState.playback.durationMs.coerceAtLeast(1L)
            val playedFraction = if (isUserSeeking) seekFraction else (uiState.playback.positionMs.toFloat() / durationMs).coerceIn(0f, 1f)
            Slider(
                value = playedFraction,
                onValueChange = { isUserSeeking = true; seekFraction = it },
                onValueChangeFinished = {
                    viewModel.seekTo((seekFraction * durationMs).toLong())
                    isUserSeeking = false
                },
                colors = SliderDefaults.colors(thumbColor = FanzineColors.Paper, activeTrackColor = FanzineColors.Red, inactiveTrackColor = FanzineColors.Grime),
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(formatMillis(uiState.playback.positionMs), fontFamily = FanzineFonts.SpecialElite, fontSize = 11.sp, color = FanzineColors.Faded)
                Text(formatMillis(uiState.playback.durationMs), fontFamily = FanzineFonts.SpecialElite, fontSize = 11.sp, color = FanzineColors.Faded)
            }

            Spacer(Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.SkipPrevious,
                    contentDescription = "Anterior",
                    tint = FanzineColors.Paper,
                    modifier = Modifier.size(54.dp).border(2.dp, FanzineColors.Paper).clickable(onClick = viewModel::skipPrevious).padding(14.dp),
                )
                Spacer(Modifier.width(13.dp))
                Icon(
                    if (uiState.playback.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (uiState.playback.isPlaying) "Pausar" else "Reproducir",
                    tint = FanzineColors.Ink,
                    modifier = Modifier.size(84.dp).rotate(1.5f).background(FanzineColors.Red).clickable(onClick = viewModel::togglePlayPause).padding(20.dp),
                )
                Spacer(Modifier.width(13.dp))
                Icon(
                    Icons.Filled.SkipNext,
                    contentDescription = "Siguiente",
                    tint = FanzineColors.Paper,
                    modifier = Modifier.size(54.dp).border(2.dp, FanzineColors.Paper).clickable(onClick = viewModel::skipNext).padding(14.dp),
                )
            }

            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FanzineToggleChip(
                    label = "caos",
                    icon = Icons.Filled.Shuffle,
                    filled = uiState.playback.shuffleModeEnabled,
                    onClick = viewModel::toggleShuffle,
                )
                FanzineToggleChip(
                    label = "bucle",
                    icon = if (uiState.playback.repeatMode == Player.REPEAT_MODE_ONE) Icons.Filled.RepeatOne else Icons.Filled.Repeat,
                    filled = uiState.playback.repeatMode != Player.REPEAT_MODE_OFF,
                    onClick = viewModel::cycleRepeatMode,
                )
                val isFavorite = song != null && song.id in uiState.favoriteSongIds
                FanzineToggleChip(
                    label = "mía",
                    icon = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    filled = isFavorite,
                    onClick = { song?.let { viewModel.toggleFavorite(it.id) } },
                )
            }
        }
    }
}

@Composable
private fun FanzineToggleChip(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, filled: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .then(if (filled) Modifier.background(FanzineColors.Red) else Modifier.border(2.dp, FanzineColors.Hair))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = label, tint = if (filled) FanzineColors.Ink else FanzineColors.Faded, modifier = Modifier.size(15.dp))
        Spacer(Modifier.width(5.dp))
        Text(label, fontFamily = FanzineFonts.SpecialElite, fontSize = 11.sp, color = if (filled) FanzineColors.Ink else FanzineColors.Faded)
    }
}
