package com.PolGrauDev.reproductor_nativo_android.player

import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
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
        player.addListener(object : Player.Listener {
            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                mediaSession?.setMediaButtonPreferences(buildMediaButtonPreferences(player))
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                mediaSession?.setMediaButtonPreferences(buildMediaButtonPreferences(player))
            }
        })
        mediaSession = MediaSession.Builder(this, player)
            .setMediaButtonPreferences(buildMediaButtonPreferences(player))
            .build()

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

    /**
     * Botones de shuffle/repeat para la notificación/lock-screen, en [CommandButton.SLOT_OVERFLOW]
     * (la vista compacta ya la ocupan previous/play-pause/next). Se reconstruyen en cada cambio de
     * estado porque tanto el icono como el `parameter` de repeat (el "siguiente modo" del ciclo)
     * dependen del estado actual del [Player].
     */
    private fun buildMediaButtonPreferences(player: Player): List<CommandButton> {
        val shuffleIcon = if (player.shuffleModeEnabled) {
            CommandButton.ICON_SHUFFLE_ON
        } else {
            CommandButton.ICON_SHUFFLE_OFF
        }
        val shuffleButton = CommandButton.Builder(shuffleIcon)
            .setPlayerCommand(Player.COMMAND_SET_SHUFFLE_MODE)
            .setSlots(CommandButton.SLOT_OVERFLOW)
            .setDisplayName("Aleatorio")
            .build()

        val repeatIcon = when (player.repeatMode) {
            Player.REPEAT_MODE_ALL -> CommandButton.ICON_REPEAT_ALL
            Player.REPEAT_MODE_ONE -> CommandButton.ICON_REPEAT_ONE
            else -> CommandButton.ICON_REPEAT_OFF
        }
        val repeatButton = CommandButton.Builder(repeatIcon)
            .setPlayerCommand(Player.COMMAND_SET_REPEAT_MODE, nextRepeatMode(player.repeatMode))
            .setSlots(CommandButton.SLOT_OVERFLOW)
            .setDisplayName("Repetir")
            .build()

        return listOf(shuffleButton, repeatButton)
    }

    /** Mismo orden de ciclo que [PlaybackConnection.cycleRepeatMode]: sin repetir -> todo -> una -> sin repetir. */
    private fun nextRepeatMode(current: Int): Int = when (current) {
        Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
        Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
        else -> Player.REPEAT_MODE_OFF
    }
}
