package com.PolGrauDev.reproductor_nativo_android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.PolGrauDev.reproductor_nativo_android.ui.screens.NowPlayingScreen
import com.PolGrauDev.reproductor_nativo_android.ui.screens.SongListScreen
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicViewModel

object Routes {
    const val SONG_LIST = "songList"
    const val NOW_PLAYING = "nowPlaying"
}

@Composable
fun NavGraph(
    viewModel: MusicViewModel,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(navController = navController, startDestination = Routes.SONG_LIST) {
        composable(Routes.SONG_LIST) {
            SongListScreen(
                viewModel = viewModel,
                onSongClick = { navController.navigate(Routes.NOW_PLAYING) },
            )
        }
        composable(Routes.NOW_PLAYING) {
            NowPlayingScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
