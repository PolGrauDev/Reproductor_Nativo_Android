package com.PolGrauDev.reproductor_nativo_android.ui.screens.style.papel

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.PolGrauDev.reproductor_nativo_android.data.AlbumArtRequest
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelRowDivider
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelType
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicViewModel

@Composable
fun FavoritesScreenPapel(viewModel: MusicViewModel, onBack: () -> Unit, onSongClick: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val favorites = uiState.favoriteSongs

    Scaffold(containerColor = PapelColors.Background) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Row(Modifier.fillMaxWidth().padding(top = 10.dp, start = 4.dp, end = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = PapelColors.OnSurfaceVariant)
                }
            }
            Column(Modifier.fillMaxWidth().padding(20.dp, 2.dp, 20.dp, 16.dp)) {
                Text("Las que vuelves", style = PapelType.HeadlineLarge, color = PapelColors.OnSurface)
                Text(
                    "a poner",
                    style = PapelType.HeadlineLarge.let { it.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic) },
                    color = PapelColors.OnSurface,
                )
                Text(
                    "${favorites.size} canciones guardadas",
                    style = PapelType.SectionLabel,
                    color = PapelColors.OnSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            if (favorites.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aún no tienes canciones favoritas", style = PapelType.BodyMedium, color = PapelColors.OnSurfaceVariant)
                }
                return@Column
            }
            LazyColumn(Modifier.fillMaxSize()) {
                items(favorites, key = { it.id }) { song ->
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable {
                                viewModel.playSong(song, fromList = favorites)
                                onSongClick()
                            }.padding(20.dp, 16.dp, 20.dp, 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            AsyncImage(
                                model = AlbumArtRequest(song.contentUri),
                                contentDescription = null,
                                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(2.dp)),
                                contentScale = ContentScale.Crop,
                            )
                            Spacer(Modifier.width(14.dp))
                            Column(Modifier.weight(1f)) {
                                Text(song.title, style = PapelType.TitleMedium, color = PapelColors.OnSurface, maxLines = 1)
                                Text(song.artist, style = PapelType.BodySmall, color = PapelColors.OnSurfaceVariant, maxLines = 1)
                            }
                            Icon(
                                Icons.Filled.Favorite,
                                contentDescription = "Quitar de favoritos",
                                tint = PapelColors.Accent,
                                modifier = Modifier.clickable { viewModel.toggleFavorite(song.id) }.padding(6.dp),
                            )
                        }
                        PapelRowDivider()
                    }
                }
            }
        }
    }
}
