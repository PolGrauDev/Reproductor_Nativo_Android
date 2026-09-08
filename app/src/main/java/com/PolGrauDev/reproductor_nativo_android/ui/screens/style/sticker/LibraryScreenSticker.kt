package com.PolGrauDev.reproductor_nativo_android.ui.screens.style.sticker

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.draw.rotate
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.automirrored.filled.Sort
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
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
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.StickerColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.StickerHardShadowBox
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.StickerType
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.stickerTilt
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicUiState
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicViewModel

private val TABS = listOf("Canciones", "Álbumes", "Artistas", "Carpetas", "Playlists")
private val TAB_COLORS = listOf(StickerColors.Pink, StickerColors.Butter, StickerColors.Mint, StickerColors.Grape, StickerColors.Blush)

@Composable
fun LibraryScreenSticker(
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

    Box(Modifier.fillMaxSize().background(StickerColors.Paper)) {
        Column(Modifier.fillMaxSize()) {
            StickerLibraryHeader(onSettingsClick = onSettingsClick, viewModel = viewModel, uiState = uiState)
            StickerSearchField(query = uiState.searchQuery, onQueryChange = viewModel::setSearchQuery)
            StickerTabsRow(selectedTab = selectedTab, onSelect = { selectedTab = it })

            when {
                uiState.isLoadingLibrary -> StickerCentered { CircularProgressIndicator(color = StickerColors.Ink) }
                uiState.songs.isEmpty() && selectedTab != 4 -> StickerCentered {
                    Text("No se encontraron canciones en el dispositivo", style = StickerType.HandwrittenSmall, color = StickerColors.Faded)
                }
                else -> when (selectedTab) {
                    0 -> StickerSongsTab(
                        uiState = uiState,
                        onClick = { song -> viewModel.playSong(song); onSongClick() },
                        onToggleFavorite = viewModel::toggleFavorite,
                        onAddToPlaylist = { song -> songForPlaylistDialog = song },
                    )
                    1 -> StickerAlbumsTab(uiState.albums, onAlbumClick)
                    2 -> StickerArtistsTab(uiState.artists, onArtistClick)
                    3 -> StickerFoldersTab(uiState.folders, onFolderClick)
                    else -> StickerPlaylistsTab(
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
            appStyle = AppStyle.STICKERS,
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
private fun StickerCentered(content: @Composable () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
}

@Composable
private fun StickerLibraryHeader(onSettingsClick: () -> Unit, viewModel: MusicViewModel, uiState: MusicUiState) {
    var sortMenuOpen by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp, 14.dp, 16.dp, 4.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                "♫ mi cuadernito de",
                style = StickerType.Handwritten.copy(fontSize = 15.sp),
                color = StickerColors.Faded,
                modifier = Modifier.rotate(-2f),
            )
            Text("Canciones", style = StickerType.HeadlineLarge, color = StickerColors.Ink)
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            StickerHardShadowBox(modifier = Modifier.size(38.dp), shape = RoundedCornerShape(14.dp)) {
                Box(Modifier.fillMaxSize().clickable { sortMenuOpen = true }, contentAlignment = Alignment.Center) {
                    Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Ordenar", tint = StickerColors.Ink, modifier = Modifier.size(19.dp))
                }
            }
            StickerHardShadowBox(modifier = Modifier.size(38.dp), shape = RoundedCornerShape(14.dp)) {
                Box(Modifier.fillMaxSize().clickable(onClick = onSettingsClick), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Settings, contentDescription = "Ajustes", tint = StickerColors.Ink, modifier = Modifier.size(19.dp))
                }
            }
        }
    }
    if (sortMenuOpen) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SortOrder.entries.forEach { order ->
                Box(
                    Modifier
                        .background(if (order == uiState.sortOrder) StickerColors.Butter else Color.White, RoundedCornerShape(12.dp))
                        .clickable { viewModel.setSortOrder(order); sortMenuOpen = false }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                ) { Text(order.label, style = StickerType.HandwrittenSmall, color = StickerColors.Ink) }
            }
        }
    }
}

@Composable
private fun StickerSearchField(query: String, onQueryChange: (String) -> Unit) {
    StickerHardShadowBox(
        modifier = Modifier.fillMaxWidth().padding(16.dp, 10.dp, 16.dp, 12.dp),
        shape = RoundedCornerShape(25.dp),
    ) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Search, contentDescription = null, tint = StickerColors.Pink)
            Spacer(Modifier.width(10.dp))
            Box(Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text("busca lo que te apetezca…", style = StickerType.HandwrittenSmall, color = StickerColors.Faded)
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = StickerType.HandwrittenSmall.copy(color = StickerColors.Ink),
                    cursorBrush = SolidColor(StickerColors.Ink),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (query.isNotEmpty()) {
                Icon(Icons.Filled.Close, contentDescription = "Limpiar búsqueda", tint = StickerColors.Faded, modifier = Modifier.clickable { onQueryChange("") })
            }
        }
    }
}

@Composable
private fun StickerTabsRow(selectedTab: Int, onSelect: (Int) -> Unit) {
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        TABS.forEachIndexed { index, title ->
            val selected = index == selectedTab
            Box(
                Modifier
                    .background(if (selected) TAB_COLORS[index] else Color.White, RoundedCornerShape(14.dp, 14.dp, 0.dp, 0.dp))
                    .clickable { onSelect(index) }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
            ) {
                Text(title, style = StickerType.TitleMedium.copy(fontSize = 13.sp), color = if (selected) Color.White else StickerColors.Faded)
            }
        }
    }
}

@Composable
private fun StickerSongsTab(uiState: MusicUiState, onClick: (Song) -> Unit, onToggleFavorite: (Long) -> Unit, onAddToPlaylist: (Song) -> Unit) {
    val songs = uiState.filteredSongs
    if (songs.isEmpty()) {
        StickerCentered { Text("Sin resultados", style = StickerType.HandwrittenSmall, color = StickerColors.Faded) }
        return
    }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
        itemsIndexed(songs, key = { _, song -> song.id }) { index, song ->
            StickerSongRow(
                song = song,
                isFavorite = song.id in uiState.favoriteSongIds,
                tilt = stickerTilt(index),
                onClick = { onClick(song) },
                onToggleFavorite = { onToggleFavorite(song.id) },
                onAddToPlaylist = { onAddToPlaylist(song) },
            )
        }
    }
}

@Composable
fun StickerSongRow(song: Song, isFavorite: Boolean, tilt: Float, onClick: () -> Unit, onToggleFavorite: () -> Unit, onAddToPlaylist: () -> Unit) {
    StickerHardShadowBox(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), rotationDegrees = tilt) {
        Row(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(9.dp, 9.dp, 12.dp, 9.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = AlbumArtRequest(song.contentUri),
                contentDescription = null,
                modifier = Modifier.size(50.dp).clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop,
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(song.title, style = StickerType.TitleMedium, color = StickerColors.Ink, maxLines = 1)
                Text(song.artist, style = StickerType.HandwrittenSmall, color = StickerColors.Faded, maxLines = 1)
            }
            Box(
                Modifier.size(34.dp).background(StickerColors.Blush, RoundedCornerShape(12.dp)).clickable(onClick = onToggleFavorite),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = if (isFavorite) "Quitar de favoritos" else "Añadir a favoritos",
                    tint = StickerColors.Pink,
                    modifier = Modifier.size(17.dp),
                )
            }
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier.size(34.dp).background(Color(0xFFEDE1FF), RoundedCornerShape(11.dp)).clickable(onClick = onAddToPlaylist),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Añadir a playlist", tint = StickerColors.Grape, modifier = Modifier.size(17.dp))
            }
        }
    }
}

@Composable
private fun StickerAlbumsTab(albums: List<AlbumGroup>, onClick: (AlbumGroup) -> Unit) {
    if (albums.isEmpty()) {
        StickerCentered { Text("Sin resultados", style = StickerType.HandwrittenSmall, color = StickerColors.Faded) }
        return
    }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
        itemsIndexed(albums, key = { _, it -> it.albumId ?: -1L }) { index, album ->
            StickerHardShadowBox(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), rotationDegrees = stickerTilt(index)) {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onClick(album) }.padding(9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AsyncImage(
                        model = AlbumArtRequest(album.songs.first().contentUri),
                        contentDescription = null,
                        modifier = Modifier.size(50.dp).clip(RoundedCornerShape(14.dp)),
                        contentScale = ContentScale.Crop,
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(album.title, style = StickerType.TitleMedium, color = StickerColors.Ink, maxLines = 1)
                        Text("${album.artist} · ${album.songs.size} canciones", style = StickerType.HandwrittenSmall, color = StickerColors.Faded, maxLines = 1)
                    }
                }
            }
        }
    }
}

@Composable
private fun StickerArtistsTab(artists: List<ArtistGroup>, onClick: (ArtistGroup) -> Unit) {
    if (artists.isEmpty()) {
        StickerCentered { Text("Sin resultados", style = StickerType.HandwrittenSmall, color = StickerColors.Faded) }
        return
    }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
        itemsIndexed(artists, key = { _, it -> it.artistId ?: -1L }) { index, artist ->
            StickerHardShadowBox(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), rotationDegrees = stickerTilt(index)) {
                Row(modifier = Modifier.fillMaxWidth().clickable { onClick(artist) }.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(artist.name, style = StickerType.TitleMedium, color = StickerColors.Ink, maxLines = 1)
                        Text("${artist.songs.size} canciones", style = StickerType.HandwrittenSmall, color = StickerColors.Faded, maxLines = 1)
                    }
                }
            }
        }
    }
}

@Composable
private fun StickerFoldersTab(folders: List<FolderGroup>, onClick: (FolderGroup) -> Unit) {
    if (folders.isEmpty()) {
        StickerCentered { Text("Sin resultados", style = StickerType.HandwrittenSmall, color = StickerColors.Faded) }
        return
    }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
        itemsIndexed(folders, key = { _, it -> it.path }) { index, folder ->
            StickerHardShadowBox(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), rotationDegrees = stickerTilt(index)) {
                Row(modifier = Modifier.fillMaxWidth().clickable { onClick(folder) }.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(folder.name, style = StickerType.TitleMedium, color = StickerColors.Ink, maxLines = 1)
                        Text("${folder.songs.size} canciones", style = StickerType.HandwrittenSmall, color = StickerColors.Faded, maxLines = 1)
                    }
                }
            }
        }
    }
}

@Composable
private fun StickerPlaylistsTab(
    playlists: List<PlaylistSummary>,
    favoriteCount: Int,
    onFavoritesClick: () -> Unit,
    onPlaylistClick: (Long) -> Unit,
    onCreatePlaylist: (String) -> Unit,
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
        item {
            StickerHardShadowBox(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), rotationDegrees = -0.8f) {
                Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onFavoritesClick).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Favorite, contentDescription = null, tint = StickerColors.Pink)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Favoritos", style = StickerType.TitleMedium, color = StickerColors.Ink)
                        Text("$favoriteCount canciones", style = StickerType.HandwrittenSmall, color = StickerColors.Faded)
                    }
                }
            }
        }
        itemsIndexed(playlists, key = { _, it -> it.id }) { index, playlist ->
            StickerHardShadowBox(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), rotationDegrees = stickerTilt(index)) {
                Row(modifier = Modifier.fillMaxWidth().clickable { onPlaylistClick(playlist.id) }.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(playlist.name, style = StickerType.TitleMedium, color = StickerColors.Ink, maxLines = 1)
                        Text("${playlist.songCount} canciones", style = StickerType.HandwrittenSmall, color = StickerColors.Faded)
                    }
                }
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { showCreateDialog = true }.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, tint = StickerColors.Ink)
                Spacer(Modifier.width(12.dp))
                Text("Nueva playlist", style = StickerType.HandwrittenSmall, color = StickerColors.Ink)
            }
        }
    }

    if (showCreateDialog) {
        StickerCreatePlaylistDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { onCreatePlaylist(it); showCreateDialog = false },
        )
    }
}

@Composable
private fun StickerCreatePlaylistDialog(onDismiss: () -> Unit, onCreate: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismiss) {
        StickerHardShadowBox(shape = RoundedCornerShape(26.dp), borderWidth = 4.dp, shadowOffsetX = 7.dp, shadowOffsetY = 7.dp) {
            Column(Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth().background(StickerColors.Grape, RoundedCornerShape(22.dp, 22.dp, 0.dp, 0.dp)).padding(16.dp, 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, tint = StickerColors.Ink)
                    Spacer(Modifier.width(9.dp))
                    Text("Nueva playlist", style = StickerType.TitleMedium.let { it.copy(fontSize = 20.sp) }, color = StickerColors.Ink)
                }
                Column(Modifier.padding(16.dp)) {
                    Box(Modifier.background(Color.White, RoundedCornerShape(16.dp)).fillMaxWidth().padding(12.dp, 10.dp)) {
                        BasicTextField(
                            value = name,
                            onValueChange = { name = it },
                            singleLine = true,
                            textStyle = StickerType.HandwrittenSmall.copy(color = StickerColors.Ink),
                            cursorBrush = SolidColor(StickerColors.Ink),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        StickerHardShadowBox(shape = RoundedCornerShape(15.dp)) {
                            Text(
                                "cancelar",
                                style = StickerType.HandwrittenSmall,
                                color = StickerColors.Faded,
                                modifier = Modifier.clickable(onClick = onDismiss).padding(13.dp, 8.dp),
                            )
                        }
                        Spacer(Modifier.width(9.dp))
                        StickerHardShadowBox(shape = RoundedCornerShape(15.dp), backgroundColor = StickerColors.Pink) {
                            Text(
                                "crear",
                                style = StickerType.TitleMedium.let { it.copy(fontSize = 15.sp) },
                                color = Color.White,
                                modifier = Modifier
                                    .clickable(enabled = name.isNotBlank()) { onCreate(name) }
                                    .padding(13.dp, 8.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

