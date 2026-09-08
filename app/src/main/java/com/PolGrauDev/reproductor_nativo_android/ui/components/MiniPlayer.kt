package com.PolGrauDev.reproductor_nativo_android.ui.components

import androidx.compose.runtime.Composable
import com.PolGrauDev.reproductor_nativo_android.data.model.Song
import com.PolGrauDev.reproductor_nativo_android.ui.components.style.fanzine.MiniPlayerFanzine
import com.PolGrauDev.reproductor_nativo_android.ui.components.style.papel.MiniPlayerPapel
import com.PolGrauDev.reproductor_nativo_android.ui.components.style.sticker.MiniPlayerSticker
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.AppStyle

/**
 * Thin dispatcher over the three bespoke visual styles — see `ui/components/style/<style>/
 * MiniPlayerX.kt` for the actual per-style implementations. Hosted as `NavGraph`'s `bottomBar`
 * so it persists while browsing Library/detail/Favorites/Playlist/Queue screens, hidden on
 * `Routes.NOW_PLAYING` itself and whenever nothing is loaded (see `NavGraph.kt`).
 */
@Composable
fun MiniPlayer(
    appStyle: AppStyle,
    song: Song,
    isPlaying: Boolean,
    onTogglePlayPause: () -> Unit,
    onClick: () -> Unit,
) {
    when (appStyle) {
        AppStyle.PAPEL -> MiniPlayerPapel(song, isPlaying, onTogglePlayPause, onClick)
        AppStyle.STICKERS -> MiniPlayerSticker(song, isPlaying, onTogglePlayPause, onClick)
        AppStyle.FANZINE -> MiniPlayerFanzine(song, isPlaying, onTogglePlayPause, onClick)
    }
}
