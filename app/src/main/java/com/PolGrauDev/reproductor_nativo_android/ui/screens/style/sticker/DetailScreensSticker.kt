package com.PolGrauDev.reproductor_nativo_android.ui.screens.style.sticker

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.AppStyle
import com.PolGrauDev.reproductor_nativo_android.data.AlbumArtRequest
import com.PolGrauDev.reproductor_nativo_android.data.model.Song
import com.PolGrauDev.reproductor_nativo_android.ui.components.AddToPlaylistDialog
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.StickerColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.StickerHardShadowBox
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.StickerType
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.stickerTilt
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicViewModel

@Composable
private fun StickerDetailTopBar(title: String, onBack: () -> Unit, actions: @Composable RowScope.() -> Unit = {}) {
    Row(Modifier.fillMaxWidth().padding(16.dp, 14.dp, 16.dp, 4.dp), verticalAlignment = Alignment.CenterVertically) {
        StickerHardShadowBox(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(14.dp)) {
            Box(Modifier.fillMaxSize().clickable(onClick = onBack), contentAlignment = Alignment.Center) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = StickerColors.Ink)
            }
        }
        Spacer(Modifier.width(10.dp))
        Text(title, style = StickerType.HeadlineLarge.let { it.copy(fontSize = 26.sp) }, color = StickerColors.Ink, modifier = Modifier.weight(1f), maxLines = 1)
        actions()
    }
}

@Composable
fun AlbumDetailScreenSticker(viewModel: MusicViewModel, albumId: Long?, onBack: () -> Unit, onSongClick: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val album = uiState.albums.firstOrNull { it.albumId == albumId }
    var songForPlaylistDialog by remember { mutableStateOf<Song?>(null) }

    Column(Modifier.fillMaxSize().background(StickerColors.Paper)) {
        StickerDetailTopBar(album?.title ?: "Álbum", onBack)
        if (album == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Álbum no encontrado", style = StickerType.HandwrittenSmall, color = StickerColors.Faded)
            }
            return@Column
        }
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
            item {
                Column(Modifier.fillMaxWidth().padding(bottom = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    StickerHardShadowBox(modifier = Modifier.size(140.dp), shape = RoundedCornerShape(22.dp), rotationDegrees = -2f) {
                        AsyncImage(model = AlbumArtRequest(album.songs.first().contentUri), contentDescription = null, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp)), contentScale = ContentScale.Crop)
                    }
                    Spacer(Modifier.height(14.dp))
                    Text(album.title, style = StickerType.HeadlineLarge, color = StickerColors.Ink, textAlign = TextAlign.Center, maxLines = 2)
                    Text("${album.artist} · ${album.songs.size} canciones", style = StickerType.HandwrittenSmall, color = StickerColors.Faded)
                }
            }
            itemsIndexed(album.songs, key = { _, it -> it.id }) { index, song ->
                StickerDetailSongRow(
                    song = song,
                    isPlaying = uiState.currentSong?.id == song.id && uiState.playback.isPlaying,
                    isFavorite = song.id in uiState.favoriteSongIds,
                    tilt = stickerTilt(index),
                    onClick = { viewModel.playSong(song, fromList = album.songs); onSongClick() },
                    onToggleFavorite = { viewModel.toggleFavorite(song.id) },
                    onAddToPlaylist = { songForPlaylistDialog = song },
                )
            }
        }
    }

    songForPlaylistDialog?.let { song ->
        AddToPlaylistDialog(
            appStyle = AppStyle.STICKERS,
            playlists = uiState.playlists,
            onDismiss = { songForPlaylistDialog = null },
            onPlaylistSelected = { playlistId -> viewModel.addSongToPlaylist(playlistId, song.id); songForPlaylistDialog = null },
            onCreatePlaylist = { name -> viewModel.createPlaylistAndAddSong(name, song.id); songForPlaylistDialog = null },
        )
    }
}

@Composable
private fun StickerDetailSongRow(
    song: Song,
    isPlaying: Boolean,
    isFavorite: Boolean,
    tilt: Float,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAddToPlaylist: () -> Unit,
) {
    StickerHardShadowBox(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), rotationDegrees = tilt) {
        Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(11.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(song.title, style = StickerType.TitleMedium, color = StickerColors.Ink, maxLines = 1)
                Text(song.artist, style = StickerType.HandwrittenSmall, color = StickerColors.Faded, maxLines = 1)
            }
            if (isPlaying) {
                Icon(Icons.Filled.MusicNote, contentDescription = "Reproduciendo", tint = StickerColors.Pink, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
            }
            Icon(
                if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = null,
                tint = StickerColors.Pink,
                modifier = Modifier.clickable(onClick = onToggleFavorite).padding(6.dp),
            )
            Icon(Icons.Filled.Add, contentDescription = "Añadir a playlist", tint = StickerColors.Grape, modifier = Modifier.clickable(onClick = onAddToPlaylist).padding(6.dp))
        }
    }
}

@Composable
fun ArtistDetailScreenSticker(viewModel: MusicViewModel, artistId: Long?, onBack: () -> Unit, onSongClick: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val artist = uiState.artists.firstOrNull { it.artistId == artistId }
    var songForPlaylistDialog by remember { mutableStateOf<Song?>(null) }

    Column(Modifier.fillMaxSize().background(StickerColors.Paper)) {
        StickerDetailTopBar(artist?.name ?: "Artista", onBack)
        if (artist == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Artista no encontrado", style = StickerType.HandwrittenSmall, color = StickerColors.Faded)
            }
            return@Column
        }
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
            itemsIndexed(artist.songs, key = { _, it -> it.id }) { index, song ->
                StickerHardShadowBox(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), rotationDegrees = stickerTilt(index)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { viewModel.playSong(song, fromList = artist.songs); onSongClick() }.padding(9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AsyncImage(model = AlbumArtRequest(song.contentUri), contentDescription = null, modifier = Modifier.size(50.dp).clip(RoundedCornerShape(14.dp)), contentScale = ContentScale.Crop)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(song.title, style = StickerType.TitleMedium, color = StickerColors.Ink, maxLines = 1)
                            Text(song.album, style = StickerType.HandwrittenSmall, color = StickerColors.Faded, maxLines = 1)
                        }
                        val isFavorite = song.id in uiState.favoriteSongIds
                        Icon(
                            if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = null,
                            tint = StickerColors.Pink,
                            modifier = Modifier.clickable { viewModel.toggleFavorite(song.id) }.padding(6.dp),
                        )
                        Icon(Icons.Filled.Add, contentDescription = "Añadir a playlist", tint = StickerColors.Grape, modifier = Modifier.clickable { songForPlaylistDialog = song }.padding(6.dp))
                    }
                }
            }
        }
    }

    songForPlaylistDialog?.let { song ->
        AddToPlaylistDialog(
            appStyle = AppStyle.STICKERS,
            playlists = uiState.playlists,
            onDismiss = { songForPlaylistDialog = null },
            onPlaylistSelected = { playlistId -> viewModel.addSongToPlaylist(playlistId, song.id); songForPlaylistDialog = null },
            onCreatePlaylist = { name -> viewModel.createPlaylistAndAddSong(name, song.id); songForPlaylistDialog = null },
        )
    }
}

@Composable
fun FolderDetailScreenSticker(viewModel: MusicViewModel, folderPath: String, onBack: () -> Unit, onSongClick: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val folder = uiState.folders.firstOrNull { it.path == folderPath }
    var songForPlaylistDialog by remember { mutableStateOf<Song?>(null) }

    Column(Modifier.fillMaxSize().background(StickerColors.Paper)) {
        StickerDetailTopBar(folder?.name ?: "Carpeta", onBack)
        if (folder == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Carpeta no encontrada", style = StickerType.HandwrittenSmall, color = StickerColors.Faded)
            }
            return@Column
        }
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
            item {
                Text("${folder.songs.size} canciones", style = StickerType.HandwrittenSmall, color = StickerColors.Faded)
            }
            itemsIndexed(folder.songs, key = { _, it -> it.id }) { index, song ->
                StickerDetailSongRow(
                    song = song,
                    isPlaying = uiState.currentSong?.id == song.id && uiState.playback.isPlaying,
                    isFavorite = song.id in uiState.favoriteSongIds,
                    tilt = stickerTilt(index),
                    onClick = { viewModel.playSong(song, fromList = folder.songs); onSongClick() },
                    onToggleFavorite = { viewModel.toggleFavorite(song.id) },
                    onAddToPlaylist = { songForPlaylistDialog = song },
                )
            }
        }
    }

    songForPlaylistDialog?.let { song ->
        AddToPlaylistDialog(
            appStyle = AppStyle.STICKERS,
            playlists = uiState.playlists,
            onDismiss = { songForPlaylistDialog = null },
            onPlaylistSelected = { playlistId -> viewModel.addSongToPlaylist(playlistId, song.id); songForPlaylistDialog = null },
            onCreatePlaylist = { name -> viewModel.createPlaylistAndAddSong(name, song.id); songForPlaylistDialog = null },
        )
    }
}

@Composable
fun PlaylistDetailScreenSticker(
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

    Column(Modifier.fillMaxSize().background(StickerColors.Paper)) {
        StickerDetailTopBar(playlist?.name ?: "Playlist", onBack) {
            Icon(Icons.Filled.Edit, contentDescription = "Renombrar playlist", tint = StickerColors.Ink, modifier = Modifier.clickable { showRenameDialog = true }.padding(6.dp))
            Icon(Icons.Filled.Delete, contentDescription = "Borrar playlist", tint = StickerColors.Ink, modifier = Modifier.clickable { showDeleteDialog = true }.padding(6.dp))
        }
        if (songs.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Esta playlist todavía no tiene canciones", style = StickerType.HandwrittenSmall, color = StickerColors.Faded)
            }
        } else {
            LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
                itemsIndexed(songs, key = { _, song -> song.id }) { index, song ->
                    StickerHardShadowBox(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), rotationDegrees = stickerTilt(index)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { viewModel.playSong(song, fromList = songs); onSongClick() }.padding(9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            AsyncImage(model = AlbumArtRequest(song.contentUri), contentDescription = null, modifier = Modifier.size(46.dp).clip(RoundedCornerShape(14.dp)), contentScale = ContentScale.Crop)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(song.title, style = StickerType.TitleMedium, color = StickerColors.Ink, maxLines = 1)
                                Text(song.artist, style = StickerType.HandwrittenSmall, color = StickerColors.Faded, maxLines = 1)
                            }
                            Icon(
                                Icons.Filled.KeyboardArrowUp,
                                contentDescription = "Subir",
                                tint = if (index > 0) StickerColors.Ink else StickerColors.Faded,
                                modifier = Modifier.clickable(enabled = index > 0) { viewModel.moveSongInPlaylist(playlistId, songs.map { it.id }, index, index - 1) }.padding(4.dp),
                            )
                            Icon(
                                Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Bajar",
                                tint = if (index < songs.lastIndex) StickerColors.Ink else StickerColors.Faded,
                                modifier = Modifier.clickable(enabled = index < songs.lastIndex) { viewModel.moveSongInPlaylist(playlistId, songs.map { it.id }, index, index + 1) }.padding(4.dp),
                            )
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Quitar de la playlist",
                                tint = StickerColors.Faded,
                                modifier = Modifier.clickable { viewModel.removeSongFromPlaylist(playlistId, song.id) }.padding(4.dp),
                            )
                        }
                    }
                }
            }
        }
    }

    if (showRenameDialog && playlist != null) {
        var name by remember { mutableStateOf(playlist.name) }
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Renombrar playlist") },
            text = { OutlinedTextField(value = name, onValueChange = { name = it }, singleLine = true, modifier = Modifier.fillMaxWidth()) },
            confirmButton = {
                TextButton(onClick = { viewModel.renamePlaylist(playlistId, name); showRenameDialog = false }, enabled = name.isNotBlank()) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { showRenameDialog = false }) { Text("Cancelar") } },
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Borrar playlist") },
            text = { Text("¿Seguro que quieres borrar \"${playlist?.name}\"? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = { viewModel.deletePlaylist(playlistId); showDeleteDialog = false; onPlaylistDeleted() }) { Text("Borrar") }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") } },
        )
    }
}
