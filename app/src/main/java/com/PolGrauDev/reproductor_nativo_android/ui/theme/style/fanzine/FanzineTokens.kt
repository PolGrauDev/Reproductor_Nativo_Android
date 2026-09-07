package com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PolGrauDev.reproductor_nativo_android.R

/**
 * "Fanzine fotocopiado" — recortes de papel pegados con cinta, letras de nota de rescate, tinta
 * roja, máquina de escribir. Source: design canvas option `3a` (Library/Now Playing) + `4c`
 * (Album/Queue/Favorites/Ajustes/playlist dialog). Fully outside Material 3: zero corner radius,
 * zero elevation, zero `Card` — every "cutout" is a plain rotated background box, see
 * [FanzineCutout] and [Modifier.photocopyGrain].
 */
object FanzineColors {
    val Slate = Color(0xFF332F2A) // fondo
    val Paper = Color(0xFFE8E4DA) // recorte
    val Red = Color(0xFFFF1E1E)
    val Grime = Color(0xFF4A443D)
    val Faded = Color(0xFFB4ADA1)
    val Hair = Color(0xFF615A51) // borde 2dp
    val Ink = Color(0xFF332F2A) // texto sobre recortes de papel
}

object FanzineFonts {
    /** Only weight 400 exists for Anton — display/title text is always MAYÚSCULAS. */
    val Anton = FontFamily(Font(R.font.anton_regular, FontWeight.Normal))

    /** Typewriter body/labels, +0.14em tracking per the source tokens. */
    val SpecialElite = FontFamily(Font(R.font.special_elite_regular, FontWeight.Normal))
}

/** Rotation alternates sign per index so no cutout row reads perfectly straight. */
fun fanzineTilt(index: Int): Float {
    val magnitude = 1f + (index % 3) * 0.7f
    return if (index % 2 == 0) -magnitude else magnitude
}

/**
 * A "cutout": a plain background box (no radius, no border, no elevation) rotated a couple of
 * degrees, as if scissored from a photocopy and glued down.
 */
@Composable
fun FanzineCutout(
    modifier: Modifier = Modifier,
    backgroundColor: Color = FanzineColors.Paper,
    rotationDegrees: Float = 0f,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .rotate(rotationDegrees)
            .background(backgroundColor),
    ) { content() }
}

/** Faint photocopy-grain hairlines drawn directly, never a bitmap asset. */
fun Modifier.photocopyGrain(alpha: Float = 0.03f, stepDp: Float = 4f): Modifier = drawBehind {
    val step = stepDp * density
    var x = 0f
    while (x < size.width) {
        drawLine(
            color = Color.White.copy(alpha = alpha),
            start = Offset(x, 0f),
            end = Offset(x, size.height),
            strokeWidth = 1f,
        )
        x += step
    }
}

data class RansomWord(
    val text: String,
    val background: Color,
    val contentColor: Color,
    val fontSize: TextUnit = 28.sp,
    val rotationDegrees: Float = 0f,
    val bordered: Boolean = false,
)

/**
 * A ransom-note title: individual [androidx.compose.material3.Text]s in a [FlowRow], each with
 * its own background/size/rotation — never a single styled `Text`. Vary [RansomWord]s per song so
 * the rhythm never repeats.
 */
@Composable
fun FanzineRansomTitle(words: List<RansomWord>, modifier: Modifier = Modifier) {
    FlowRow(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        words.forEach { word ->
            val decoratedModifier = Modifier
                .rotate(word.rotationDegrees)
                .let {
                    if (word.bordered) it.border(width = 2.dp, color = word.contentColor) else it.background(word.background)
                }
                .padding(horizontal = 4.dp, vertical = 1.dp)
            Text(
                text = word.text.uppercase(),
                color = word.contentColor,
                fontFamily = FanzineFonts.Anton,
                fontSize = word.fontSize,
                textAlign = TextAlign.Center,
                lineHeight = word.fontSize,
                modifier = decoratedModifier,
            )
        }
    }
}
