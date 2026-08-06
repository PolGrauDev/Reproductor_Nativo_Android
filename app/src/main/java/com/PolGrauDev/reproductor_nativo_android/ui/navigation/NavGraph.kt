package com.PolGrauDev.reproductor_nativo_android.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.PolGrauDev.reproductor_nativo_android.ui.screens.AlbumDetailScreen
import com.PolGrauDev.reproductor_nativo_android.ui.screens.ArtistDetailScreen
import com.PolGrauDev.reproductor_nativo_android.ui.screens.FavoritesScreen
import com.PolGrauDev.reproductor_nativo_android.ui.screens.FolderDetailScreen
import com.PolGrauDev.reproductor_nativo_android.ui.screens.LibraryScreen
import com.PolGrauDev.reproductor_nativo_android.ui.screens.NowPlayingScreen
import com.PolGrauDev.reproductor_nativo_android.ui.screens.PlaylistDetailScreen
import com.PolGrauDev.reproductor_nativo_android.ui.screens.QueueScreen
import com.PolGrauDev.reproductor_nativo_android.ui.screens.SettingsScreen
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicViewModel

private const val NO_ID = -1L

object Routes {
    const val SONG_LIST = "songList"
    const val NOW_PLAYING = "nowPlaying"
    const val QUEUE = "queue"
    const val ALBUM_DETAIL = "albumDetail/{albumId}"
    const val ARTIST_DETAIL = "artistDetail/{artistId}"
    const val FOLDER_DETAIL = "folderDetail/{folderPath}"
    const val FAVORITES = "favorites"
    const val PLAYLIST_DETAIL = "playlistDetail/{playlistId}"
    const val SETTINGS = "settings"

    fun albumDetail(albumId: Long?) = "albumDetail/${albumId ?: NO_ID}"
    fun artistDetail(artistId: Long?) = "artistDetail/${artistId ?: NO_ID}"
    fun folderDetail(path: String) = "folderDetail/${Uri.encode(path)}"
    fun playlistDetail(playlistId: Long) = "playlistDetail/$playlistId"
}

@Composable
fun NavGraph(
    viewModel: MusicViewModel,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(navController = navController, startDestination = Routes.SONG_LIST) {
        composable(Routes.SONG_LIST) {
            LibraryScreen(
                viewModel = viewModel,
                onSongClick = { navController.navigate(Routes.NOW_PLAYING) },
                onAlbumClick = { album -> navController.navigate(Routes.albumDetail(album.albumId)) },
                onArtistClick = { artist -> navController.navigate(Routes.artistDetail(artist.artistId)) },
                onFolderClick = { folder -> navController.navigate(Routes.folderDetail(folder.path)) },
                onFavoritesClick = { navController.navigate(Routes.FAVORITES) },
                onPlaylistClick = { playlistId -> navController.navigate(Routes.playlistDetail(playlistId)) },
                onSettingsClick = { navController.navigate(Routes.SETTINGS) },
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
            )
        }
        composable(Routes.NOW_PLAYING) {
            NowPlayingScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onQueueClick = { navController.navigate(Routes.QUEUE) },
            )
        }
        composable(Routes.QUEUE) {
            QueueScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
            )
        }
        composable(
            route = Routes.ALBUM_DETAIL,
            arguments = listOf(navArgument("albumId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val albumId = backStackEntry.arguments?.getLong("albumId") ?: NO_ID
            AlbumDetailScreen(
                viewModel = viewModel,
                albumId = albumId.takeIf { it != NO_ID },
                onBack = { navController.popBackStack() },
                onSongClick = { navController.navigate(Routes.NOW_PLAYING) },
            )
        }
        composable(
            route = Routes.ARTIST_DETAIL,
            arguments = listOf(navArgument("artistId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val artistId = backStackEntry.arguments?.getLong("artistId") ?: NO_ID
            ArtistDetailScreen(
                viewModel = viewModel,
                artistId = artistId.takeIf { it != NO_ID },
                onBack = { navController.popBackStack() },
                onSongClick = { navController.navigate(Routes.NOW_PLAYING) },
            )
        }
        composable(
            route = Routes.FOLDER_DETAIL,
            arguments = listOf(navArgument("folderPath") { type = NavType.StringType }),
        ) { backStackEntry ->
            val folderPath = Uri.decode(backStackEntry.arguments?.getString("folderPath") ?: "")
            FolderDetailScreen(
                viewModel = viewModel,
                folderPath = folderPath,
                onBack = { navController.popBackStack() },
                onSongClick = { navController.navigate(Routes.NOW_PLAYING) },
            )
        }
        composable(Routes.FAVORITES) {
            FavoritesScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSongClick = { navController.navigate(Routes.NOW_PLAYING) },
            )
        }
        composable(
            route = Routes.PLAYLIST_DETAIL,
            arguments = listOf(navArgument("playlistId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getLong("playlistId") ?: NO_ID
            PlaylistDetailScreen(
                viewModel = viewModel,
                playlistId = playlistId,
                onBack = { navController.popBackStack() },
                onSongClick = { navController.navigate(Routes.NOW_PLAYING) },
                onPlaylistDeleted = { navController.popBackStack() },
            )
        }
    }
}
