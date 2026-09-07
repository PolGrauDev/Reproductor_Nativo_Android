package com.PolGrauDev.reproductor_nativo_android.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.PolGrauDev.reproductor_nativo_android.ui.screens.style.fanzine.SettingsScreenFanzine
import com.PolGrauDev.reproductor_nativo_android.ui.screens.style.papel.SettingsScreenPapel
import com.PolGrauDev.reproductor_nativo_android.ui.screens.style.sticker.SettingsScreenSticker
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.AppStyle
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicViewModel

/**
 * Thin dispatcher over the three bespoke visual styles — see `ui/screens/style/<style>/
 * SettingsScreenX.kt` for the actual per-style implementations (each built from the design
 * canvas's `4a`/`4b`/`4c` Ajustes mockups) and `ui/theme/style/AppStyle.kt` for the enum.
 */
@Composable
fun SettingsScreen(viewModel: MusicViewModel, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState.appStyle) {
        AppStyle.PAPEL -> SettingsScreenPapel(
            uiState = uiState,
            onBack = onBack,
            onStartSleepTimer = viewModel::startSleepTimer,
            onCancelSleepTimer = viewModel::cancelSleepTimer,
            onSetFadeDurationMs = viewModel::setFadeDurationMs,
            onSetAppStyle = viewModel::setAppStyle,
        )

        AppStyle.STICKERS -> SettingsScreenSticker(
            uiState = uiState,
            onBack = onBack,
            onStartSleepTimer = viewModel::startSleepTimer,
            onCancelSleepTimer = viewModel::cancelSleepTimer,
            onSetFadeDurationMs = viewModel::setFadeDurationMs,
            onSetAppStyle = viewModel::setAppStyle,
        )

        AppStyle.FANZINE -> SettingsScreenFanzine(
            uiState = uiState,
            onBack = onBack,
            onStartSleepTimer = viewModel::startSleepTimer,
            onCancelSleepTimer = viewModel::cancelSleepTimer,
            onSetFadeDurationMs = viewModel::setFadeDurationMs,
            onSetAppStyle = viewModel::setAppStyle,
        )
    }
}
