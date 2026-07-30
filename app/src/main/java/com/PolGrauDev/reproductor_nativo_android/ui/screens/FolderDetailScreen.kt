package com.PolGrauDev.reproductor_nativo_android.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.PolGrauDev.reproductor_nativo_android.data.model.Song
import com.PolGrauDev.reproductor_nativo_android.ui.components.AddToPlaylistDialog
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderDetailScreen(
    viewModel: MusicViewModel,
    folderPath: String,
    onBack: () -> Unit,
    onSongClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val folder = uiState.folders.firstOrNull { it.path == folderPath }
    var songForPlaylistDialog by remember { mutableStateOf<Song?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(folder?.name ?: "Carpeta") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
            )
        },
    ) { padding ->
        if (folder == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text("Carpeta no encontrada")
            }
            return@Scaffold
        }

        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Text(folder.name, style = MaterialTheme.typography.headlineSmall, maxLines = 2)
                    Text(
                        "${folder.songs.size} canciones",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            items(folder.songs, key = { it.id }) { song ->
                AlbumSongRow(
                    song = song,
                    isPlaying = uiState.currentSong?.id == song.id && uiState.playback.isPlaying,
                    isFavorite = song.id in uiState.favoriteSongIds,
                    onClick = {
                        viewModel.playSong(song, fromList = folder.songs)
                        onSongClick()
                    },
                    onToggleFavorite = { viewModel.toggleFavorite(song.id) },
                    onAddToPlaylist = { songForPlaylistDialog = song },
                )
            }
        }
    }

    songForPlaylistDialog?.let { song ->
        AddToPlaylistDialog(
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
