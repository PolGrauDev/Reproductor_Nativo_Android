package com.PolGrauDev.reproductor_nativo_android.player

import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.PolGrauDev.reproductor_nativo_android.R

/**
 * Servicio en primer plano que mantiene [ExoPlayer] y la [MediaSession] vivos
 * independientemente del ciclo de vida de la UI (sobrevive con la pantalla apagada).
 *
 * Nota: no hace falta sobrescribir onTaskRemoved() para pausar/detener el servicio al
 * cerrar la app desde recientes — MediaSessionService ya lo hace por defecto desde 1.x
 * (pauseAllPlayersAndStopSelf() si no hay ninguna sesión reproduciendo activamente).
 */
class PlaybackService : MediaSessionService() {

    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()

        val player = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, player).build()

        val notificationProvider = DefaultMediaNotificationProvider.Builder(this).build()
        notificationProvider.setSmallIcon(R.drawable.ic_notification)
        setMediaNotificationProvider(notificationProvider)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    override fun onDestroy() {
        mediaSession?.let { session ->
            session.player.release()
            session.release()
        }
        mediaSession = null
        super.onDestroy()
    }
}
