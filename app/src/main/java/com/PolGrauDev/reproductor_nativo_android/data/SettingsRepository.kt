package com.PolGrauDev.reproductor_nativo_android.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

private const val DEFAULT_SLEEP_TIMER_MINUTES = 30

/**
 * Envuelve DataStore Preferences para que el resto de la app no dependa de él directamente,
 * igual que [PlaylistRepository] hace con Room.
 */
class SettingsRepository(private val context: Context) {

    private object Keys {
        val FADE_DURATION_MS = intPreferencesKey("fade_duration_ms")
        val SLEEP_TIMER_DEFAULT_MINUTES = intPreferencesKey("sleep_timer_default_minutes")
    }

    /** 0 = fundido desactivado. */
    val fadeDurationMs: Flow<Int> =
        context.settingsDataStore.data.map { it[Keys.FADE_DURATION_MS] ?: 0 }

    /** Última duración elegida en el picker del sleep timer, no el estado runtime del temporizador. */
    val sleepTimerDefaultMinutes: Flow<Int> =
        context.settingsDataStore.data.map { it[Keys.SLEEP_TIMER_DEFAULT_MINUTES] ?: DEFAULT_SLEEP_TIMER_MINUTES }

    suspend fun setFadeDurationMs(ms: Int) {
        context.settingsDataStore.edit { it[Keys.FADE_DURATION_MS] = ms }
    }

    suspend fun setSleepTimerDefaultMinutes(minutes: Int) {
        context.settingsDataStore.edit { it[Keys.SLEEP_TIMER_DEFAULT_MINUTES] = minutes }
    }
}
