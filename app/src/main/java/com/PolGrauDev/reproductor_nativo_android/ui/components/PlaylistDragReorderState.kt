package com.PolGrauDev.reproductor_nativo_android.ui.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import com.PolGrauDev.reproductor_nativo_android.data.model.Song
import sh.calvin.reorderable.ReorderableLazyListState
import sh.calvin.reorderable.rememberReorderableLazyListState

/**
 * Backs the free long-press drag reorder gesture in a playlist's song list, as a complement to
 * the existing up/down move buttons (not a replacement). `songs` is re-ordered locally/instantly
 * for live drag feedback — persisting through [PlaylistRepository.moveSong] on every intermediate
 * swap would mean waiting on a Room round trip per swap, which the underlying reorder library
 * doesn't tolerate well (it awaits the list actually re-laying-out after each `onMove`). The real
 * repository write happens exactly once, when the drag ends.
 */
@Stable
class PlaylistDragReorderState internal constructor(
    initialSongs: List<Song>,
    val lazyListState: LazyListState,
    private val onCommit: (fromIndex: Int, toIndex: Int) -> Unit,
) {
    var songs by mutableStateOf(initialSongs)
        private set

    lateinit var reorderableState: ReorderableLazyListState
        private set

    private var draggedSongId: Long? = null
    private var originalIndex: Int = -1

    internal fun attachReorderableState(state: ReorderableLazyListState) {
        reorderableState = state
    }

    internal fun syncIfIdle(latest: List<Song>) {
        if (draggedSongId == null) songs = latest
    }

    internal fun onMove(fromIndex: Int, toIndex: Int) {
        songs = songs.toMutableList().apply { add(toIndex, removeAt(fromIndex)) }
    }

    fun onDragStarted(song: Song) {
        draggedSongId = song.id
        originalIndex = songs.indexOf(song)
    }

    fun onDragStopped() {
        val songId = draggedSongId ?: return
        draggedSongId = null
        val finalIndex = songs.indexOfFirst { it.id == songId }
        if (originalIndex != -1 && finalIndex != -1 && originalIndex != finalIndex) {
            onCommit(originalIndex, finalIndex)
        }
        originalIndex = -1
    }
}

@Composable
fun rememberPlaylistDragReorderState(
    songs: List<Song>,
    onCommit: (fromIndex: Int, toIndex: Int) -> Unit,
): PlaylistDragReorderState {
    val onCommitState = rememberUpdatedState(onCommit)
    val lazyListState = rememberLazyListState()
    val dragState = remember {
        PlaylistDragReorderState(songs, lazyListState) { from, to -> onCommitState.value(from, to) }
    }
    LaunchedEffect(songs) { dragState.syncIfIdle(songs) }

    val reorderableState = rememberReorderableLazyListState(lazyListState) { from, to ->
        dragState.onMove(from.index, to.index)
    }
    dragState.attachReorderableState(reorderableState)

    return dragState
}
