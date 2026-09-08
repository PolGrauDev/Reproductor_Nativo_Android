package com.PolGrauDev.reproductor_nativo_android.ui.screens.style.fanzine

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.AppStyle
import com.PolGrauDev.reproductor_nativo_android.data.AlbumArtRequest
import com.PolGrauDev.reproductor_nativo_android.data.model.Song
import com.PolGrauDev.reproductor_nativo_android.ui.components.AddToPlaylistDialog
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.FanzineColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.FanzineFonts
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.fanzineTilt
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.photocopyGrain
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicViewModel

@Composable
private fun FanzineDetailTopBar(title: String, onBack: () -> Unit, actions: @Composable RowScope.() -> Unit = {}) {
    Row(Modifier.fillMaxWidth().padding(14.dp, 12.dp, 14.dp, 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Volver",
            tint = FanzineColors.Ink,
            modifier = Modifier.size(36.dp).rotate(-2f).background(FanzineColors.Paper).clickable(onClick = onBack).padding(7.dp),
        )
        Spacer(Modifier.width(9.dp))
        Text(
            title.uppercase(),
            fontFamily = FanzineFonts.Anton,
            fontSize = 20.sp,
            color = FanzineColors.Paper,
            maxLines = 1,
            modifier = Modifier.weight(1f),
        )
        actions()
    }
}

@Composable
fun AlbumDetailScreenFanzine(viewModel: MusicViewModel, albumId: Long?, onBack: () -> Unit, onSongClick: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val album = uiState.albums.firstOrNull { it.albumId == albumId }
    var songForPlaylistDialog by remember { mutableStateOf<Song?>(null) }

    Column(Modifier.fillMaxSize().background(FanzineColors.Slate).photocopyGrain()) {
        FanzineDetailTopBar(album?.title ?: "Disco", onBack)
        if (album == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Álbum no encontrado", fontFamily = FanzineFonts.SpecialElite, fontSize = 13.sp, color = FanzineColors.Faded)
            }
            return@Column
        }
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                Column(Modifier.fillMaxWidth().padding(bottom = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    AsyncImage(
                        model = AlbumArtRequest(album.songs.first().contentUri),
                        contentDescription = null,
                        modifier = Modifier.size(132.dp).rotate(-1.5f).border(2.dp, FanzineColors.Paper),
                        contentScale = ContentScale.Crop,
                    )
                    Spacer(Modifier.height(14.dp))
                    Text(album.title.uppercase(), fontFamily = FanzineFonts.Anton, fontSize = 26.sp, color = FanzineColors.Paper, textAlign = TextAlign.Center, maxLines = 2)
                    Text("${album.artist} · ${album.songs.size} cortes", fontFamily = FanzineFonts.SpecialElite, fontSize = 12.sp, color = FanzineColors.Faded)
                }
            }
            itemsIndexed(album.songs, key = { _, it -> it.id }) { index, song ->
                FanzineDetailSongRow(
                    index = index + 1,
                    song = song,
                    isPlaying = uiState.currentSong?.id == song.id && uiState.playback.isPlaying,
                    isFavorite = song.id in uiState.favoriteSongIds,
                    tilt = fanzineTilt(index),
                    onClick = { viewModel.playSong(song, fromList = album.songs); onSongClick() },
                    onToggleFavorite = { viewModel.toggleFavorite(song.id) },
                    onAddToPlaylist = { songForPlaylistDialog = song },
                )
            }
        }
    }

    songForPlaylistDialog?.let { song ->
        AddToPlaylistDialog(
            appStyle = AppStyle.FANZINE,
            playlists = uiState.playlists,
            onDismiss = { songForPlaylistDialog = null },
            onPlaylistSelected = { playlistId -> viewModel.addSongToPlaylist(playlistId, song.id); songForPlaylistDialog = null },
            onCreatePlaylist = { name -> viewModel.createPlaylistAndAddSong(name, song.id); songForPlaylistDialog = null },
        )
    }
}

@Composable
private fun FanzineDetailSongRow(
    index: Int,
    song: Song,
    isPlaying: Boolean,
    isFavorite: Boolean,
    tilt: Float,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAddToPlaylist: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().rotate(tilt).background(FanzineColors.Paper).clickable(onClick = onClick).padding(11.dp, 9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("$index", fontFamily = FanzineFonts.Anton, fontSize = 18.sp, color = FanzineColors.Red, modifier = Modifier.width(20.dp))
        Column(Modifier.weight(1f)) {
            Text(song.title.uppercase(), fontFamily = FanzineFonts.Anton, fontSize = 16.sp, color = FanzineColors.Ink, maxLines = 1)
            Text(song.artist, fontFamily = FanzineFonts.SpecialElite, fontSize = 12.sp, color = FanzineColors.Grime, maxLines = 1)
        }
        if (isPlaying) {
            Icon(Icons.Filled.MusicNote, contentDescription = "Reproduciendo", tint = FanzineColors.Red, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
        }
        Icon(
            if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = null,
            tint = FanzineColors.Ink,
            modifier = Modifier.clickable(onClick = onToggleFavorite).padding(6.dp),
        )
        Icon(Icons.Filled.Add, contentDescription = "Añadir a playlist", tint = FanzineColors.Ink, modifier = Modifier.clickable(onClick = onAddToPlaylist).padding(6.dp))
    }
}

@Composable
fun ArtistDetailScreenFanzine(viewModel: MusicViewModel, artistId: Long?, onBack: () -> Unit, onSongClick: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val artist = uiState.artists.firstOrNull { it.artistId == artistId }
    var songForPlaylistDialog by remember { mutableStateOf<Song?>(null) }

    Column(Modifier.fillMaxSize().background(FanzineColors.Slate).photocopyGrain()) {
        FanzineDetailTopBar(artist?.name ?: "Grupo", onBack)
        if (artist == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Artista no encontrado", fontFamily = FanzineFonts.SpecialElite, fontSize = 13.sp, color = FanzineColors.Faded)
            }
            return@Column
        }
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            itemsIndexed(artist.songs, key = { _, it -> it.id }) { index, song ->
                Row(
                    modifier = Modifier.fillMaxWidth().rotate(fanzineTilt(index)).background(FanzineColors.Paper).clickable { viewModel.playSong(song, fromList = artist.songs); onSongClick() }.padding(11.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AsyncImage(model = AlbumArtRequest(song.contentUri), contentDescription = null, modifier = Modifier.size(46.dp), contentScale = ContentScale.Crop)
                    Spacer(Modifier.width(11.dp))
                    Column(Modifier.weight(1f)) {
                        Text(song.title.uppercase(), fontFamily = FanzineFonts.Anton, fontSize = 16.sp, color = FanzineColors.Ink, maxLines = 1)
                        Text(song.album, fontFamily = FanzineFonts.SpecialElite, fontSize = 12.sp, color = FanzineColors.Grime, maxLines = 1)
                    }
                    val isFavorite = song.id in uiState.favoriteSongIds
                    Icon(
                        if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = null,
                        tint = FanzineColors.Ink,
                        modifier = Modifier.clickable { viewModel.toggleFavorite(song.id) }.padding(6.dp),
                    )
                    Icon(Icons.Filled.Add, contentDescription = "Añadir a playlist", tint = FanzineColors.Ink, modifier = Modifier.clickable { songForPlaylistDialog = song }.padding(6.dp))
                }
            }
        }
    }

    songForPlaylistDialog?.let { song ->
        AddToPlaylistDialog(
            appStyle = AppStyle.FANZINE,
            playlists = uiState.playlists,
            onDismiss = { songForPlaylistDialog = null },
            onPlaylistSelected = { playlistId -> viewModel.addSongToPlaylist(playlistId, song.id); songForPlaylistDialog = null },
            onCreatePlaylist = { name -> viewModel.createPlaylistAndAddSong(name, song.id); songForPlaylistDialog = null },
        )
    }
}

@Composable
fun FolderDetailScreenFanzine(viewModel: MusicViewModel, folderPath: String, onBack: () -> Unit, onSongClick: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val folder = uiState.folders.firstOrNull { it.path == folderPath }
    var songForPlaylistDialog by remember { mutableStateOf<Song?>(null) }

    Column(Modifier.fillMaxSize().background(FanzineColors.Slate).photocopyGrain()) {
        FanzineDetailTopBar(folder?.name ?: "Carpeta", onBack)
        if (folder == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Carpeta no encontrada", fontFamily = FanzineFonts.SpecialElite, fontSize = 13.sp, color = FanzineColors.Faded)
            }
            return@Column
        }
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                Text("${folder.songs.size} canciones", fontFamily = FanzineFonts.SpecialElite, fontSize = 12.sp, color = FanzineColors.Faded)
            }
            itemsIndexed(folder.songs, key = { _, it -> it.id }) { index, song ->
                FanzineDetailSongRow(
                    index = index + 1,
                    song = song,
                    isPlaying = uiState.currentSong?.id == song.id && uiState.playback.isPlaying,
                    isFavorite = song.id in uiState.favoriteSongIds,
                    tilt = fanzineTilt(index),
                    onClick = { viewModel.playSong(song, fromList = folder.songs); onSongClick() },
                    onToggleFavorite = { viewModel.toggleFavorite(song.id) },
                    onAddToPlaylist = { songForPlaylistDialog = song },
                )
            }
        }
    }

    songForPlaylistDialog?.let { song ->
        AddToPlaylistDialog(
            appStyle = AppStyle.FANZINE,
            playlists = uiState.playlists,
            onDismiss = { songForPlaylistDialog = null },
            onPlaylistSelected = { playlistId -> viewModel.addSongToPlaylist(playlistId, song.id); songForPlaylistDialog = null },
            onCreatePlaylist = { name -> viewModel.createPlaylistAndAddSong(name, song.id); songForPlaylistDialog = null },
        )
    }
}

@Composable
fun PlaylistDetailScreenFanzine(
    viewModel: MusicViewModel,
    playlistId: Long,
    onBack: () -> Unit,
    onSongClick: () -> Unit,
    onPlaylistDeleted: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val songsFlow = remember(playlistId) { viewModel.playlistSongsFlow(playlistId) }
    val songs by songsFlow.collectAsStateWithLifecycle(initialValue = emptyList())
    val playlist = uiState.playlists.firstOrNull { it.id == playlistId }
    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(FanzineColors.Slate).photocopyGrain()) {
        FanzineDetailTopBar(playlist?.name ?: "Lista", onBack) {
            Icon(Icons.Filled.Edit, contentDescription = "Renombrar playlist", tint = FanzineColors.Ink, modifier = Modifier.clickable { showRenameDialog = true }.padding(6.dp))
            Icon(Icons.Filled.Delete, contentDescription = "Borrar playlist", tint = FanzineColors.Ink, modifier = Modifier.clickable { showDeleteDialog = true }.padding(6.dp))
        }
        if (songs.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Esta playlist todavía no tiene canciones", fontFamily = FanzineFonts.SpecialElite, fontSize = 13.sp, color = FanzineColors.Faded)
            }
        } else {
            LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                itemsIndexed(songs, key = { _, song -> song.id }) { index, song ->
                    Row(
                        modifier = Modifier.fillMaxWidth().rotate(fanzineTilt(index)).background(FanzineColors.Paper).clickable { viewModel.playSong(song, fromList = songs); onSongClick() }.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AsyncImage(model = AlbumArtRequest(song.contentUri), contentDescription = null, modifier = Modifier.size(40.dp), contentScale = ContentScale.Crop)
                        Spacer(Modifier.width(11.dp))
                        Column(Modifier.weight(1f)) {
                            Text(song.title.uppercase(), fontFamily = FanzineFonts.Anton, fontSize = 15.sp, color = FanzineColors.Ink, maxLines = 1)
                            Text(song.artist, fontFamily = FanzineFonts.SpecialElite, fontSize = 12.sp, color = FanzineColors.Grime, maxLines = 1)
                        }
                        Icon(
                            Icons.Filled.KeyboardArrowUp,
                            contentDescription = "Subir",
                            tint = if (index > 0) FanzineColors.Ink else FanzineColors.Faded,
                            modifier = Modifier.clickable(enabled = index > 0) { viewModel.moveSongInPlaylist(playlistId, songs.map { it.id }, index, index - 1) }.padding(4.dp),
                        )
                        Icon(
                            Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Bajar",
                            tint = if (index < songs.lastIndex) FanzineColors.Ink else FanzineColors.Faded,
                            modifier = Modifier.clickable(enabled = index < songs.lastIndex) { viewModel.moveSongInPlaylist(playlistId, songs.map { it.id }, index, index + 1) }.padding(4.dp),
                        )
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Quitar de la playlist",
                            tint = FanzineColors.Red,
                            modifier = Modifier.clickable { viewModel.removeSongFromPlaylist(playlistId, song.id) }.padding(4.dp),
                        )
                    }
                }
            }
        }
    }

    if (showRenameDialog && playlist != null) {
        FanzineRenamePlaylistDialog(
            currentName = playlist.name,
            onDismiss = { showRenameDialog = false },
            onRename = { viewModel.renamePlaylist(playlistId, it); showRenameDialog = false },
        )
    }

    if (showDeleteDialog) {
        FanzineDeletePlaylistDialog(
            playlistName = playlist?.name ?: "",
            onDismiss = { showDeleteDialog = false },
            onDelete = { viewModel.deletePlaylist(playlistId); showDeleteDialog = false; onPlaylistDeleted() },
        )
    }
}

@Composable
private fun FanzineRenamePlaylistDialog(currentName: String, onDismiss: () -> Unit, onRename: (String) -> Unit) {
    var name by remember { mutableStateOf(currentName) }
    Dialog(onDismissRequest = onDismiss) {
        Column(Modifier.rotate(-1f).background(FanzineColors.Paper)) {
            Row(Modifier.fillMaxWidth().background(FanzineColors.Red).padding(14.dp, 11.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Edit, contentDescription = null, tint = FanzineColors.Ink)
                Spacer(Modifier.width(8.dp))
                Text("RENOMBRAR", fontFamily = FanzineFonts.Anton, fontSize = 20.sp, color = FanzineColors.Ink)
            }
            Column(Modifier.padding(14.dp)) {
                Box(Modifier.border(2.dp, FanzineColors.Ink).fillMaxWidth().padding(11.dp, 9.dp)) {
                    BasicTextField(
                        value = name,
                        onValueChange = { name = it },
                        singleLine = true,
                        textStyle = TextStyle(fontFamily = FanzineFonts.SpecialElite, fontSize = 15.sp, color = FanzineColors.Ink),
                        cursorBrush = SolidColor(FanzineColors.Ink),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Text(
                        "cancelar",
                        fontFamily = FanzineFonts.SpecialElite,
                        fontSize = 11.sp,
                        color = FanzineColors.Grime,
                        modifier = Modifier.border(2.dp, FanzineColors.Ink).clickable(onClick = onDismiss).padding(11.dp, 5.dp),
                    )
                    Spacer(Modifier.width(9.dp))
                    Text(
                        "guardar",
                        fontFamily = FanzineFonts.SpecialElite,
                        fontSize = 11.sp,
                        color = FanzineColors.Paper,
                        modifier = Modifier
                            .background(FanzineColors.Ink)
                            .clickable(enabled = name.isNotBlank()) { onRename(name) }
                            .padding(12.dp, 7.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun FanzineDeletePlaylistDialog(playlistName: String, onDismiss: () -> Unit, onDelete: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Column(Modifier.rotate(-1f).background(FanzineColors.Paper)) {
            Row(Modifier.fillMaxWidth().background(FanzineColors.Red).padding(14.dp, 11.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Delete, contentDescription = null, tint = FanzineColors.Ink)
                Spacer(Modifier.width(8.dp))
                Text("BORRAR LISTA", fontFamily = FanzineFonts.Anton, fontSize = 20.sp, color = FanzineColors.Ink)
            }
            Column(Modifier.padding(14.dp)) {
                Text(
                    "¿Seguro que quieres borrar \"$playlistName\"? Esta acción no se puede deshacer.",
                    fontFamily = FanzineFonts.SpecialElite,
                    fontSize = 13.sp,
                    color = FanzineColors.Grime,
                )
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Text(
                        "cancelar",
                        fontFamily = FanzineFonts.SpecialElite,
                        fontSize = 11.sp,
                        color = FanzineColors.Grime,
                        modifier = Modifier.border(2.dp, FanzineColors.Ink).clickable(onClick = onDismiss).padding(11.dp, 5.dp),
                    )
                    Spacer(Modifier.width(9.dp))
                    Text(
                        "borrar",
                        fontFamily = FanzineFonts.SpecialElite,
                        fontSize = 11.sp,
                        color = FanzineColors.Paper,
                        modifier = Modifier
                            .background(FanzineColors.Red)
                            .clickable(onClick = onDelete)
                            .padding(12.dp, 7.dp),
                    )
                }
            }
        }
    }
}
