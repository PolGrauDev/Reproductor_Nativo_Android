package com.PolGrauDev.reproductor_nativo_android.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.AppStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

private const val DEFAULT_SLEEP_TIMER_MINUTES = 30

/**
 * Envuelve DataStore Preferences para que el resto de la app no dependa de él directamente,
 * igual que [PlaylistRepository] hace con Room. Recibe el [DataStore] ya construido (en vez de
 * resolverlo internamente desde el [Context] singleton `Context.settingsDataStore`) para que los
 * tests puedan inyectar una instancia aislada por test en vez de compartir el fichero real de la
 * app.
 */
class SettingsRepository(private val dataStore: DataStore<Preferences>) {

    constructor(context: Context) : this(context.settingsDataStore)

    private object Keys {
        val FADE_DURATION_MS = intPreferencesKey("fade_duration_ms")
        val SLEEP_TIMER_DEFAULT_MINUTES = intPreferencesKey("sleep_timer_default_minutes")
        val APP_STYLE = stringPreferencesKey("app_style")
    }

    /** 0 = fundido desactivado. */
    val fadeDurationMs: Flow<Int> =
        dataStore.data.map { it[Keys.FADE_DURATION_MS] ?: 0 }

    /** Última duración elegida en el picker del sleep timer, no el estado runtime del temporizador. */
    val sleepTimerDefaultMinutes: Flow<Int> =
        dataStore.data.map { it[Keys.SLEEP_TIMER_DEFAULT_MINUTES] ?: DEFAULT_SLEEP_TIMER_MINUTES }

    /** Estilo visual elegido en Ajustes. Cae a [AppStyle.PAPEL] si no hay valor o es inválido. */
    val appStyle: Flow<AppStyle> =
        dataStore.data.map { prefs ->
            prefs[Keys.APP_STYLE]?.let { name ->
                runCatching { AppStyle.valueOf(name) }.getOrNull()
            } ?: AppStyle.PAPEL
        }

    suspend fun setFadeDurationMs(ms: Int) {
        dataStore.edit { it[Keys.FADE_DURATION_MS] = ms }
    }

    suspend fun setSleepTimerDefaultMinutes(minutes: Int) {
        dataStore.edit { it[Keys.SLEEP_TIMER_DEFAULT_MINUTES] = minutes }
    }

    suspend fun setAppStyle(style: AppStyle) {
        dataStore.edit { it[Keys.APP_STYLE] = style.name }
    }
}
