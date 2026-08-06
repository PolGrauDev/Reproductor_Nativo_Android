package com.PolGrauDev.reproductor_nativo_android.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.PolGrauDev.reproductor_nativo_android.ui.util.formatMillis
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicUiState
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicViewModel

private val SLEEP_TIMER_PRESETS_MINUTES = listOf(15, 30, 45, 60, 90)
private const val FADE_MAX_MS = 3000f
private const val FADE_STEP_MS = 250f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: MusicViewModel, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            SleepTimerSection(
                uiState = uiState,
                onStart = viewModel::startSleepTimer,
                onCancel = viewModel::cancelSleepTimer,
            )
            HorizontalDivider()
            FadeSection(
                fadeDurationMs = uiState.fadeDurationMs,
                onChange = viewModel::setFadeDurationMs,
            )
        }
    }
}

@Composable
private fun SleepTimerSection(uiState: MusicUiState, onStart: (Int) -> Unit, onCancel: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text("Temporizador para pausar", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        if (uiState.playback.sleepTimerActive) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Se pausará en ${formatMillis(uiState.playback.sleepTimerRemainingMs)}")
                Spacer(Modifier.weight(1f))
                TextButton(onClick = onCancel) { Text("Cancelar") }
            }
        } else {
            SleepTimerPresetMenu(default = uiState.sleepTimerDefaultMinutes, onSelect = onStart)
        }
    }
}

@Composable
private fun SleepTimerPresetMenu(default: Int, onSelect: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        TextButton(onClick = { expanded = true }) {
            Text("Iniciar temporizador ($default min)")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            SLEEP_TIMER_PRESETS_MINUTES.forEach { minutes ->
                DropdownMenuItem(
                    text = { Text("$minutes min") },
                    onClick = {
                        onSelect(minutes)
                        expanded = false
                    },
                    leadingIcon = {
                        if (minutes == default) {
                            Icon(Icons.Filled.Check, contentDescription = null)
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun FadeSection(fadeDurationMs: Int, onChange: (Int) -> Unit) {
    var sliderValue by remember(fadeDurationMs) { mutableFloatStateOf(fadeDurationMs.toFloat()) }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text("Fundido entre canciones", style = MaterialTheme.typography.titleMedium)
        Text(
            if (sliderValue <= 0f) "Desactivado" else "%.1f s".format(sliderValue / 1000f),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            onValueChangeFinished = { onChange(sliderValue.toInt()) },
            valueRange = 0f..FADE_MAX_MS,
            steps = (FADE_MAX_MS / FADE_STEP_MS).toInt() - 1,
        )
        Text(
            "Baja el volumen al final de una pista y lo sube al empezar la siguiente. No es un " +
                "solapamiento de audio real, y la reproducción sin cortes entre pistas ya es automática.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
