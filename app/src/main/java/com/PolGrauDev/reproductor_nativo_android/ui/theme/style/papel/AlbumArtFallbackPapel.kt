package com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Fallback shown by `SubcomposeAsyncImage` while loading and when a song has no embedded art.
 * Defaults to filling the space already sized/clipped by the caller's `SubcomposeAsyncImage`
 * modifier, so call sites don't need to repeat their size/shape here.
 */
@Composable
fun AlbumArtFallbackPapel(modifier: Modifier = Modifier.fillMaxSize()) {
    Box(modifier.background(PapelColors.SurfaceVariant), contentAlignment = Alignment.Center) {
        Icon(Icons.Filled.MusicNote, contentDescription = null, tint = PapelColors.Faint, modifier = Modifier.size(20.dp))
    }
}
