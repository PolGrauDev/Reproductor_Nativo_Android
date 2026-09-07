package com.PolGrauDev.reproductor_nativo_android.ui.screens.style.sticker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.AppStyle
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.StickerColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.StickerHardShadowBox
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.StickerType
import com.PolGrauDev.reproductor_nativo_android.ui.util.formatMillis
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicUiState

private val SLEEP_TIMER_PRESETS_MINUTES = listOf(15, 30, 45, 60, 90)
private const val FADE_MAX_MS = 3000f
private const val FADE_STEP_MS = 250f

private val STYLE_LABELS = mapOf(
    AppStyle.PAPEL to "Papel",
    AppStyle.STICKERS to "Cuaderno",
    AppStyle.FANZINE to "Fanzine",
)

private val STYLE_COLORS = mapOf(
    AppStyle.PAPEL to StickerColors.Mint,
    AppStyle.STICKERS to StickerColors.Pink,
    AppStyle.FANZINE to StickerColors.Grape,
)

@Composable
fun SettingsScreenSticker(
    uiState: MusicUiState,
    onBack: () -> Unit,
    onStartSleepTimer: (Int) -> Unit,
    onCancelSleepTimer: () -> Unit,
    onSetFadeDurationMs: (Int) -> Unit,
    onSetAppStyle: (AppStyle) -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(StickerColors.Paper)
            .verticalScroll(rememberScrollState()),
    ) {
        StickerTopBar(onBack)
        StickerSleepTimerCard(uiState, onStartSleepTimer, onCancelSleepTimer)
        Spacer(Modifier.height(16.dp))
        StickerFadeCard(uiState.fadeDurationMs, onSetFadeDurationMs)
        Spacer(Modifier.height(16.dp))
        StickerStyleCard(uiState.appStyle, onSetAppStyle)
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun StickerTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp, 12.dp, 16.dp, 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StickerHardShadowBox(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(14.dp),
            rotationDegrees = 0f,
        ) {
            Box(Modifier.fillMaxSize().clickable(onClick = onBack), contentAlignment = Alignment.Center) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = StickerColors.Ink)
            }
        }
        Spacer(Modifier.size(12.dp))
        Text("Ajustes", style = StickerType.HeadlineLarge.copy(fontSize = 26.sp), color = StickerColors.Ink)
    }
}

@Composable
private fun StickerSleepTimerCard(uiState: MusicUiState, onStart: (Int) -> Unit, onCancel: () -> Unit) {
    var pickerOpen by remember { mutableStateOf(false) }
    StickerHardShadowBox(
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        rotationDegrees = -0.6f,
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(32.dp).background(StickerColors.Grape, RoundedCornerShape(11.dp)),
                    contentAlignment = Alignment.Center,
                ) { Icon(Icons.Filled.Bedtime, contentDescription = null, tint = StickerColors.Ink) }
                Spacer(Modifier.size(8.dp))
                Text("Para dormir", style = StickerType.TitleMedium, color = StickerColors.Ink)
            }
            Spacer(Modifier.height(8.dp))
            if (uiState.playback.sleepTimerActive) {
                Text("se apaga solita en un ratito", style = StickerType.HandwrittenSmall, color = StickerColors.Faded)
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.background(StickerColors.Butter, RoundedCornerShape(15.dp)).padding(horizontal = 12.dp, vertical = 5.dp),
                    ) { Text(formatMillis(uiState.playback.sleepTimerRemainingMs), style = StickerType.TitleMedium, color = StickerColors.Ink) }
                    Spacer(Modifier.size(10.dp))
                    Text("y a la camita", style = StickerType.HandwrittenSmall, color = StickerColors.Faded, modifier = Modifier.weight(1f))
                    StickerHardShadowBox(shape = RoundedCornerShape(15.dp), rotationDegrees = 0f) {
                        Text(
                            "quitar",
                            style = StickerType.HandwrittenSmall,
                            color = StickerColors.Ink,
                            modifier = Modifier.clickable(onClick = onCancel).padding(horizontal = 12.dp, vertical = 6.dp),
                        )
                    }
                }
            } else {
                Text("se apaga solita cuando te duermas", style = StickerType.HandwrittenSmall, color = StickerColors.Faded)
                Spacer(Modifier.height(12.dp))
                Box(
                    Modifier.background(StickerColors.Butter, RoundedCornerShape(15.dp))
                        .clickable { pickerOpen = !pickerOpen }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) { Text("iniciar (${uiState.sleepTimerDefaultMinutes} min)", style = StickerType.HandwrittenSmall, color = StickerColors.Ink) }
                if (pickerOpen) {
                    Row(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SLEEP_TIMER_PRESETS_MINUTES.forEach { minutes ->
                            Box(
                                Modifier
                                    .background(
                                        if (minutes == uiState.sleepTimerDefaultMinutes) StickerColors.Mint else Color.White,
                                        RoundedCornerShape(12.dp),
                                    )
                                    .clickable {
                                        onStart(minutes)
                                        pickerOpen = false
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                            ) { Text("${minutes}m", style = StickerType.HandwrittenSmall, color = StickerColors.Ink) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StickerFadeCard(fadeDurationMs: Int, onChange: (Int) -> Unit) {
    var sliderValue by remember(fadeDurationMs) { mutableFloatStateOf(fadeDurationMs.toFloat()) }
    StickerHardShadowBox(
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        rotationDegrees = 0.5f,
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(32.dp).background(StickerColors.Mint, RoundedCornerShape(11.dp)),
                    contentAlignment = Alignment.Center,
                ) { Icon(Icons.Filled.GraphicEq, contentDescription = null, tint = StickerColors.Ink) }
                Spacer(Modifier.size(8.dp))
                Text("Fundido suave", style = StickerType.TitleMedium, color = StickerColors.Ink)
            }
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    if (sliderValue <= 0f) "apagado" else "%.1f s".format(sliderValue / 1000f),
                    style = StickerType.HeadlineLarge.copy(fontSize = 26.sp),
                    color = StickerColors.Pink,
                )
                Spacer(Modifier.size(8.dp))
                Text("entre canción y canción", style = StickerType.HandwrittenSmall, color = StickerColors.Faded)
            }
            Spacer(Modifier.height(10.dp))
            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                onValueChangeFinished = { onChange(sliderValue.toInt()) },
                valueRange = 0f..FADE_MAX_MS,
                steps = (FADE_MAX_MS / FADE_STEP_MS).toInt() - 1,
                colors = SliderDefaults.colors(
                    thumbColor = StickerColors.Butter,
                    activeTrackColor = StickerColors.Mint,
                    inactiveTrackColor = Color.White,
                ),
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("nada", style = StickerType.HandwrittenSmall, color = StickerColors.Faded)
                Text("3 s", style = StickerType.HandwrittenSmall, color = StickerColors.Faded)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "Baja el volumen al acabar una y lo sube al empezar la otra. Lo de no dejar silencios ya lo hace solo.",
                style = StickerType.HandwrittenSmall,
                color = StickerColors.Faded,
            )
        }
    }
}

@Composable
private fun StickerStyleCard(current: AppStyle, onSelect: (AppStyle) -> Unit) {
    StickerHardShadowBox(
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        rotationDegrees = -0.4f,
    ) {
        Column(Modifier.padding(14.dp)) {
            Text("Estilo del cuaderno", style = StickerType.TitleMedium, color = StickerColors.Ink)
            Spacer(Modifier.height(4.dp))
            Text("elige cómo quieres verlo todo", style = StickerType.HandwrittenSmall, color = StickerColors.Faded)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AppStyle.entries.forEachIndexed { index, style ->
                    val selected = style == current
                    Box(
                        Modifier
                            .rotate(if (index % 2 == 0) -2f else 2f)
                            .background(STYLE_COLORS.getValue(style), RoundedCornerShape(14.dp))
                            .then(
                                if (selected) Modifier.border(3.dp, StickerColors.Ink, RoundedCornerShape(14.dp)) else Modifier,
                            )
                            .clickable { onSelect(style) }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                    ) {
                        Text(
                            STYLE_LABELS.getValue(style),
                            style = StickerType.HandwrittenSmall,
                            color = StickerColors.Ink,
                        )
                    }
                }
            }
        }
    }
}
