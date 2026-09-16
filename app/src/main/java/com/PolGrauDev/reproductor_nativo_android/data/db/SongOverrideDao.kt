package com.PolGrauDev.reproductor_nativo_android.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SongOverrideDao {

    @Query("SELECT * FROM song_overrides")
    fun observeAll(): Flow<List<SongOverrideEntity>>

    @Query("SELECT * FROM song_overrides WHERE songId = :songId")
    suspend fun get(songId: Long): SongOverrideEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SongOverrideEntity)
}
