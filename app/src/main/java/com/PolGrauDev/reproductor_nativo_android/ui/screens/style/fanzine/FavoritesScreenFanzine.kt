package com.PolGrauDev.reproductor_nativo_android.ui.screens.style.fanzine

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
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
fun FavoritesScreenFanzine(viewModel: MusicViewModel, onBack: () -> Unit, onSongClick: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val favorites = uiState.favoriteSongs

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
                Text("Mis", fontFamily = FanzineFonts.Anton, fontSize = 22.sp, color = FanzineColors.Paper, modifier = Modifier.rotate(-2.5f).background(FanzineColors.Red).padding(horizontal = 4.dp))
                Spacer(Modifier.width(2.dp))
                Text("favoritas", fontFamily = FanzineFonts.Anton, fontSize = 19.sp, color = FanzineColors.Ink, modifier = Modifier.rotate(2f).background(FanzineColors.Paper).padding(horizontal = 4.dp))
            }
        }
        Text(
            "las que te sabes de memoria · ${favorites.size}",
            fontFamily = FanzineFonts.SpecialElite,
            fontSize = 12.sp,
            color = FanzineColors.Faded,
            modifier = Modifier.padding(14.dp, 4.dp, 14.dp, 4.dp),
        )
        if (favorites.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aún no tienes canciones favoritas", fontFamily = FanzineFonts.SpecialElite, fontSize = 13.sp, color = FanzineColors.Faded)
            }
        } else {
            LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                itemsIndexed(favorites) { index, song ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .rotate(fanzineTilt(index))
                            .background(FanzineColors.Paper)
                            .clickable { viewModel.playSong(song, fromList = favorites); onSongClick() }
                            .padding(11.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AsyncImage(model = AlbumArtRequest(song.contentUri), contentDescription = null, modifier = Modifier.size(46.dp), contentScale = ContentScale.Crop)
                        Spacer(Modifier.width(11.dp))
                        Column(Modifier.weight(1f)) {
                            Text(song.title.uppercase(), fontFamily = FanzineFonts.Anton, fontSize = 16.sp, color = FanzineColors.Ink, maxLines = 1)
                            Text(song.artist, fontFamily = FanzineFonts.SpecialElite, fontSize = 12.sp, color = FanzineColors.Grime, maxLines = 1)
                        }
                        Box(
                            Modifier.size(30.dp).rotate(-3f).background(FanzineColors.Red).clickable { viewModel.toggleFavorite(song.id) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Filled.Favorite, contentDescription = "Quitar de favoritos", tint = FanzineColors.Ink, modifier = Modifier.size(17.dp))
                        }
                    }
                }
            }
        }
    }
}
