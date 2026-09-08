package com.PolGrauDev.reproductor_nativo_android.ui.screens.style.papel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.AppStyle
import com.PolGrauDev.reproductor_nativo_android.data.AlbumArtRequest
import com.PolGrauDev.reproductor_nativo_android.data.model.AlbumGroup
import com.PolGrauDev.reproductor_nativo_android.data.model.ArtistGroup
import com.PolGrauDev.reproductor_nativo_android.data.model.FolderGroup
import com.PolGrauDev.reproductor_nativo_android.data.model.PlaylistSummary
import com.PolGrauDev.reproductor_nativo_android.data.model.Song
import com.PolGrauDev.reproductor_nativo_android.data.model.SortOrder
import com.PolGrauDev.reproductor_nativo_android.ui.components.AddToPlaylistDialog
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelRowDivider
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelSectionDivider
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelSectionLabel
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelType
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicUiState
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicViewModel

private val TABS = listOf("Canciones", "Álbumes", "Artistas", "Carpetas", "Playlists")

@Composable
fun LibraryScreenPapel(
    viewModel: MusicViewModel,
    uiState: MusicUiState,
    onSongClick: () -> Unit,
    onAlbumClick: (AlbumGroup) -> Unit,
    onArtistClick: (ArtistGroup) -> Unit,
    onFolderClick: (FolderGroup) -> Unit,
    onFavoritesClick: () -> Unit,
    onPlaylistClick: (Long) -> Unit,
    onSettingsClick: () -> Unit,
) {
    var selectedTab by remember { mutableStateOf(0) }
    var songForPlaylistDialog by remember { mutableStateOf<Song?>(null) }

    Scaffold(containerColor = PapelColors.Background) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            PapelLibraryHeader(
                sortOrder = uiState.sortOrder,
                onSortSelect = viewModel::setSortOrder,
                onSettingsClick = onSettingsClick,
            )
            PapelSearchField(query = uiState.searchQuery, onQueryChange = viewModel::setSearchQuery)
            PapelTabsRow(selectedTab = selectedTab, onSelect = { selectedTab = it })
            PapelSectionDivider()

            when {
                uiState.isLoadingLibrary -> PapelCentered { CircularProgressIndicator(color = PapelColors.Primary) }
                uiState.songs.isEmpty() && selectedTab != 4 -> PapelCentered {
                    Text("No se encontraron canciones en el dispositivo", style = PapelType.BodyMedium, color = PapelColors.OnSurfaceVariant)
                }
                else -> when (selectedTab) {
                    0 -> PapelSongsTab(
                        uiState = uiState,
                        onClick = { song -> viewModel.playSong(song); onSongClick() },
                        onToggleFavorite = viewModel::toggleFavorite,
                        onAddToPlaylist = { song -> songForPlaylistDialog = song },
                    )
                    1 -> PapelAlbumsTab(uiState.albums, onAlbumClick)
                    2 -> PapelArtistsTab(uiState.artists, onArtistClick)
                    3 -> PapelFoldersTab(uiState.folders, onFolderClick)
                    else -> PapelPlaylistsTab(
                        playlists = uiState.playlists,
                        favoriteCount = uiState.favoriteSongIds.size,
                        onFavoritesClick = onFavoritesClick,
                        onPlaylistClick = onPlaylistClick,
                        onCreatePlaylist = viewModel::createPlaylist,
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
}

@Composable
private fun PapelCentered(content: @Composable () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
}

@Composable
private fun PapelLibraryHeader(sortOrder: SortOrder, onSortSelect: (SortOrder) -> Unit, onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(20.dp, 26.dp, 20.dp, 14.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        Column(Modifier.weight(1f)) {
            PapelSectionLabel("Biblioteca")
            Text("Tu colección", style = PapelType.HeadlineLarge, color = PapelColors.OnSurface)
        }
        var sortMenuOpen by remember { mutableStateOf(false) }
        Box {
            IconButton(onClick = { sortMenuOpen = true }) {
                Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Ordenar", tint = PapelColors.OnSurfaceVariant)
            }
            DropdownMenu(expanded = sortMenuOpen, onDismissRequest = { sortMenuOpen = false }) {
                SortOrder.entries.forEach { order ->
                    DropdownMenuItem(
                        text = { Text(order.label) },
                        onClick = { onSortSelect(order); sortMenuOpen = false },
                    )
                }
            }
        }
        IconButton(onClick = onSettingsClick) {
            Icon(Icons.Filled.Settings, contentDescription = "Ajustes", tint = PapelColors.OnSurfaceVariant)
        }
    }
}

@Composable
private fun PapelSearchField(query: String, onQueryChange: (String) -> Unit) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 0.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Filled.Search, contentDescription = null, tint = PapelColors.OnSurfaceVariant, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(10.dp))
            Box(Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text("Buscar en la colección", style = PapelType.BodyMedium, color = PapelColors.OnSurfaceVariant)
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = PapelType.BodyMedium.copy(color = PapelColors.OnSurface),
                    cursorBrush = androidx.compose.ui.graphics.SolidColor(PapelColors.Primary),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (query.isNotEmpty()) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Limpiar búsqueda",
                    tint = PapelColors.OnSurfaceVariant,
                    modifier = Modifier.size(18.dp).clickable { onQueryChange("") },
                )
            }
        }
        PapelRowDivider()
    }
}

@Composable
private fun PapelTabsRow(selectedTab: Int, onSelect: (Int) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
        TABS.forEachIndexed { index, title ->
            val selected = index == selectedTab
            Text(
                title.uppercase(),
                style = PapelType.SectionLabel,
                color = if (selected) PapelColors.OnSurface else PapelColors.Faint,
                modifier = Modifier
                    .padding(end = 20.dp, bottom = 10.dp)
                    .clickable { onSelect(index) },
            )
        }
    }
}

@Composable
private fun PapelSongsTab(uiState: MusicUiState, onClick: (Song) -> Unit, onToggleFavorite: (Long) -> Unit, onAddToPlaylist: (Song) -> Unit) {
    val songs = uiState.filteredSongs
    if (songs.isEmpty()) {
        PapelCentered { Text("Sin resultados", style = PapelType.BodyMedium, color = PapelColors.OnSurfaceVariant) }
        return
    }
    LazyColumn(Modifier.fillMaxSize()) {
        items(songs, key = { it.id }) { song ->
            PapelSongRow(
                song = song,
                isFavorite = song.id in uiState.favoriteSongIds,
                onClick = { onClick(song) },
                onToggleFavorite = { onToggleFavorite(song.id) },
                onAddToPlaylist = { onAddToPlaylist(song) },
            )
        }
    }
}

@Composable
fun PapelSongRow(song: Song, isFavorite: Boolean, onClick: () -> Unit, onToggleFavorite: () -> Unit, onAddToPlaylist: () -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = AlbumArtRequest(song.contentUri),
                contentDescription = null,
                modifier = Modifier.size(44.dp).clip(androidx.compose.foundation.shape.RoundedCornerShape(2.dp)),
                contentScale = ContentScale.Crop,
            )
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(song.title, style = PapelType.TitleMedium, color = PapelColors.OnSurface, maxLines = 1)
                Text(song.artist, style = PapelType.BodySmall, color = PapelColors.OnSurfaceVariant, maxLines = 1)
            }
            Icon(
                if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = if (isFavorite) "Quitar de favoritos" else "Añadir a favoritos",
                tint = if (isFavorite) PapelColors.Accent else PapelColors.Faint,
                modifier = Modifier.clickable(onClick = onToggleFavorite).padding(6.dp),
            )
            Spacer(Modifier.width(6.dp))
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
private fun PapelAlbumsTab(albums: List<AlbumGroup>, onClick: (AlbumGroup) -> Unit) {
    if (albums.isEmpty()) {
        PapelCentered { Text("Sin resultados", style = PapelType.BodyMedium, color = PapelColors.OnSurfaceVariant) }
        return
    }
    LazyColumn(Modifier.fillMaxSize()) {
        items(albums, key = { it.albumId ?: -1L }) { album ->
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onClick(album) }.padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AsyncImage(
                        model = AlbumArtRequest(album.songs.first().contentUri),
                        contentDescription = null,
                        modifier = Modifier.size(44.dp).clip(androidx.compose.foundation.shape.RoundedCornerShape(2.dp)),
                        contentScale = ContentScale.Crop,
                    )
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(album.title, style = PapelType.TitleMedium, color = PapelColors.OnSurface, maxLines = 1)
                        Text("${album.artist} · ${album.songs.size} canciones", style = PapelType.BodySmall, color = PapelColors.OnSurfaceVariant, maxLines = 1)
                    }
                }
                PapelRowDivider()
            }
        }
    }
}

@Composable
private fun PapelArtistsTab(artists: List<ArtistGroup>, onClick: (ArtistGroup) -> Unit) {
    if (artists.isEmpty()) {
        PapelCentered { Text("Sin resultados", style = PapelType.BodyMedium, color = PapelColors.OnSurfaceVariant) }
        return
    }
    LazyColumn(Modifier.fillMaxSize()) {
        items(artists, key = { it.artistId ?: -1L }) { artist ->
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onClick(artist) }.padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(artist.name, style = PapelType.TitleMedium, color = PapelColors.OnSurface, maxLines = 1)
                        Text("${artist.songs.size} canciones", style = PapelType.BodySmall, color = PapelColors.OnSurfaceVariant, maxLines = 1)
                    }
                }
                PapelRowDivider()
            }
        }
    }
}

@Composable
private fun PapelFoldersTab(folders: List<FolderGroup>, onClick: (FolderGroup) -> Unit) {
    if (folders.isEmpty()) {
        PapelCentered { Text("Sin resultados", style = PapelType.BodyMedium, color = PapelColors.OnSurfaceVariant) }
        return
    }
    LazyColumn(Modifier.fillMaxSize()) {
        items(folders, key = { it.path }) { folder ->
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onClick(folder) }.padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(folder.name, style = PapelType.TitleMedium, color = PapelColors.OnSurface, maxLines = 1)
                        Text("${folder.songs.size} canciones", style = PapelType.BodySmall, color = PapelColors.OnSurfaceVariant, maxLines = 1)
                    }
                }
                PapelRowDivider()
            }
        }
    }
}

@Composable
private fun PapelPlaylistsTab(
    playlists: List<PlaylistSummary>,
    favoriteCount: Int,
    onFavoritesClick: () -> Unit,
    onPlaylistClick: (Long) -> Unit,
    onCreatePlaylist: (String) -> Unit,
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    LazyColumn(Modifier.fillMaxSize()) {
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable(onClick = onFavoritesClick).padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Filled.Favorite, contentDescription = null, tint = PapelColors.Accent)
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Favoritos", style = PapelType.TitleMedium, color = PapelColors.OnSurface)
                        Text("$favoriteCount canciones", style = PapelType.BodySmall, color = PapelColors.OnSurfaceVariant)
                    }
                }
                PapelRowDivider()
            }
        }
        items(playlists, key = { it.id }) { playlist ->
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onPlaylistClick(playlist.id) }.padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(playlist.name, style = PapelType.TitleMedium, color = PapelColors.OnSurface, maxLines = 1)
                        Text("${playlist.songCount} canciones", style = PapelType.BodySmall, color = PapelColors.OnSurfaceVariant)
                    }
                }
                PapelRowDivider()
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { showCreateDialog = true }.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, tint = PapelColors.OnSurface)
                Spacer(Modifier.width(14.dp))
                Text("Nueva playlist", style = PapelType.BodyMedium, color = PapelColors.OnSurface)
            }
        }
    }

    if (showCreateDialog) {
        PapelCreatePlaylistDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { onCreatePlaylist(it); showCreateDialog = false },
        )
    }
}

@Composable
private fun PapelCreatePlaylistDialog(onDismiss: () -> Unit, onCreate: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismiss) {
        Column(Modifier.background(PapelColors.Surface).padding(22.dp)) {
            PapelSectionLabel("Nueva")
            Text("Nueva playlist", style = PapelType.TitleLarge, color = PapelColors.OnSurface)
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
                    "CREAR",
                    style = PapelType.SectionLabel,
                    color = if (name.isNotBlank()) PapelColors.OnSurface else PapelColors.Faint,
                    modifier = Modifier
                        .clickable(enabled = name.isNotBlank()) { onCreate(name) }
                        .padding(8.dp),
                )
            }
        }
    }
}
