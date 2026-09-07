@file:OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)

package com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PolGrauDev.reproductor_nativo_android.R

/**
 * "Cuaderno de pegatinas" — papel crema, contorno de tinta 3px, pegatinas torcidas, botones de
 * juguete. Source: design canvas option `2a` (Library/Now Playing) + `4b` (Album/Queue/
 * Favorites/Ajustes/playlist dialog). Fully outside Material 3: no `Card`/elevation — every
 * "sticker" is a hand-drawn border box with a hard offset shadow, see [StickerHardShadowBox].
 */
object StickerColors {
    val Ink = Color(0xFF4A2C3C) // contorno 3dp
    val Pink = Color(0xFFFF6FA5)
    val Butter = Color(0xFFFFD84D)
    val Mint = Color(0xFF7BD9C0)
    val Grape = Color(0xFFC7A6F5)
    val Paper = Color(0xFFFFF8EE) // fondo con textura de puntitos
    val Blush = Color(0xFFFFE3EE) // fondo now-playing
    val Faded = Color(0xFFB9927F)
}

object StickerFonts {
    val Fredoka = FontFamily(
        Font(
            R.font.fredoka_variable,
            FontWeight.Medium,
            variationSettings = FontVariation.Settings(FontVariation.weight(500), FontVariation.width(100f)),
        ),
        Font(
            R.font.fredoka_variable,
            FontWeight.SemiBold,
            variationSettings = FontVariation.Settings(FontVariation.weight(600), FontVariation.width(100f)),
        ),
        Font(
            R.font.fredoka_variable,
            FontWeight.Bold,
            variationSettings = FontVariation.Settings(FontVariation.weight(700), FontVariation.width(100f)),
        ),
    )

    /** Only weight 700 is used anywhere in the source mockups. */
    val Gaegu = FontFamily(Font(R.font.gaegu_bold, FontWeight.Bold))

    val Quicksand = FontFamily(
        Font(
            R.font.quicksand_variable,
            FontWeight.Medium,
            variationSettings = FontVariation.Settings(FontVariation.weight(500)),
        ),
        Font(
            R.font.quicksand_variable,
            FontWeight.SemiBold,
            variationSettings = FontVariation.Settings(FontVariation.weight(600)),
        ),
        Font(
            R.font.quicksand_variable,
            FontWeight.Bold,
            variationSettings = FontVariation.Settings(FontVariation.weight(700)),
        ),
    )
}

object StickerType {
    val HeadlineLarge = TextStyle(fontFamily = StickerFonts.Fredoka, fontWeight = FontWeight.Bold, fontSize = 36.sp)
    val TitleMedium = TextStyle(fontFamily = StickerFonts.Fredoka, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
    val Handwritten = TextStyle(fontFamily = StickerFonts.Gaegu, fontWeight = FontWeight.Bold, fontSize = 19.sp)
    val HandwrittenSmall = TextStyle(fontFamily = StickerFonts.Gaegu, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    val LabelLarge = TextStyle(fontFamily = StickerFonts.Gaegu, fontWeight = FontWeight.Bold, fontSize = 17.sp)
}

/** Alternating tilt for stacked stickers, per index — "nunca perfectamente recto". */
fun stickerTilt(index: Int): Float {
    val magnitude = 0.9f + (index % 3) * 0.25f
    return if (index % 2 == 0) -magnitude else magnitude
}

/**
 * The one shared building block behind every sticker/chip/button in this style: a hand-inked
 * border box with a second, same-shaped box of solid ink offset behind it as a hard drop shadow —
 * never a real `elevation`. Callers must leave room around the composable (the shadow extends
 * past the visible box) since this does not clip to bounds.
 */
@Composable
fun StickerHardShadowBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color = Color.White,
    borderColor: Color = StickerColors.Ink,
    borderWidth: Dp = 3.dp,
    shadowColor: Color = StickerColors.Ink,
    shadowOffsetX: Dp = 4.dp,
    shadowOffsetY: Dp = 4.dp,
    rotationDegrees: Float = 0f,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier = modifier.rotate(rotationDegrees)) {
        Box(
            Modifier
                .matchParentSize()
                .offset(x = shadowOffsetX, y = shadowOffsetY)
                .background(shadowColor, shape),
        )
        Box(
            Modifier
                .background(backgroundColor, shape)
                .border(borderWidth, borderColor, shape)
                .padding(1.dp),
            content = content,
        )
    }
}
