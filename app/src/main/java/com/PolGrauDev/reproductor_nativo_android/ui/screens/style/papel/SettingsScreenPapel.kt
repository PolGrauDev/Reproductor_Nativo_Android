package com.PolGrauDev.reproductor_nativo_android.ui.screens.style.papel

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.AppStyle
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelRowDivider
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelSectionDivider
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelSectionLabel
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelType
import com.PolGrauDev.reproductor_nativo_android.ui.util.formatMillis
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicUiState

private val SLEEP_TIMER_PRESETS_MINUTES = listOf(15, 30, 45, 60, 90)
private const val FADE_MAX_MS = 3000f
private const val FADE_STEP_MS = 250f

private val STYLE_LABELS = mapOf(
    AppStyle.PAPEL to "Papel",
    AppStyle.STICKERS to "Cuaderno de pegatinas",
    AppStyle.FANZINE to "Fanzine fotocopiado",
)

@Composable
fun SettingsScreenPapel(
    uiState: MusicUiState,
    onBack: () -> Unit,
    onStartSleepTimer: (Int) -> Unit,
    onCancelSleepTimer: () -> Unit,
    onSetFadeDurationMs: (Int) -> Unit,
    onSetAppStyle: (AppStyle) -> Unit,
) {
    Scaffold(containerColor = PapelColors.Background) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            PapelTopBar(onBack = onBack)
            PapelSleepTimerSection(
                uiState = uiState,
                onStart = onStartSleepTimer,
                onCancel = onCancelSleepTimer,
            )
            PapelSectionDivider()
            PapelFadeSection(
                fadeDurationMs = uiState.fadeDurationMs,
                onChange = onSetFadeDurationMs,
            )
            PapelSectionDivider()
            PapelStyleSection(current = uiState.appStyle, onSelect = onSetAppStyle)
        }
    }
}

@Composable
private fun PapelTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp, start = 4.dp, end = 20.dp, bottom = 0.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = PapelColors.OnSurfaceVariant)
        }
        Text(
            "AJUSTES",
            style = PapelType.SectionLabel,
            color = PapelColors.Accent,
            modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.size(22.dp))
    }
}

@Composable
private fun PapelSleepTimerSection(uiState: MusicUiState, onStart: (Int) -> Unit, onCancel: () -> Unit) {
    var pickerOpen by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth().padding(20.dp, 22.dp, 20.dp, 20.dp)) {
        PapelSectionLabel("Temporizador")
        Text("Pausar al dormir", style = PapelType.TitleLarge, color = PapelColors.OnSurface)
        Spacer(Modifier.height(18.dp))
        if (uiState.playback.sleepTimerActive) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                Column {
                    Text(formatMillis(uiState.playback.sleepTimerRemainingMs), style = PapelType.DisplaySmall, color = PapelColors.OnSurface)
                    Text("restante", style = PapelType.SectionLabel, color = PapelColors.OnSurfaceVariant)
                }
                Spacer(Modifier.weight(1f))
                UnderlinedLink("Cancelar", onClick = onCancel)
            }
        } else {
            UnderlinedLink(
                "Iniciar temporizador (${uiState.sleepTimerDefaultMinutes} min)",
                onClick = { pickerOpen = !pickerOpen },
            )
            if (pickerOpen) {
                Column(Modifier.padding(top = 12.dp)) {
                    SLEEP_TIMER_PRESETS_MINUTES.forEach { minutes ->
                        Text(
                            "$minutes min",
                            style = PapelType.BodyMedium,
                            color = if (minutes == uiState.sleepTimerDefaultMinutes) PapelColors.OnSurface else PapelColors.OnSurfaceVariant,
                            fontWeight = if (minutes == uiState.sleepTimerDefaultMinutes) FontWeight.SemiBold else FontWeight.Normal,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onStart(minutes)
                                    pickerOpen = false
                                }
                                .padding(vertical = 8.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PapelFadeSection(fadeDurationMs: Int, onChange: (Int) -> Unit) {
    var sliderValue by remember(fadeDurationMs) { mutableFloatStateOf(fadeDurationMs.toFloat()) }
    Column(Modifier.fillMaxWidth().padding(20.dp, 22.dp, 20.dp, 20.dp)) {
        PapelSectionLabel("Reproducción")
        Text("Fundido entre pistas", style = PapelType.TitleLarge, color = PapelColors.OnSurface)
        Spacer(Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                if (sliderValue <= 0f) "Desactivado" else "%.1f s".format(sliderValue / 1000f),
                style = PapelType.DisplaySmall,
                color = PapelColors.OnSurface,
            )
            Spacer(Modifier.size(10.dp))
            Text("de 0 a 3 s", style = PapelType.BodySmall, color = PapelColors.OnSurfaceVariant)
        }
        Spacer(Modifier.height(20.dp))
        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            onValueChangeFinished = { onChange(sliderValue.toInt()) },
            valueRange = 0f..FADE_MAX_MS,
            steps = (FADE_MAX_MS / FADE_STEP_MS).toInt() - 1,
            colors = SliderDefaults.colors(
                thumbColor = PapelColors.Primary,
                activeTrackColor = PapelColors.Primary,
                inactiveTrackColor = PapelColors.Outline,
            ),
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("0", style = PapelType.Mono, color = PapelColors.OnSurfaceVariant)
            Text("pasos de 0,25 s", style = PapelType.Mono, color = PapelColors.OnSurfaceVariant)
            Text("3", style = PapelType.Mono, color = PapelColors.OnSurfaceVariant)
        }
        Spacer(Modifier.height(16.dp))
        Text(
            "Baja el volumen al final de una pista y lo sube al empezar la siguiente. No es un " +
                "solapamiento de audio real, y la reproducción sin cortes entre pistas ya es automática.",
            style = PapelType.BodySmall,
            color = PapelColors.OnSurfaceVariant,
        )
    }
}

@Composable
private fun PapelStyleSection(current: AppStyle, onSelect: (AppStyle) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth().padding(20.dp, 22.dp, 20.dp, 24.dp)) {
        PapelSectionLabel("Apariencia")
        Text("Estilo visual", style = PapelType.TitleLarge, color = PapelColors.OnSurface)
        Spacer(Modifier.height(14.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(STYLE_LABELS.getValue(current), style = PapelType.TitleMedium, color = PapelColors.OnSurface, modifier = Modifier.weight(1f))
            UnderlinedLink(if (expanded) "Cerrar" else "Cambiar", onClick = { expanded = !expanded })
        }
        if (expanded) {
            AppStyle.entries.forEach { style ->
                PapelRowDivider()
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelect(style)
                            expanded = false
                        }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        Modifier
                            .size(6.dp)
                            .background(if (style == current) PapelColors.Primary else PapelColors.Outline, CircleShape),
                    )
                    Spacer(Modifier.size(12.dp))
                    Text(
                        STYLE_LABELS.getValue(style),
                        style = PapelType.BodyMedium,
                        color = if (style == current) PapelColors.OnSurface else PapelColors.OnSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun UnderlinedLink(text: String, onClick: () -> Unit) {
    Text(
        text.uppercase(),
        style = PapelType.SectionLabel,
        color = PapelColors.OnSurface,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(bottom = 4.dp),
    )
}
