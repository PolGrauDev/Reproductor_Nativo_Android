package com.PolGrauDev.reproductor_nativo_android

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import com.PolGrauDev.reproductor_nativo_android.data.AlbumArtFetcher
import com.PolGrauDev.reproductor_nativo_android.data.AlbumArtKeyer
import com.PolGrauDev.reproductor_nativo_android.data.MediaRepository
import com.PolGrauDev.reproductor_nativo_android.data.PlaylistRepository
import com.PolGrauDev.reproductor_nativo_android.data.SettingsRepository
import com.PolGrauDev.reproductor_nativo_android.data.SongOverrideRepository
import com.PolGrauDev.reproductor_nativo_android.data.db.AppDatabase
import okio.Path.Companion.toOkioPath

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS song_overrides (
                songId INTEGER NOT NULL PRIMARY KEY,
                title TEXT,
                artist TEXT,
                album TEXT,
                customArtUpdatedAt INTEGER
            )
            """.trimIndent(),
        )
    }
}

class App : Application(), SingletonImageLoader.Factory {

    val mediaRepository: MediaRepository by lazy { MediaRepository(applicationContext) }

    private val database: AppDatabase by lazy {
        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "reproductor.db")
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    val playlistRepository: PlaylistRepository by lazy {
        PlaylistRepository(database.favoriteDao(), database.playlistDao())
    }

    val songOverrideRepository: SongOverrideRepository by lazy {
        SongOverrideRepository(database.songOverrideDao())
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
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("album_art_cache").toOkioPath())
                    .maxSizeBytes(50L * 1024 * 1024)
                    .build()
            }
            .build()
}
