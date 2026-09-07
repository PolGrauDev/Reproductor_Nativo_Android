package com.PolGrauDev.reproductor_nativo_android.ui.screens.style.papel

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.AppStyle
import com.PolGrauDev.reproductor_nativo_android.data.AlbumArtRequest
import com.PolGrauDev.reproductor_nativo_android.data.model.Song
import com.PolGrauDev.reproductor_nativo_android.ui.components.AddToPlaylistDialog
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelRowDivider
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelType
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicViewModel

@Composable
private fun PapelDetailTopBar(title: String, onBack: () -> Unit, actions: @Composable RowScope.() -> Unit = {}) {
    Row(Modifier.fillMaxWidth().padding(top = 10.dp, start = 4.dp, end = 20.dp), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = PapelColors.OnSurfaceVariant)
        }
        Text(title.uppercase(), style = PapelType.SectionLabel, color = PapelColors.Accent, modifier = Modifier.weight(1f))
        actions()
    }
}

@Composable
fun AlbumDetailScreenPapel(viewModel: MusicViewModel, albumId: Long?, onBack: () -> Unit, onSongClick: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val album = uiState.albums.firstOrNull { it.albumId == albumId }
    var songForPlaylistDialog by remember { mutableStateOf<Song?>(null) }

    Scaffold(containerColor = PapelColors.Background) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            PapelDetailTopBar("Álbum", onBack)
            if (album == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Álbum no encontrado", style = PapelType.BodyMedium, color = PapelColors.OnSurfaceVariant)
                }
                return@Column
            }
            LazyColumn(Modifier.fillMaxSize()) {
                item {
                    Column(Modifier.fillMaxWidth().padding(24.dp, 4.dp, 24.dp, 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        AsyncImage(
                            model = AlbumArtRequest(album.songs.first().contentUri),
                            contentDescription = null,
                            modifier = Modifier.size(172.dp).clip(RoundedCornerShape(2.dp)),
                            contentScale = ContentScale.Crop,
                        )
                        Spacer(Modifier.height(20.dp))
                        Text(album.title, style = PapelType.HeadlineLarge, maxLines = 2, textAlign = TextAlign.Center, color = PapelColors.OnSurface)
                        Text(
                            "${album.artist} · ${album.songs.size} canciones",
                            style = PapelType.SectionLabel,
                            color = PapelColors.OnSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                }
                itemsIndexed(album.songs, key = { _, it -> it.id }) { index, song ->
                    PapelDetailSongRow(
                        index = index + 1,
                        song = song,
                        isPlaying = uiState.currentSong?.id == song.id && uiState.playback.isPlaying,
                        isFavorite = song.id in uiState.favoriteSongIds,
                        onClick = { viewModel.playSong(song, fromList = album.songs); onSongClick() },
                        onToggleFavorite = { viewModel.toggleFavorite(song.id) },
                        onAddToPlaylist = { songForPlaylistDialog = song },
                    )
                }
            }
        }
    }

    songForPlaylistDialog?.let { song ->
        AddToPlaylistDialog(
            appStyle = AppStyle.PAPEL,
            playlists = uiState.playlists,
            onDismiss = { songForPlaylistDialog = null },
            onPlaylistSelected = { playlistId -> viewModel.addSongToPlaylist(playlistId, song.id); songForPlaylistDialog = null },
            onCreatePlaylist = { name -> viewModel.createPlaylistAndAddSong(name, song.id); songForPlaylistDialog = null },
        )
    }
}

@Composable
private fun PapelDetailSongRow(
    index: Int,
    song: Song,
    isPlaying: Boolean,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAddToPlaylist: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(20.dp, 14.dp, 20.dp, 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("$index", style = PapelType.Mono, color = PapelColors.Faint, modifier = Modifier.width(20.dp))
            Column(Modifier.weight(1f)) {
                Text(song.title, style = PapelType.TitleMedium, color = PapelColors.OnSurface, maxLines = 1)
                Text(song.artist, style = PapelType.BodySmall, color = PapelColors.OnSurfaceVariant, maxLines = 1)
            }
            if (isPlaying) {
                Icon(Icons.Filled.MusicNote, contentDescription = "Reproduciendo", tint = PapelColors.Accent, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
            }
            Icon(
                if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = if (isFavorite) "Quitar de favoritos" else "Añadir a favoritos",
                tint = if (isFavorite) PapelColors.Accent else PapelColors.Faint,
                modifier = Modifier.clickable(onClick = onToggleFavorite).padding(6.dp),
            )
            Spacer(Modifier.width(4.dp))
            Icon(
                Icons.Filled.Add,
                contentDescription = "Añadir a playlist",
                tint = PapelColors.OnSurfaceVariant,
                modifier = Modifier.clickable(onClick = onAddToPlaylist).padding(6.dp),
            )
        }
        PapelRowDivider()
    }
}

@Composable
fun ArtistDetailScreenPapel(viewModel: MusicViewModel, artistId: Long?, onBack: () -> Unit, onSongClick: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val artist = uiState.artists.firstOrNull { it.artistId == artistId }
    var songForPlaylistDialog by remember { mutableStateOf<Song?>(null) }

    Scaffold(containerColor = PapelColors.Background) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            PapelDetailTopBar(artist?.name ?: "Artista", onBack)
            if (artist == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Artista no encontrado", style = PapelType.BodyMedium, color = PapelColors.OnSurfaceVariant)
                }
                return@Column
            }
            LazyColumn(Modifier.fillMaxSize()) {
                items(artist.songs, key = { it.id }) { song ->
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable {
                                viewModel.playSong(song, fromList = artist.songs)
                                onSongClick()
                            }.padding(20.dp, 14.dp, 20.dp, 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            AsyncImage(
                                model = AlbumArtRequest(song.contentUri),
                                contentDescription = null,
                                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(2.dp)),
                                contentScale = ContentScale.Crop,
                            )
                            Spacer(Modifier.width(14.dp))
                            Column(Modifier.weight(1f)) {
                                Text(song.title, style = PapelType.TitleMedium, color = PapelColors.OnSurface, maxLines = 1)
                                Text(song.album, style = PapelType.BodySmall, color = PapelColors.OnSurfaceVariant, maxLines = 1)
                            }
                            val isFavorite = song.id in uiState.favoriteSongIds
                            Icon(
                                if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = null,
                                tint = if (isFavorite) PapelColors.Accent else PapelColors.Faint,
                                modifier = Modifier.clickable { viewModel.toggleFavorite(song.id) }.padding(6.dp),
                            )
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "Añadir a playlist",
                                tint = PapelColors.OnSurfaceVariant,
                                modifier = Modifier.clickable { songForPlaylistDialog = song }.padding(6.dp),
                            )
                        }
                        PapelRowDivider()
                    }
                }
            }
        }
    }

    songForPlaylistDialog?.let { song ->
        AddToPlaylistDialog(
            appStyle = AppStyle.PAPEL,
            playlists = uiState.playlists,
            onDismiss = { songForPlaylistDialog = null },
            onPlaylistSelected = { playlistId -> viewModel.addSongToPlaylist(playlistId, song.id); songForPlaylistDialog = null },
            onCreatePlaylist = { name -> viewModel.createPlaylistAndAddSong(name, song.id); songForPlaylistDialog = null },
        )
    }
}

@Composable
fun FolderDetailScreenPapel(viewModel: MusicViewModel, folderPath: String, onBack: () -> Unit, onSongClick: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val folder = uiState.folders.firstOrNull { it.path == folderPath }
    var songForPlaylistDialog by remember { mutableStateOf<Song?>(null) }

    Scaffold(containerColor = PapelColors.Background) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            PapelDetailTopBar("Carpeta", onBack)
            if (folder == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Carpeta no encontrada", style = PapelType.BodyMedium, color = PapelColors.OnSurfaceVariant)
                }
                return@Column
            }
            LazyColumn(Modifier.fillMaxSize()) {
                item {
                    Column(Modifier.fillMaxWidth().padding(20.dp, 8.dp, 20.dp, 16.dp)) {
                        Text(folder.name, style = PapelType.HeadlineLarge, maxLines = 2, color = PapelColors.OnSurface)
                        Text(
                            "${folder.songs.size} canciones",
                            style = PapelType.SectionLabel,
                            color = PapelColors.OnSurfaceVariant,
                            modifier = Modifier.padding(top = 6.dp),
                        )
                    }
                }
                itemsIndexed(folder.songs, key = { _, it -> it.id }) { index, song ->
                    PapelDetailSongRow(
                        index = index + 1,
                        song = song,
                        isPlaying = uiState.currentSong?.id == song.id && uiState.playback.isPlaying,
                        isFavorite = song.id in uiState.favoriteSongIds,
                        onClick = { viewModel.playSong(song, fromList = folder.songs); onSongClick() },
                        onToggleFavorite = { viewModel.toggleFavorite(song.id) },
                        onAddToPlaylist = { songForPlaylistDialog = song },
                    )
                }
            }
        }
    }

    songForPlaylistDialog?.let { song ->
        AddToPlaylistDialog(
            appStyle = AppStyle.PAPEL,
            playlists = uiState.playlists,
            onDismiss = { songForPlaylistDialog = null },
            onPlaylistSelected = { playlistId -> viewModel.addSongToPlaylist(playlistId, song.id); songForPlaylistDialog = null },
            onCreatePlaylist = { name -> viewModel.createPlaylistAndAddSong(name, song.id); songForPlaylistDialog = null },
        )
    }
}

@Composable
fun PlaylistDetailScreenPapel(
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

    Scaffold(containerColor = PapelColors.Background) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            PapelDetailTopBar(playlist?.name ?: "Playlist", onBack) {
                Icon(
                    Icons.Filled.Edit,
                    contentDescription = "Renombrar playlist",
                    tint = PapelColors.OnSurfaceVariant,
                    modifier = Modifier.clickable { showRenameDialog = true }.padding(6.dp),
                )
                Spacer(Modifier.width(4.dp))
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Borrar playlist",
                    tint = PapelColors.OnSurfaceVariant,
                    modifier = Modifier.clickable { showDeleteDialog = true }.padding(6.dp),
                )
            }
            if (songs.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Esta playlist todavía no tiene canciones", style = PapelType.BodyMedium, color = PapelColors.OnSurfaceVariant)
                }
            } else {
                LazyColumn(Modifier.fillMaxSize()) {
                    itemsIndexed(songs, key = { _, song -> song.id }) { index, song ->
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable {
                                    viewModel.playSong(song, fromList = songs)
                                    onSongClick()
                                }.padding(20.dp, 12.dp, 20.dp, 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                AsyncImage(
                                    model = AlbumArtRequest(song.contentUri),
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(2.dp)),
                                    contentScale = ContentScale.Crop,
                                )
                                Spacer(Modifier.width(14.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(song.title, style = PapelType.TitleMedium, color = PapelColors.OnSurface, maxLines = 1)
                                    Text(song.artist, style = PapelType.BodySmall, color = PapelColors.OnSurfaceVariant, maxLines = 1)
                                }
                                Icon(
                                    Icons.Filled.KeyboardArrowUp,
                                    contentDescription = "Subir",
                                    tint = if (index > 0) PapelColors.OnSurface else PapelColors.Faint,
                                    modifier = Modifier.clickable(enabled = index > 0) {
                                        viewModel.moveSongInPlaylist(playlistId, songs.map { it.id }, index, index - 1)
                                    }.padding(6.dp),
                                )
                                Icon(
                                    Icons.Filled.KeyboardArrowDown,
                                    contentDescription = "Bajar",
                                    tint = if (index < songs.lastIndex) PapelColors.OnSurface else PapelColors.Faint,
                                    modifier = Modifier.clickable(enabled = index < songs.lastIndex) {
                                        viewModel.moveSongInPlaylist(playlistId, songs.map { it.id }, index, index + 1)
                                    }.padding(6.dp),
                                )
                                Icon(
                                    Icons.Filled.Close,
                                    contentDescription = "Quitar de la playlist",
                                    tint = PapelColors.OnSurfaceVariant,
                                    modifier = Modifier.clickable { viewModel.removeSongFromPlaylist(playlistId, song.id) }.padding(6.dp),
                                )
                            }
                            PapelRowDivider()
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
