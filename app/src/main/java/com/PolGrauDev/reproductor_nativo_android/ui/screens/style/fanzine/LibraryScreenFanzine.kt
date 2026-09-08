package com.PolGrauDev.reproductor_nativo_android.ui.screens.style.fanzine

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.FanzineColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.FanzineFonts
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.fanzineTilt
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.photocopyGrain
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicUiState
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicViewModel

private val TABS = listOf("Temas", "Discos", "Grupos", "Carpetas", "Listas")

@Composable
fun LibraryScreenFanzine(
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

    Column(
        Modifier
            .fillMaxSize()
            .background(FanzineColors.Slate)
            .photocopyGrain(),
    ) {
        FanzineLibraryHeader(sortOrder = uiState.sortOrder, onSortSelect = viewModel::setSortOrder, onSettingsClick = onSettingsClick)
        FanzineSearchField(query = uiState.searchQuery, onQueryChange = viewModel::setSearchQuery)
        FanzineTabsRow(selectedTab = selectedTab, onSelect = { selectedTab = it })

        when {
            uiState.isLoadingLibrary -> FanzineCentered { CircularProgressIndicator(color = FanzineColors.Paper) }
            uiState.songs.isEmpty() && selectedTab != 4 -> FanzineCentered {
                Text("No se encontraron canciones en el dispositivo", fontFamily = FanzineFonts.SpecialElite, fontSize = 13.sp, color = FanzineColors.Faded)
            }
            else -> when (selectedTab) {
                0 -> FanzineSongsTab(
                    uiState = uiState,
                    onClick = { song -> viewModel.playSong(song); onSongClick() },
                    onToggleFavorite = viewModel::toggleFavorite,
                    onAddToPlaylist = { song -> songForPlaylistDialog = song },
                )
                1 -> FanzineAlbumsTab(uiState.albums, onAlbumClick)
                2 -> FanzineArtistsTab(uiState.artists, onArtistClick)
                3 -> FanzineFoldersTab(uiState.folders, onFolderClick)
                else -> FanzinePlaylistsTab(
                    playlists = uiState.playlists,
                    favoriteCount = uiState.favoriteSongIds.size,
                    onFavoritesClick = onFavoritesClick,
                    onPlaylistClick = onPlaylistClick,
                    onCreatePlaylist = viewModel::createPlaylist,
                )
            }
        }
    }

    songForPlaylistDialog?.let { song ->
        AddToPlaylistDialog(
            appStyle = AppStyle.FANZINE,
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
private fun FanzineCentered(content: @Composable () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
}

@Composable
private fun FanzineLibraryHeader(sortOrder: SortOrder, onSortSelect: (SortOrder) -> Unit, onSettingsClick: () -> Unit) {
    var sortMenuOpen by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth().padding(14.dp, 12.dp, 14.dp, 8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            "BIBLIOTECA",
            fontFamily = FanzineFonts.Anton,
            fontSize = 26.sp,
            color = FanzineColors.Paper,
            modifier = Modifier.weight(1f).rotate(-1f).background(FanzineColors.Red).padding(horizontal = 6.dp),
        )
        Column {
            Row {
                Icon(
                    Icons.AutoMirrored.Filled.Sort,
                    contentDescription = "Ordenar",
                    tint = FanzineColors.Ink,
                    modifier = Modifier
                        .size(30.dp)
                        .rotate(-2f)
                        .background(FanzineColors.Paper)
                        .clickable { sortMenuOpen = !sortMenuOpen }
                        .padding(4.dp),
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = "Ajustes",
                    tint = FanzineColors.Ink,
                    modifier = Modifier
                        .size(30.dp)
                        .rotate(2f)
                        .background(FanzineColors.Paper)
                        .clickable(onClick = onSettingsClick)
                        .padding(4.dp),
                )
            }
            if (sortMenuOpen) {
                Row(Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    SortOrder.entries.forEach { order ->
                        Text(
                            order.label,
                            fontFamily = FanzineFonts.SpecialElite,
                            fontSize = 10.sp,
                            color = if (order == sortOrder) FanzineColors.Paper else FanzineColors.Ink,
                            modifier = Modifier
                                .then(if (order == sortOrder) Modifier.background(FanzineColors.Red) else Modifier.background(FanzineColors.Paper))
                                .clickable { onSortSelect(order); sortMenuOpen = false }
                                .padding(horizontal = 6.dp, vertical = 3.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FanzineSearchField(query: String, onQueryChange: (String) -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(14.dp, 0.dp, 14.dp, 12.dp)
            .rotate(-0.7f)
            .background(FanzineColors.Paper),
    ) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 11.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Search, contentDescription = null, tint = FanzineColors.Red)
            Spacer(Modifier.width(9.dp))
            Box(Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text("buscar entre el ruido…", fontFamily = FanzineFonts.SpecialElite, fontSize = 13.sp, color = FanzineColors.Grime)
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FanzineFonts.SpecialElite, fontSize = 13.sp, color = FanzineColors.Ink),
                    cursorBrush = SolidColor(FanzineColors.Ink),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (query.isNotEmpty()) {
                Icon(Icons.Filled.Close, contentDescription = "Limpiar búsqueda", tint = FanzineColors.Grime, modifier = Modifier.clickable { onQueryChange("") })
            }
        }
    }
}

@Composable
private fun FanzineTabsRow(selectedTab: Int, onSelect: (Int) -> Unit) {
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        TABS.forEachIndexed { index, title ->
            val selected = index == selectedTab
            Text(
                title,
                fontFamily = FanzineFonts.Anton,
                fontSize = 13.sp,
                color = if (selected) FanzineColors.Ink else FanzineColors.Faded,
                modifier = Modifier
                    .rotate(if (index % 2 == 0) -1.5f else 1.5f)
                    .then(if (selected) Modifier.background(FanzineColors.Red) else Modifier.border(2.dp, FanzineColors.Hair))
                    .clickable { onSelect(index) }
                    .padding(horizontal = 9.dp, vertical = 4.dp),
            )
        }
    }
}

@Composable
private fun FanzineSongsTab(uiState: MusicUiState, onClick: (Song) -> Unit, onToggleFavorite: (Long) -> Unit, onAddToPlaylist: (Song) -> Unit) {
    val songs = uiState.filteredSongs
    if (songs.isEmpty()) {
        FanzineCentered { Text("Sin resultados", fontFamily = FanzineFonts.SpecialElite, fontSize = 13.sp, color = FanzineColors.Faded) }
        return
    }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        itemsIndexed(songs, key = { _, song -> song.id }) { index, song ->
            FanzineSongRow(
                song = song,
                isFavorite = song.id in uiState.favoriteSongIds,
                tilt = fanzineTilt(index),
                onClick = { onClick(song) },
                onToggleFavorite = { onToggleFavorite(song.id) },
                onAddToPlaylist = { onAddToPlaylist(song) },
            )
        }
    }
}

@Composable
fun FanzineSongRow(song: Song, isFavorite: Boolean, tilt: Float, onClick: () -> Unit, onToggleFavorite: () -> Unit, onAddToPlaylist: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .rotate(tilt)
            .background(FanzineColors.Paper)
            .clickable(onClick = onClick)
            .padding(11.dp, 8.dp, 11.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = AlbumArtRequest(song.contentUri),
            contentDescription = null,
            modifier = Modifier.size(46.dp),
            contentScale = ContentScale.Crop,
        )
        Spacer(Modifier.width(11.dp))
        Column(Modifier.weight(1f)) {
            Text(song.title.uppercase(), fontFamily = FanzineFonts.Anton, fontSize = 17.sp, color = FanzineColors.Ink, maxLines = 1)
            Text(song.artist, fontFamily = FanzineFonts.SpecialElite, fontSize = 13.sp, color = FanzineColors.Grime, maxLines = 1)
        }
        Icon(
            if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = if (isFavorite) "Quitar de favoritos" else "Añadir a favoritos",
            tint = FanzineColors.Red,
            modifier = Modifier.clickable(onClick = onToggleFavorite).padding(6.dp),
        )
        Spacer(Modifier.width(4.dp))
        Icon(
            Icons.Filled.Add,
            contentDescription = "Añadir a playlist",
            tint = FanzineColors.Ink,
            modifier = Modifier.clickable(onClick = onAddToPlaylist).padding(6.dp),
        )
    }
}

@Composable
private fun FanzineAlbumsTab(albums: List<AlbumGroup>, onClick: (AlbumGroup) -> Unit) {
    if (albums.isEmpty()) {
        FanzineCentered { Text("Sin resultados", fontFamily = FanzineFonts.SpecialElite, fontSize = 13.sp, color = FanzineColors.Faded) }
        return
    }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        itemsIndexed(albums, key = { _, it -> it.albumId ?: -1L }) { index, album ->
            Row(
                modifier = Modifier.fillMaxWidth().rotate(fanzineTilt(index)).background(FanzineColors.Paper).clickable { onClick(album) }.padding(11.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AsyncImage(model = AlbumArtRequest(album.songs.first().contentUri), contentDescription = null, modifier = Modifier.size(46.dp), contentScale = ContentScale.Crop)
                Spacer(Modifier.width(11.dp))
                Column(Modifier.weight(1f)) {
                    Text(album.title.uppercase(), fontFamily = FanzineFonts.Anton, fontSize = 16.sp, color = FanzineColors.Ink, maxLines = 1)
                    Text("${album.artist} · ${album.songs.size} canciones", fontFamily = FanzineFonts.SpecialElite, fontSize = 12.sp, color = FanzineColors.Grime, maxLines = 1)
                }
            }
        }
    }
}

@Composable
private fun FanzineArtistsTab(artists: List<ArtistGroup>, onClick: (ArtistGroup) -> Unit) {
    if (artists.isEmpty()) {
        FanzineCentered { Text("Sin resultados", fontFamily = FanzineFonts.SpecialElite, fontSize = 13.sp, color = FanzineColors.Faded) }
        return
    }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        itemsIndexed(artists, key = { _, it -> it.artistId ?: -1L }) { index, artist ->
            Row(
                modifier = Modifier.fillMaxWidth().rotate(fanzineTilt(index)).background(FanzineColors.Paper).clickable { onClick(artist) }.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(artist.name.uppercase(), fontFamily = FanzineFonts.Anton, fontSize = 16.sp, color = FanzineColors.Ink, maxLines = 1)
                    Text("${artist.songs.size} canciones", fontFamily = FanzineFonts.SpecialElite, fontSize = 12.sp, color = FanzineColors.Grime, maxLines = 1)
                }
            }
        }
    }
}

@Composable
private fun FanzineFoldersTab(folders: List<FolderGroup>, onClick: (FolderGroup) -> Unit) {
    if (folders.isEmpty()) {
        FanzineCentered { Text("Sin resultados", fontFamily = FanzineFonts.SpecialElite, fontSize = 13.sp, color = FanzineColors.Faded) }
        return
    }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        itemsIndexed(folders, key = { _, it -> it.path }) { index, folder ->
            Row(
                modifier = Modifier.fillMaxWidth().rotate(fanzineTilt(index)).background(FanzineColors.Paper).clickable { onClick(folder) }.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(folder.name.uppercase(), fontFamily = FanzineFonts.Anton, fontSize = 16.sp, color = FanzineColors.Ink, maxLines = 1)
                    Text("${folder.songs.size} canciones", fontFamily = FanzineFonts.SpecialElite, fontSize = 12.sp, color = FanzineColors.Grime, maxLines = 1)
                }
            }
        }
    }
}

@Composable
private fun FanzinePlaylistsTab(
    playlists: List<PlaylistSummary>,
    favoriteCount: Int,
    onFavoritesClick: () -> Unit,
    onPlaylistClick: (Long) -> Unit,
    onCreatePlaylist: (String) -> Unit,
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().rotate(-0.8f).background(FanzineColors.Paper).clickable(onClick = onFavoritesClick).padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.Favorite, contentDescription = null, tint = FanzineColors.Red)
                Spacer(Modifier.width(11.dp))
                Column(Modifier.weight(1f)) {
                    Text("FAVORITAS", fontFamily = FanzineFonts.Anton, fontSize = 16.sp, color = FanzineColors.Ink)
                    Text("$favoriteCount canciones", fontFamily = FanzineFonts.SpecialElite, fontSize = 12.sp, color = FanzineColors.Grime)
                }
            }
        }
        itemsIndexed(playlists, key = { _, it -> it.id }) { index, playlist ->
            Row(
                modifier = Modifier.fillMaxWidth().rotate(fanzineTilt(index)).background(FanzineColors.Paper).clickable { onPlaylistClick(playlist.id) }.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(playlist.name.uppercase(), fontFamily = FanzineFonts.Anton, fontSize = 16.sp, color = FanzineColors.Ink, maxLines = 1)
                    Text("${playlist.songCount} canciones", fontFamily = FanzineFonts.SpecialElite, fontSize = 12.sp, color = FanzineColors.Grime)
                }
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { showCreateDialog = true }.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, tint = FanzineColors.Paper)
                Spacer(Modifier.width(11.dp))
                Text("nueva lista", fontFamily = FanzineFonts.SpecialElite, fontSize = 13.sp, color = FanzineColors.Paper)
            }
        }
    }

    if (showCreateDialog) {
        FanzineCreatePlaylistDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { onCreatePlaylist(it); showCreateDialog = false },
        )
    }
}

@Composable
private fun FanzineCreatePlaylistDialog(onDismiss: () -> Unit, onCreate: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismiss) {
        Column(Modifier.rotate(-1f).background(FanzineColors.Paper)) {
            Row(Modifier.fillMaxWidth().background(FanzineColors.Red).padding(14.dp, 11.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Add, contentDescription = null, tint = FanzineColors.Ink)
                Spacer(Modifier.width(8.dp))
                Text("NUEVA PLAYLIST", fontFamily = FanzineFonts.Anton, fontSize = 20.sp, color = FanzineColors.Ink)
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
                        "crear",
                        fontFamily = FanzineFonts.SpecialElite,
                        fontSize = 11.sp,
                        color = FanzineColors.Paper,
                        modifier = Modifier
                            .background(FanzineColors.Ink)
                            .clickable(enabled = name.isNotBlank()) { onCreate(name) }
                            .padding(12.dp, 7.dp),
                    )
                }
            }
        }
    }
}
