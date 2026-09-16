package com.PolGrauDev.reproductor_nativo_android.ui.screens.style.papel

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.SubcomposeAsyncImage
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.AlbumArtFallbackPapel
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.AppStyle
import com.PolGrauDev.reproductor_nativo_android.data.AlbumArtRequest
import com.PolGrauDev.reproductor_nativo_android.data.SongArtStorage
import com.PolGrauDev.reproductor_nativo_android.data.model.Song
import com.PolGrauDev.reproductor_nativo_android.ui.components.AddSongsToPlaylistDialog
import com.PolGrauDev.reproductor_nativo_android.ui.components.AddToPlaylistDialog
import com.PolGrauDev.reproductor_nativo_android.ui.components.EditSongDialog
import com.PolGrauDev.reproductor_nativo_android.ui.components.SongOptionsMenu
import com.PolGrauDev.reproductor_nativo_android.ui.components.rememberPlaylistDragReorderState
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelRowDivider
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelSectionLabel
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelType
import com.PolGrauDev.reproductor_nativo_android.ui.util.shareSong
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicViewModel
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem

@Composable
private fun PapelDetailTopBar(title: String, onBack: () -> Unit, actions: @Composable RowScope.() -> Unit = {}) {
    Row(Modifier.fillMaxWidth().padding(top = 10.dp, start = 4.dp, end = 20.dp), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = PapelColors.OnSurfaceVariant)
        }
        Text(
            title.uppercase(),
            style = PapelType.SectionLabel,
            color = PapelColors.Accent,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip,
            modifier = Modifier.weight(1f).horizontalScroll(rememberScrollState()),
        )
        actions()
    }
}

@Composable
fun AlbumDetailScreenPapel(viewModel: MusicViewModel, albumId: Long?, onBack: () -> Unit, onSongClick: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val album = uiState.albums.firstOrNull { it.albumId == albumId }
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
                        SubcomposeAsyncImage(
                            model = AlbumArtRequest(album.songs.first().contentUri, album.songs.first().id),
                            contentDescription = null,
                            modifier = Modifier.size(172.dp).clip(RoundedCornerShape(2.dp)),
                            contentScale = ContentScale.Crop,
                            loading = { AlbumArtFallbackPapel() },
                            error = { AlbumArtFallbackPapel() },
                        )
                        Spacer(Modifier.height(20.dp))
                        Text(
                            album.title,
                            style = PapelType.HeadlineLarge,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Clip,
                            textAlign = TextAlign.Center,
                            color = PapelColors.OnSurface,
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        )
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

    songForPlaylistDialog?.let { song ->
        val playlistIdsWithSong by remember(song.id) { viewModel.playlistIdsContainingSong(song.id) }
            .collectAsStateWithLifecycle(initialValue = emptySet())
        AddToPlaylistDialog(
            appStyle = AppStyle.PAPEL,
            playlists = uiState.playlists,
            playlistIdsWithSong = playlistIdsWithSong,
            onDismiss = { songForPlaylistDialog = null },
            onPlaylistSelected = { playlistId -> viewModel.addSongToPlaylist(playlistId, song.id); songForPlaylistDialog = null },
            onCreatePlaylist = { name -> viewModel.createPlaylistAndAddSong(name, song.id); songForPlaylistDialog = null },
        )
    }

    songForEditDialog?.let { song ->
        EditSongDialog(
            appStyle = AppStyle.PAPEL,
            song = song,
            onDismiss = { songForEditDialog = null },
            onSave = { title, artist, album -> viewModel.setSongInfo(song.id, title, artist, album); songForEditDialog = null },
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
    onChangeImage: () -> Unit,
    onEditInfo: () -> Unit,
    onShare: () -> Unit,
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
            var menuExpanded by remember { mutableStateOf(false) }
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "Más opciones", tint = PapelColors.OnSurfaceVariant)
                }
                SongOptionsMenu(
                    appStyle = AppStyle.PAPEL,
                    expanded = menuExpanded,
                    isFavorite = isFavorite,
                    onDismiss = { menuExpanded = false },
                    onAddToPlaylist = onAddToPlaylist,
                    onToggleFavorite = onToggleFavorite,
                    onChangeImage = onChangeImage,
                    onEditInfo = onEditInfo,
                    onShare = onShare,
                )
            }
        }
        PapelRowDivider()
    }
}

@Composable
fun ArtistDetailScreenPapel(viewModel: MusicViewModel, artistId: Long?, onBack: () -> Unit, onSongClick: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val artist = uiState.artists.firstOrNull { it.artistId == artistId }
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
                            SubcomposeAsyncImage(
                                model = AlbumArtRequest(song.contentUri, song.id),
                                contentDescription = null,
                                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(2.dp)),
                                contentScale = ContentScale.Crop,
                                loading = { AlbumArtFallbackPapel() },
                                error = { AlbumArtFallbackPapel() },
                            )
                            Spacer(Modifier.width(14.dp))
                            Column(Modifier.weight(1f)) {
                                Text(song.title, style = PapelType.TitleMedium, color = PapelColors.OnSurface, maxLines = 1)
                                Text(song.album, style = PapelType.BodySmall, color = PapelColors.OnSurfaceVariant, maxLines = 1)
                            }
                            val isFavorite = song.id in uiState.favoriteSongIds
                            var menuExpanded by remember { mutableStateOf(false) }
                            Box {
                                IconButton(onClick = { menuExpanded = true }) {
                                    Icon(Icons.Filled.MoreVert, contentDescription = "Más opciones", tint = PapelColors.OnSurfaceVariant)
                                }
                                SongOptionsMenu(
                                    appStyle = AppStyle.PAPEL,
                                    expanded = menuExpanded,
                                    isFavorite = isFavorite,
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
            onPlaylistSelected = { playlistId -> viewModel.addSongToPlaylist(playlistId, song.id); songForPlaylistDialog = null },
            onCreatePlaylist = { name -> viewModel.createPlaylistAndAddSong(name, song.id); songForPlaylistDialog = null },
        )
    }

    songForEditDialog?.let { song ->
        EditSongDialog(
            appStyle = AppStyle.PAPEL,
            song = song,
            onDismiss = { songForEditDialog = null },
            onSave = { title, artist, album -> viewModel.setSongInfo(song.id, title, artist, album); songForEditDialog = null },
        )
    }
}

@Composable
fun FolderDetailScreenPapel(viewModel: MusicViewModel, folderPath: String, onBack: () -> Unit, onSongClick: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val folder = uiState.folders.firstOrNull { it.path == folderPath }
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
                        Text(
                            folder.name,
                            style = PapelType.HeadlineLarge,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Clip,
                            color = PapelColors.OnSurface,
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        )
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

    songForPlaylistDialog?.let { song ->
        val playlistIdsWithSong by remember(song.id) { viewModel.playlistIdsContainingSong(song.id) }
            .collectAsStateWithLifecycle(initialValue = emptySet())
        AddToPlaylistDialog(
            appStyle = AppStyle.PAPEL,
            playlists = uiState.playlists,
            playlistIdsWithSong = playlistIdsWithSong,
            onDismiss = { songForPlaylistDialog = null },
            onPlaylistSelected = { playlistId -> viewModel.addSongToPlaylist(playlistId, song.id); songForPlaylistDialog = null },
            onCreatePlaylist = { name -> viewModel.createPlaylistAndAddSong(name, song.id); songForPlaylistDialog = null },
        )
    }

    songForEditDialog?.let { song ->
        EditSongDialog(
            appStyle = AppStyle.PAPEL,
            song = song,
            onDismiss = { songForEditDialog = null },
            onSave = { title, artist, album -> viewModel.setSongInfo(song.id, title, artist, album); songForEditDialog = null },
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
    var showAddSongsDialog by remember { mutableStateOf(false) }
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
            PapelDetailTopBar(playlist?.name ?: "Playlist", onBack) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Añadir canciones",
                    tint = PapelColors.OnSurfaceVariant,
                    modifier = Modifier.clickable { showAddSongsDialog = true }.padding(6.dp),
                )
                Spacer(Modifier.width(4.dp))
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
                val dragState = rememberPlaylistDragReorderState(songs) { from, to ->
                    viewModel.moveSongInPlaylist(playlistId, songs.map { it.id }, from, to)
                }
                LazyColumn(Modifier.fillMaxSize(), state = dragState.lazyListState) {
                    itemsIndexed(dragState.songs, key = { _, song -> song.id }) { index, song ->
                        ReorderableItem(dragState.reorderableState, key = song.id) { isDragging ->
                            val rowInteractionSource = remember { MutableInteractionSource() }
                            Column(Modifier.alpha(if (isDragging) 0.85f else 1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                        .longPressDraggableHandle(
                                            onDragStarted = { dragState.onDragStarted(song) },
                                            onDragStopped = { dragState.onDragStopped() },
                                        )
                                        .clickable(interactionSource = rowInteractionSource, indication = null) {
                                            viewModel.playSong(song, fromList = songs)
                                            onSongClick()
                                        }.padding(20.dp, 12.dp, 20.dp, 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    SubcomposeAsyncImage(
                                        model = AlbumArtRequest(song.contentUri, song.id),
                                        contentDescription = null,
                                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(2.dp)),
                                        contentScale = ContentScale.Crop,
                                        loading = { AlbumArtFallbackPapel() },
                                        error = { AlbumArtFallbackPapel() },
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
                                    val isFavorite = song.id in uiState.favoriteSongIds
                                    var menuExpanded by remember { mutableStateOf(false) }
                                    Box {
                                        IconButton(onClick = { menuExpanded = true }) {
                                            Icon(Icons.Filled.MoreVert, contentDescription = "Más opciones", tint = PapelColors.OnSurfaceVariant)
                                        }
                                        SongOptionsMenu(
                                            appStyle = AppStyle.PAPEL,
                                            expanded = menuExpanded,
                                            isFavorite = isFavorite,
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
        }
    }

    if (showRenameDialog && playlist != null) {
        PapelRenamePlaylistDialog(
            currentName = playlist.name,
            onDismiss = { showRenameDialog = false },
            onRename = { viewModel.renamePlaylist(playlistId, it); showRenameDialog = false },
        )
    }

    if (showDeleteDialog) {
        PapelDeletePlaylistDialog(
            playlistName = playlist?.name ?: "",
            onDismiss = { showDeleteDialog = false },
            onDelete = { viewModel.deletePlaylist(playlistId); showDeleteDialog = false; onPlaylistDeleted() },
        )
    }

    if (showAddSongsDialog) {
        AddSongsToPlaylistDialog(
            appStyle = AppStyle.PAPEL,
            allSongs = uiState.songs,
            songIdsAlreadyInPlaylist = songs.map { it.id }.toSet(),
            onDismiss = { showAddSongsDialog = false },
            onAddSongs = { ids ->
                ids.forEach { viewModel.addSongToPlaylist(playlistId, it) }
                showAddSongsDialog = false
            },
        )
    }

    songForPlaylistDialog?.let { song ->
        val playlistIdsWithSong by remember(song.id) { viewModel.playlistIdsContainingSong(song.id) }
            .collectAsStateWithLifecycle(initialValue = emptySet())
        AddToPlaylistDialog(
            appStyle = AppStyle.PAPEL,
            playlists = uiState.playlists,
            playlistIdsWithSong = playlistIdsWithSong,
            onDismiss = { songForPlaylistDialog = null },
            onPlaylistSelected = { targetPlaylistId -> viewModel.addSongToPlaylist(targetPlaylistId, song.id); songForPlaylistDialog = null },
            onCreatePlaylist = { name -> viewModel.createPlaylistAndAddSong(name, song.id); songForPlaylistDialog = null },
        )
    }

    songForEditDialog?.let { song ->
        EditSongDialog(
            appStyle = AppStyle.PAPEL,
            song = song,
            onDismiss = { songForEditDialog = null },
            onSave = { title, artist, album -> viewModel.setSongInfo(song.id, title, artist, album); songForEditDialog = null },
        )
    }
}

@Composable
private fun PapelRenamePlaylistDialog(currentName: String, onDismiss: () -> Unit, onRename: (String) -> Unit) {
    var name by remember { mutableStateOf(currentName) }
    Dialog(onDismissRequest = onDismiss) {
        Column(Modifier.background(PapelColors.Surface).padding(22.dp)) {
            PapelSectionLabel("Renombrar")
            Text("Renombrar playlist", style = PapelType.TitleLarge, color = PapelColors.OnSurface)
            Spacer(Modifier.height(14.dp))
            BasicTextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
                textStyle = PapelType.BodyMedium.copy(color = PapelColors.OnSurface),
                cursorBrush = SolidColor(PapelColors.Primary),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            PapelRowDivider()
            Spacer(Modifier.height(22.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(
                    "CANCELAR",
                    style = PapelType.SectionLabel,
                    color = PapelColors.OnSurfaceVariant,
                    modifier = Modifier.clickable(onClick = onDismiss).padding(8.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "GUARDAR",
                    style = PapelType.SectionLabel,
                    color = if (name.isNotBlank()) PapelColors.OnSurface else PapelColors.Faint,
                    modifier = Modifier
                        .clickable(enabled = name.isNotBlank()) { onRename(name) }
                        .padding(8.dp),
                )
            }
        }
    }
}

@Composable
private fun PapelDeletePlaylistDialog(playlistName: String, onDismiss: () -> Unit, onDelete: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Column(Modifier.background(PapelColors.Surface).padding(22.dp)) {
            PapelSectionLabel("Confirmar")
            Text("Borrar playlist", style = PapelType.TitleLarge, color = PapelColors.OnSurface)
            Spacer(Modifier.height(14.dp))
            Text(
                "¿Seguro que quieres borrar \"$playlistName\"? Esta acción no se puede deshacer.",
                style = PapelType.BodyMedium,
                color = PapelColors.OnSurfaceVariant,
            )
            Spacer(Modifier.height(22.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(
                    "CANCELAR",
                    style = PapelType.SectionLabel,
                    color = PapelColors.OnSurfaceVariant,
                    modifier = Modifier.clickable(onClick = onDismiss).padding(8.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "BORRAR",
                    style = PapelType.SectionLabel,
                    color = PapelColors.OnSurface,
                    modifier = Modifier.clickable(onClick = onDelete).padding(8.dp),
                )
            }
        }
    }
}
