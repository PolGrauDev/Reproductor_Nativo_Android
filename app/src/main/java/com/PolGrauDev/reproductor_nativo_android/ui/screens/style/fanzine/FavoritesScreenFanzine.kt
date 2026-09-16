package com.PolGrauDev.reproductor_nativo_android.ui.screens.style.fanzine

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.SubcomposeAsyncImage
import com.PolGrauDev.reproductor_nativo_android.data.AlbumArtRequest
import com.PolGrauDev.reproductor_nativo_android.data.SongArtStorage
import com.PolGrauDev.reproductor_nativo_android.data.model.Song
import com.PolGrauDev.reproductor_nativo_android.ui.components.AddToPlaylistDialog
import com.PolGrauDev.reproductor_nativo_android.ui.components.EditSongDialog
import com.PolGrauDev.reproductor_nativo_android.ui.components.SongOptionsMenu
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.AppStyle
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.AlbumArtFallbackFanzine
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.FanzineColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.FanzineFonts
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.fanzineTilt
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.photocopyGrain
import com.PolGrauDev.reproductor_nativo_android.ui.util.shareSong
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicViewModel
import kotlinx.coroutines.launch

@Composable
fun FavoritesScreenFanzine(viewModel: MusicViewModel, onBack: () -> Unit, onSongClick: () -> Unit) {
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
                itemsIndexed(favorites, key = { _, song -> song.id }) { index, song ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .rotate(fanzineTilt(index))
                            .background(FanzineColors.Paper)
                            .clickable { viewModel.playSong(song, fromList = favorites); onSongClick() }
                            .padding(11.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        SubcomposeAsyncImage(
                            model = AlbumArtRequest(song.contentUri, song.id),
                            contentDescription = null,
                            modifier = Modifier.size(46.dp),
                            contentScale = ContentScale.Crop,
                            loading = { AlbumArtFallbackFanzine() },
                            error = { AlbumArtFallbackFanzine() },
                        )
                        Spacer(Modifier.width(11.dp))
                        Column(Modifier.weight(1f)) {
                            Text(song.title.uppercase(), fontFamily = FanzineFonts.Anton, fontSize = 16.sp, color = FanzineColors.Ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(song.artist, fontFamily = FanzineFonts.SpecialElite, fontSize = 12.sp, color = FanzineColors.Grime, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        var menuExpanded by remember { mutableStateOf(false) }
                        Box {
                            Box(
                                Modifier.size(30.dp).rotate(-3f).background(FanzineColors.Red).clickable { menuExpanded = true },
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons.Filled.MoreVert, contentDescription = "Más opciones", tint = FanzineColors.Ink, modifier = Modifier.size(17.dp))
                            }
                            SongOptionsMenu(
                                appStyle = AppStyle.FANZINE,
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
                }
            }
        }
    }

    songForPlaylistDialog?.let { song ->
        val playlistIdsWithSong by remember(song.id) { viewModel.playlistIdsContainingSong(song.id) }
            .collectAsStateWithLifecycle(initialValue = emptySet())
        AddToPlaylistDialog(
            appStyle = AppStyle.FANZINE,
            playlists = uiState.playlists,
            playlistIdsWithSong = playlistIdsWithSong,
            onDismiss = { songForPlaylistDialog = null },
            onPlaylistSelected = { playlistId -> viewModel.addSongToPlaylist(playlistId, song.id); songForPlaylistDialog = null },
            onCreatePlaylist = { name -> viewModel.createPlaylistAndAddSong(name, song.id); songForPlaylistDialog = null },
        )
    }

    songForEditDialog?.let { song ->
        EditSongDialog(
            appStyle = AppStyle.FANZINE,
            song = song,
            onDismiss = { songForEditDialog = null },
            onSave = { title, artist, album -> viewModel.setSongInfo(song.id, title, artist, album); songForEditDialog = null },
        )
    }
}
