package com.PolGrauDev.reproductor_nativo_android

import android.app.Application
import android.content.Context
import androidx.room.Room
import coil3.ImageLoader
import coil3.SingletonImageLoader
import com.PolGrauDev.reproductor_nativo_android.data.AlbumArtFetcher
import com.PolGrauDev.reproductor_nativo_android.data.AlbumArtKeyer
import com.PolGrauDev.reproductor_nativo_android.data.MediaRepository
import com.PolGrauDev.reproductor_nativo_android.data.PlaylistRepository
import com.PolGrauDev.reproductor_nativo_android.data.SettingsRepository
import com.PolGrauDev.reproductor_nativo_android.data.db.AppDatabase

class App : Application(), SingletonImageLoader.Factory {

    val mediaRepository: MediaRepository by lazy { MediaRepository(applicationContext) }

    private val database: AppDatabase by lazy {
        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "reproductor.db").build()
    }

    val playlistRepository: PlaylistRepository by lazy {
        PlaylistRepository(database.favoriteDao(), database.playlistDao())
    }

    val settingsRepository: SettingsRepository by lazy { SettingsRepository(applicationContext) }

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
