package com.PolGrauDev.reproductor_nativo_android.ui.screens.style.sticker

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.StickerColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.StickerHardShadowBox
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.StickerType
import com.PolGrauDev.reproductor_nativo_android.ui.util.formatMillis
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicUiState
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicViewModel

@Composable
fun NowPlayingScreenSticker(viewModel: MusicViewModel, uiState: MusicUiState, onBack: () -> Unit, onQueueClick: () -> Unit) {
    val song = uiState.currentSong
    var isUserSeeking by remember { mutableStateOf(false) }
    var seekFraction by remember { mutableFloatStateOf(0f) }

    Column(Modifier.fillMaxSize().background(StickerColors.Blush)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp, 14.dp, 16.dp, 0.dp), verticalAlignment = Alignment.CenterVertically) {
            StickerHardShadowBox(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(14.dp)) {
                Box(Modifier.fillMaxSize().clickable(onClick = onBack), contentAlignment = Alignment.Center) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = StickerColors.Ink)
                }
            }
            Text(
                "～ ahora mismo suena ～",
                style = StickerType.Handwritten,
                color = StickerColors.Faded,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            StickerHardShadowBox(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(14.dp)) {
                Box(Modifier.fillMaxSize().clickable(onClick = onQueueClick), contentAlignment = Alignment.Center) {
                    Icon(Icons.AutoMirrored.Filled.QueueMusic, contentDescription = "Ver cola", tint = StickerColors.Ink)
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(28.dp, 12.dp, 28.dp, 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val rotation = rememberInfiniteTransition(label = "vinyl")
            val angle by rotation.animateFloat(0f, 360f, infiniteRepeatable(tween(6_000, easing = LinearEasing)), label = "vinylAngle")
            StickerHardShadowBox(
                modifier = Modifier.fillMaxWidth(0.72f).aspectRatio(1f),
                shape = RoundedCornerShape(34.dp),
                shadowOffsetX = 7.dp,
                shadowOffsetY = 7.dp,
                borderWidth = 4.dp,
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    AsyncImage(
                        model = song?.contentUri?.let { AlbumArtRequest(it) },
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .aspectRatio(1f)
                            .clip(CircleShape)
                            .rotate(if (uiState.playback.isPlaying) angle else 0f),
                        contentScale = ContentScale.Crop,
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            Text(song?.title ?: "-", style = StickerType.HeadlineLarge.let { it.copy(fontSize = 30.sp) }, color = StickerColors.Ink, maxLines = 1)
            Text(song?.artist ?: "-", style = StickerType.Handwritten, color = StickerColors.Faded, maxLines = 1)
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
                colors = SliderDefaults.colors(thumbColor = StickerColors.Butter, activeTrackColor = StickerColors.Pink, inactiveTrackColor = androidx.compose.ui.graphics.Color.White),
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(formatMillis(uiState.playback.positionMs), style = StickerType.HandwrittenSmall, color = StickerColors.Faded)
                Text(formatMillis(uiState.playback.durationMs), style = StickerType.HandwrittenSmall, color = StickerColors.Faded)
            }

            Spacer(Modifier.height(18.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                StickerHardShadowBox(modifier = Modifier.size(56.dp), shape = RoundedCornerShape(19.dp), backgroundColor = StickerColors.Mint) {
                    Box(Modifier.fillMaxSize().clickable(onClick = viewModel::skipPrevious), contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.SkipPrevious, contentDescription = "Anterior", tint = StickerColors.Ink)
                    }
                }
                Spacer(Modifier.width(14.dp))
                StickerHardShadowBox(modifier = Modifier.size(88.dp), shape = RoundedCornerShape(30.dp), backgroundColor = StickerColors.Pink, borderWidth = 4.dp, shadowOffsetX = 6.dp, shadowOffsetY = 7.dp) {
                    Box(Modifier.fillMaxSize().clickable(onClick = viewModel::togglePlayPause), contentAlignment = Alignment.Center) {
                        Icon(
                            if (uiState.playback.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (uiState.playback.isPlaying) "Pausar" else "Reproducir",
                            tint = androidx.compose.ui.graphics.Color.White,
                            modifier = Modifier.size(34.dp),
                        )
                    }
                }
                Spacer(Modifier.width(14.dp))
                StickerHardShadowBox(modifier = Modifier.size(56.dp), shape = RoundedCornerShape(19.dp), backgroundColor = StickerColors.Mint) {
                    Box(Modifier.fillMaxSize().clickable(onClick = viewModel::skipNext), contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.SkipNext, contentDescription = "Siguiente", tint = StickerColors.Ink)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StickerToggleChip(
                    icon = Icons.Filled.Shuffle,
                    label = "revuelto",
                    background = StickerColors.Grape,
                    active = uiState.playback.shuffleModeEnabled,
                    onClick = viewModel::toggleShuffle,
                )
                StickerToggleChip(
                    icon = if (uiState.playback.repeatMode == Player.REPEAT_MODE_ONE) Icons.Filled.RepeatOne else Icons.Filled.Repeat,
                    label = "otra vez",
                    background = androidx.compose.ui.graphics.Color.White,
                    active = uiState.playback.repeatMode != Player.REPEAT_MODE_OFF,
                    onClick = viewModel::cycleRepeatMode,
                )
                val isFavorite = song != null && song.id in uiState.favoriteSongIds
                StickerHardShadowBox(shape = RoundedCornerShape(15.dp), backgroundColor = StickerColors.Butter) {
                    Icon(
                        if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = if (isFavorite) "Quitar de favoritos" else "Añadir a favoritos",
                        tint = StickerColors.Ink,
                        modifier = Modifier.clickable { song?.let { viewModel.toggleFavorite(it.id) } }.padding(10.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun StickerToggleChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    background: androidx.compose.ui.graphics.Color,
    active: Boolean,
    onClick: () -> Unit,
) {
    StickerHardShadowBox(shape = RoundedCornerShape(15.dp), backgroundColor = background) {
        Row(
            modifier = Modifier.clickable(onClick = onClick).padding(horizontal = 14.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, contentDescription = label, tint = if (active) StickerColors.Ink else StickerColors.Faded, modifier = Modifier.size(17.dp))
            Spacer(Modifier.width(5.dp))
            Text(label, style = StickerType.HandwrittenSmall, color = if (active) StickerColors.Ink else StickerColors.Faded)
        }
    }
}
