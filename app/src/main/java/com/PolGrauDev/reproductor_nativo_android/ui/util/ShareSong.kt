package com.PolGrauDev.reproductor_nativo_android.ui.util

import android.content.Context
import android.content.Intent
import com.PolGrauDev.reproductor_nativo_android.data.model.Song

fun shareSong(context: Context, song: Song) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "audio/*"
        putExtra(Intent.EXTRA_STREAM, song.contentUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, null))
}
