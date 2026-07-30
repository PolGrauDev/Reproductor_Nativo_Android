package com.PolGrauDev.reproductor_nativo_android

import android.app.Application
import android.content.Context
import coil3.ImageLoader
import coil3.SingletonImageLoader
import com.PolGrauDev.reproductor_nativo_android.data.AlbumArtFetcher
import com.PolGrauDev.reproductor_nativo_android.data.AlbumArtKeyer
import com.PolGrauDev.reproductor_nativo_android.data.MediaRepository

class App : Application(), SingletonImageLoader.Factory {

    val mediaRepository: MediaRepository by lazy { MediaRepository(applicationContext) }

    override fun onCreate() {
        super.onCreate()
        SingletonImageLoader.setSafe { context -> newImageLoader(context) }
    }

    override fun newImageLoader(context: Context): ImageLoader =
        ImageLoader.Builder(context)
            .components {
                add(AlbumArtFetcher.Factory())
                add(AlbumArtKeyer())
            }
            .build()
}
