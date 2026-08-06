package com.PolGrauDev.reproductor_nativo_android.player

/** Milisegundos restantes hasta que el sleep timer deba pausar la reproducción, nunca negativo. */
fun sleepTimerRemainingMs(endAtElapsedRealtime: Long, nowElapsedRealtime: Long): Long =
    (endAtElapsedRealtime - nowElapsedRealtime).coerceAtLeast(0L)

/**
 * Retraso hasta que debe empezar el fundido de salida, para que termine justo al llegar al
 * final de la pista. `null` si la duración aún no se conoce o el fundido está desactivado;
 * `0` si ya estamos dentro de la ventana de fundido (debe empezar de inmediato).
 */
fun fadeOutDelayMs(durationMs: Long, positionMs: Long, fadeDurationMs: Int): Long? {
    if (durationMs <= 0L || fadeDurationMs <= 0) return null
    val fadeStartMs = durationMs - fadeDurationMs
    return (fadeStartMs - positionMs).coerceAtLeast(0L)
}
