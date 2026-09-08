package com.PolGrauDev.reproductor_nativo_android.ui.screens.style.sticker

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.PolGrauDev.reproductor_nativo_android.data.AlbumArtRequest
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.StickerColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.StickerHardShadowBox
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.StickerType
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.stickerTilt
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicViewModel

@Composable
fun FavoritesScreenSticker(viewModel: MusicViewModel, onBack: () -> Unit, onSongClick: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val favorites = uiState.favoriteSongs

    Box(Modifier.fillMaxSize().background(StickerColors.Blush)) {
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth().padding(16.dp, 14.dp, 16.dp, 0.dp), verticalAlignment = Alignment.CenterVertically) {
                StickerHardShadowBox(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(14.dp)) {
                    Box(Modifier.fillMaxSize().clickable(onClick = onBack), contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = StickerColors.Ink)
                    }
                }
                Text("Favoritas", style = StickerType.HeadlineLarge, color = StickerColors.Ink, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                StickerHardShadowBox(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(14.dp), backgroundColor = StickerColors.Pink) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Favorite, contentDescription = null, tint = Color.White)
                    }
                }
            }
            Text(
                "las ${favorites.size} que no te cansas de oír",
                style = StickerType.Handwritten,
                color = StickerColors.Faded,
                modifier = Modifier.padding(16.dp, 8.dp, 16.dp, 4.dp),
            )
            if (favorites.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aún no tienes canciones favoritas", style = StickerType.HandwrittenSmall, color = StickerColors.Faded)
                }
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp, 0.dp, 16.dp, 90.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    itemsIndexed(favorites, key = { _, song -> song.id }) { index, song ->
                        StickerHardShadowBox(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), rotationDegrees = stickerTilt(index)) {
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { viewModel.playSong(song, fromList = favorites); onSongClick() }.padding(9.dp, 9.dp, 12.dp, 9.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                AsyncImage(model = AlbumArtRequest(song.contentUri), contentDescription = null, modifier = Modifier.size(50.dp).clip(RoundedCornerShape(15.dp)), contentScale = ContentScale.Crop)
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(song.title, style = StickerType.TitleMedium, color = StickerColors.Ink, maxLines = 1)
                                    Text(song.artist, style = StickerType.HandwrittenSmall, color = StickerColors.Faded, maxLines = 1)
                                }
                                Box(Modifier.size(34.dp).background(StickerColors.Pink, RoundedCornerShape(12.dp)).clickable { viewModel.toggleFavorite(song.id) }, contentAlignment = Alignment.Center) {
                                    Icon(Icons.Filled.Favorite, contentDescription = "Quitar de favoritos", tint = Color.White, modifier = Modifier.size(17.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        if (favorites.isNotEmpty()) {
            Row(
                modifier = Modifier.align(Alignment.BottomStart).padding(16.dp, 0.dp, 16.dp, 14.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Box(
                    Modifier.size(62.dp).border(3.dp, StickerColors.Ink, RoundedCornerShape(20.dp)).background(Color.White, RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("gatete", style = StickerType.HandwrittenSmall, color = StickerColors.Faded)
                }
                StickerHardShadowBox(shape = RoundedCornerShape(16.dp)) {
                    Text("¡qué mimadas!", style = StickerType.Handwritten, color = StickerColors.Ink, modifier = Modifier.padding(11.dp, 7.dp))
                }
            }
        }
    }
}
