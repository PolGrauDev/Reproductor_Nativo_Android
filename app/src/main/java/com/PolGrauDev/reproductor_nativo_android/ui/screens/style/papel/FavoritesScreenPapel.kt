package com.PolGrauDev.reproductor_nativo_android.ui.screens.style.papel

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.SubcomposeAsyncImage
import com.PolGrauDev.reproductor_nativo_android.data.AlbumArtRequest
import com.PolGrauDev.reproductor_nativo_android.data.SongArtStorage
import com.PolGrauDev.reproductor_nativo_android.data.model.Song
import com.PolGrauDev.reproductor_nativo_android.ui.components.AddToPlaylistDialog
import com.PolGrauDev.reproductor_nativo_android.ui.components.EditSongDialog
import com.PolGrauDev.reproductor_nativo_android.ui.components.SongOptionsMenu
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.AppStyle
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.AlbumArtFallbackPapel
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelRowDivider
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelType
import com.PolGrauDev.reproductor_nativo_android.ui.util.shareSong
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicViewModel
import kotlinx.coroutines.launch

@Composable
fun FavoritesScreenPapel(viewModel: MusicViewModel, onBack: () -> Unit, onSongClick: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val favorites = uiState.favoriteSongs

    var songForPlaylistDialog by remember { mutableStateOf<Song?>(null) }
    var songForEditDialog by remember { mutableStateOf<Song?>(null) }
    var songForImagePick by remember { mutableStateOf<Song?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        val song = songForImagePick
        songForImagePick = null
        if (uri != null && song != null) {
            scope.launch {
                SongArtStorage.copyPickedArt(context, song.id, uri)
                SongArtStorage.invalidateCache(context, song)
                viewModel.setCustomArtUpdated(song.id)
            }
        }
    }

    Scaffold(containerColor = PapelColors.Background, contentWindowInsets = WindowInsets(0, 0, 0, 0)) { padding ->
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
                            SubcomposeAsyncImage(
                                model = AlbumArtRequest(song.contentUri, song.id),
                                contentDescription = null,
                                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(2.dp)),
                                contentScale = ContentScale.Crop,
                                loading = { AlbumArtFallbackPapel() },
                                error = { AlbumArtFallbackPapel() },
                            )
                            Spacer(Modifier.width(14.dp))
                            Column(Modifier.weight(1f)) {
                                Text(song.title, style = PapelType.TitleMedium, color = PapelColors.OnSurface, maxLines = 1)
                                Text(song.artist, style = PapelType.BodySmall, color = PapelColors.OnSurfaceVariant, maxLines = 1)
                            }
                            var menuExpanded by remember { mutableStateOf(false) }
                            Box {
                                IconButton(onClick = { menuExpanded = true }) {
                                    Icon(Icons.Filled.MoreVert, contentDescription = "Más opciones", tint = PapelColors.OnSurfaceVariant)
                                }
                                SongOptionsMenu(
                                    appStyle = AppStyle.PAPEL,
                                    expanded = menuExpanded,
                                    isFavorite = true,
                                    onDismiss = { menuExpanded = false },
                                    onAddToPlaylist = { songForPlaylistDialog = song },
                                    onToggleFavorite = { viewModel.toggleFavorite(song.id) },
                                    onChangeImage = {
                                        songForImagePick = song
                                        imagePickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                    },
                                    onEditInfo = { songForEditDialog = song },
                                    onShare = { shareSong(context, song) },
                                )
                            }
                        }
                        PapelRowDivider()
                    }
                }
            }
        }
    }

    songForPlaylistDialog?.let { song ->
        val playlistIdsWithSong by remember(song.id) { viewModel.playlistIdsContainingSong(song.id) }
            .collectAsStateWithLifecycle(initialValue = emptySet())
        AddToPlaylistDialog(
            appStyle = AppStyle.PAPEL,
            playlists = uiState.playlists,
            playlistIdsWithSong = playlistIdsWithSong,
            onDismiss = { songForPlaylistDialog = null },
            onPlaylistSelected = { playlistId ->
                viewModel.addSongToPlaylist(playlistId, song.id)
                songForPlaylistDialog = null
            },
            onCreatePlaylist = { name ->
                viewModel.createPlaylistAndAddSong(name, song.id)
                songForPlaylistDialog = null
            },
        )
    }

    songForEditDialog?.let { song ->
        EditSongDialog(
            appStyle = AppStyle.PAPEL,
            song = song,
            onDismiss = { songForEditDialog = null },
            onSave = { title, artist, album ->
                viewModel.setSongInfo(song.id, title, artist, album)
                songForEditDialog = null
            },
        )
    }
}
