package com.PolGrauDev.reproductor_nativo_android.ui.screens.style.fanzine

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.AppStyle
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.FanzineColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.FanzineCutout
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.FanzineFonts
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.photocopyGrain
import com.PolGrauDev.reproductor_nativo_android.ui.util.formatMillis
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicUiState

private val SLEEP_TIMER_PRESETS_MINUTES = listOf(15, 30, 45, 60, 90)
private const val FADE_MAX_MS = 3000f
private const val FADE_STEP_MS = 250f

private val STYLE_LABELS = mapOf(
    AppStyle.PAPEL to "PAPEL",
    AppStyle.STICKERS to "CUADERNO",
    AppStyle.FANZINE to "FANZINE",
)

@Composable
fun SettingsScreenFanzine(
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
            .background(FanzineColors.Slate)
            .photocopyGrain(alpha = 0.028f)
            .verticalScroll(rememberScrollState()),
    ) {
        FanzineTopBar(onBack)
        FanzineSleepTimerCard(uiState, onStartSleepTimer, onCancelSleepTimer)
        Spacer(Modifier.height(14.dp))
        FanzineFadeCard(uiState.fadeDurationMs, onSetFadeDurationMs)
        Spacer(Modifier.height(14.dp))
        FanzineStyleCard(uiState.appStyle, onSetAppStyle)
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun FanzineTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(14.dp, 12.dp, 14.dp, 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(36.dp)
                .rotate(-2f)
                .background(FanzineColors.Paper)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = FanzineColors.Ink) }
        Spacer(Modifier.size(9.dp))
        Row {
            Text(
                "AJUS",
                fontFamily = FanzineFonts.Anton,
                fontSize = 23.sp,
                color = FanzineColors.Ink,
                modifier = Modifier.rotate(-2f).background(FanzineColors.Paper).padding(horizontal = 4.dp),
            )
            Spacer(Modifier.size(2.dp))
            Text(
                "TES",
                fontFamily = FanzineFonts.Anton,
                fontSize = 20.sp,
                color = FanzineColors.Paper,
                modifier = Modifier.rotate(3f).border(2.dp, FanzineColors.Paper).padding(horizontal = 5.dp, vertical = 1.dp),
            )
        }
    }
}

@Composable
private fun FanzineSleepTimerCard(uiState: MusicUiState, onStart: (Int) -> Unit, onCancel: () -> Unit) {
    var pickerOpen by remember { mutableStateOf(false) }
    FanzineCutout(
        modifier = Modifier.padding(horizontal = 14.dp).fillMaxWidth(),
        rotationDegrees = -0.5f,
    ) {
        Column(Modifier.padding(13.dp, 13.dp, 13.dp, 15.dp)) {
            Text("TEMPORIZADOR", fontFamily = FanzineFonts.Anton, fontSize = 17.sp, color = FanzineColors.Ink)
            Spacer(Modifier.height(6.dp))
            Text(
                if (uiState.playback.sleepTimerActive) "te pausa la música cuando llegue a cero" else "te pausa la música y te deja dormir",
                fontFamily = FanzineFonts.SpecialElite,
                fontSize = 13.sp,
                color = FanzineColors.Grime,
            )
            Spacer(Modifier.height(12.dp))
            if (uiState.playback.sleepTimerActive) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.rotate(-1.5f).background(FanzineColors.Red).padding(horizontal = 9.dp, vertical = 3.dp),
                    ) {
                        Text(formatMillis(uiState.playback.sleepTimerRemainingMs), fontFamily = FanzineFonts.Anton, fontSize = 20.sp, color = FanzineColors.Ink)
                    }
                    Spacer(Modifier.weight(1f))
                    Text(
                        "cancelar",
                        fontFamily = FanzineFonts.SpecialElite,
                        fontSize = 11.sp,
                        color = FanzineColors.Ink,
                        modifier = Modifier.border(2.dp, FanzineColors.Ink).clickable(onClick = onCancel).padding(horizontal = 9.dp, vertical = 3.dp),
                    )
                }
            } else {
                Text(
                    "empezar (${uiState.sleepTimerDefaultMinutes} min)",
                    fontFamily = FanzineFonts.SpecialElite,
                    fontSize = 12.sp,
                    color = FanzineColors.Ink,
                    modifier = Modifier.border(2.dp, FanzineColors.Ink).clickable { pickerOpen = !pickerOpen }.padding(horizontal = 9.dp, vertical = 5.dp),
                )
                if (pickerOpen) {
                    Row(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SLEEP_TIMER_PRESETS_MINUTES.forEach { minutes ->
                            val selected = minutes == uiState.sleepTimerDefaultMinutes
                            Text(
                                "${minutes}m",
                                fontFamily = FanzineFonts.Anton,
                                fontSize = 13.sp,
                                color = if (selected) FanzineColors.Paper else FanzineColors.Ink,
                                modifier = Modifier
                                    .then(if (selected) Modifier.background(FanzineColors.Red) else Modifier.border(1.dp, FanzineColors.Hair))
                                    .clickable {
                                        onStart(minutes)
                                        pickerOpen = false
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FanzineFadeCard(fadeDurationMs: Int, onChange: (Int) -> Unit) {
    var sliderValue by remember(fadeDurationMs) { mutableFloatStateOf(fadeDurationMs.toFloat()) }
    FanzineCutout(
        modifier = Modifier.padding(horizontal = 14.dp).fillMaxWidth(),
        rotationDegrees = 0.6f,
    ) {
        Column(Modifier.padding(13.dp, 13.dp, 13.dp, 16.dp)) {
            Text("FUNDIDO ENTRE CORTES", fontFamily = FanzineFonts.Anton, fontSize = 17.sp, color = FanzineColors.Ink)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    if (sliderValue <= 0f) "apagado" else "%.1f s".format(sliderValue / 1000f),
                    fontFamily = FanzineFonts.Anton,
                    fontSize = 26.sp,
                    color = FanzineColors.Red,
                )
                Spacer(Modifier.size(8.dp))
                Text("de 0 a 3 s", fontFamily = FanzineFonts.SpecialElite, fontSize = 12.sp, color = FanzineColors.Grime)
            }
            Spacer(Modifier.height(12.dp))
            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                onValueChangeFinished = { onChange(sliderValue.toInt()) },
                valueRange = 0f..FADE_MAX_MS,
                steps = (FADE_MAX_MS / FADE_STEP_MS).toInt() - 1,
                colors = SliderDefaults.colors(
                    thumbColor = FanzineColors.Red,
                    activeTrackColor = FanzineColors.Red,
                    inactiveTrackColor = FanzineColors.Slate,
                ),
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("0", fontFamily = FanzineFonts.SpecialElite, fontSize = 11.sp, color = FanzineColors.Grime)
                Text("0,25 s por paso", fontFamily = FanzineFonts.SpecialElite, fontSize = 11.sp, color = FanzineColors.Grime)
                Text("3", fontFamily = FanzineFonts.SpecialElite, fontSize = 11.sp, color = FanzineColors.Grime)
            }
            Spacer(Modifier.height(10.dp))
            Text(
                "Baja el volumen al final de una pista y lo sube al empezar la siguiente. No es un solapamiento real: la reproducción sin cortes ya va sola.",
                fontFamily = FanzineFonts.SpecialElite,
                fontSize = 12.sp,
                color = FanzineColors.Grime,
            )
        }
    }
}

@Composable
private fun FanzineStyleCard(current: AppStyle, onSelect: (AppStyle) -> Unit) {
    FanzineCutout(
        modifier = Modifier.padding(horizontal = 14.dp).fillMaxWidth(),
        rotationDegrees = -0.3f,
    ) {
        Column(Modifier.padding(13.dp, 13.dp, 13.dp, 16.dp)) {
            Text("ESTILO", fontFamily = FanzineFonts.Anton, fontSize = 17.sp, color = FanzineColors.Ink)
            Spacer(Modifier.height(4.dp))
            Text("elige la maqueta", fontFamily = FanzineFonts.SpecialElite, fontSize = 12.sp, color = FanzineColors.Grime)
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AppStyle.entries.forEachIndexed { index, style ->
                    val selected = style == current
                    Text(
                        STYLE_LABELS.getValue(style),
                        fontFamily = FanzineFonts.Anton,
                        fontSize = 13.sp,
                        color = if (selected) FanzineColors.Paper else FanzineColors.Ink,
                        modifier = Modifier
                            .rotate(if (index % 2 == 0) -1.5f else 1.5f)
                            .then(if (selected) Modifier.background(FanzineColors.Red) else Modifier.border(2.dp, FanzineColors.Hair))
                            .clickable { onSelect(style) }
                            .padding(horizontal = 9.dp, vertical = 5.dp),
                    )
                }
            }
        }
    }
}

