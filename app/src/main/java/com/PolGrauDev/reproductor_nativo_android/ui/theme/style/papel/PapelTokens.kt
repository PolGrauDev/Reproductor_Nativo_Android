@file:OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)

package com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PolGrauDev.reproductor_nativo_android.R

/**
 * "Papel" — neutro elegante: hueso cálido, serif editorial, bronce, aire. Source: design canvas
 * option `1c` (Library/Now Playing) + `4a` (Album/Queue/Favorites/Ajustes/playlist dialog).
 * No cards or elevation anywhere — hierarchy comes from 1px dividers and generous spacing.
 */
object PapelColors {
    val Primary = Color(0xFF1E1B17)
    val OnPrimary = Color(0xFFFAF8F4)
    val Accent = Color(0xFFA08D6E) // bronce
    val Background = Color(0xFFFAF8F4)
    val Surface = Color(0xFFFFFFFF)
    val SurfaceVariant = Color(0xFFEFEAE0)
    val RowDivider = Color(0xFFEFEAE0)
    val SectionDivider = Color(0xFFE7E1D6)
    val Outline = Color(0xFFDCD5C8)
    val OnSurface = Color(0xFF1E1B17)
    val OnSurfaceVariant = Color(0xFF8C857B)
    val Faint = Color(0xFFC4BBA8)
}

object PapelFonts {
    val Serif = FontFamily(
        Font(R.font.instrument_serif_regular, FontWeight.Normal, FontStyle.Normal),
        Font(R.font.instrument_serif_italic, FontWeight.Normal, FontStyle.Italic),
    )

    val Karla = FontFamily(
        Font(
            R.font.karla_variable,
            FontWeight.Normal,
            variationSettings = FontVariation.Settings(FontVariation.weight(400)),
        ),
        Font(
            R.font.karla_variable,
            FontWeight.Medium,
            variationSettings = FontVariation.Settings(FontVariation.weight(500)),
        ),
        Font(
            R.font.karla_variable,
            FontWeight.SemiBold,
            variationSettings = FontVariation.Settings(FontVariation.weight(600)),
        ),
    )
}

/** Text roles used across every Papel screen, matching the `1c`/`4a` mockups. */
object PapelType {
    val HeadlineLarge = TextStyle(fontFamily = PapelFonts.Serif, fontSize = 36.sp, lineHeight = 40.sp)
    val DisplaySmall = TextStyle(fontFamily = PapelFonts.Serif, fontSize = 34.sp, lineHeight = 38.sp)
    val TitleLarge = TextStyle(fontFamily = PapelFonts.Serif, fontSize = 26.sp, lineHeight = 30.sp)
    val TitleMedium = TextStyle(fontFamily = PapelFonts.Serif, fontSize = 19.sp, lineHeight = 23.sp)
    val SectionLabel = TextStyle(
        fontFamily = PapelFonts.Karla,
        fontWeight = FontWeight.SemiBold,
        fontSize = 10.sp,
        letterSpacing = 1.8.sp,
    )
    val BodyMedium = TextStyle(fontFamily = PapelFonts.Karla, fontSize = 13.sp, lineHeight = 19.sp)
    val BodySmall = TextStyle(fontFamily = PapelFonts.Karla, fontSize = 12.sp, lineHeight = 17.sp)
    val Mono = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp)
}

/** A 1px hairline row divider — Papel's only hierarchy device besides spacing. */
@Composable
fun PapelRowDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(modifier = modifier, thickness = 1.dp, color = PapelColors.RowDivider)
}

@Composable
fun PapelSectionDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(modifier = modifier, thickness = 1.dp, color = PapelColors.SectionDivider)
}

/** Uppercase bronze eyebrow label, e.g. "BIBLIOTECA" above a serif headline. */
@Composable
fun PapelSectionLabel(text: String, modifier: Modifier = Modifier) {
    CompositionLocalProvider(LocalContentColor provides PapelColors.Accent) {
        Text(
            text = text.uppercase(),
            style = PapelType.SectionLabel,
            modifier = modifier.padding(bottom = 2.dp),
        )
    }
}

@Composable
fun ProvidePapelDefaults(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalContentColor provides PapelColors.OnSurface,
        LocalTextStyle provides PapelType.BodyMedium,
        content = content,
    )
}
