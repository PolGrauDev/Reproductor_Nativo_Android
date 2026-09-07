package com.PolGrauDev.reproductor_nativo_android

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.PolGrauDev.reproductor_nativo_android.ui.navigation.NavGraph
import com.PolGrauDev.reproductor_nativo_android.ui.permissions.AudioPermissionStatus
import com.PolGrauDev.reproductor_nativo_android.ui.permissions.rememberAudioPermissionState
import com.PolGrauDev.reproductor_nativo_android.ui.theme.Reproductor_Nativo_AndroidTheme
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.AppStyle
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.FanzineColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.FanzineFonts
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.photocopyGrain
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelType
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.StickerColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.StickerType
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicViewModel
import com.PolGrauDev.reproductor_nativo_android.viewmodel.MusicViewModelFactory

class MainActivity : ComponentActivity() {

    private val musicViewModel: MusicViewModel by viewModels {
        MusicViewModelFactory(application as App)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Reproductor_Nativo_AndroidTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        AudioLibraryGate(viewModel = musicViewModel, snackbarHostState = snackbarHostState)
                    }
                }
            }
        }
    }
}

@Composable
private fun AudioLibraryGate(viewModel: MusicViewModel, snackbarHostState: SnackbarHostState) {
    val context = LocalContext.current
    val permissionState = rememberAudioPermissionState()

    LaunchedEffect(Unit) {
        if (permissionState.status == AudioPermissionStatus.NotRequested) {
            permissionState.request()
        }
    }

    LaunchedEffect(permissionState.status) {
        if (permissionState.status == AudioPermissionStatus.Granted) {
            viewModel.refreshLibrary()
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(uiState.playback.errorMessage) {
        val message = uiState.playback.errorMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        viewModel.clearPlaybackError()
    }

    when (permissionState.status) {
        AudioPermissionStatus.Granted -> NavGraph(viewModel = viewModel)

        AudioPermissionStatus.PermanentlyDenied -> PermissionRationale(
            style = uiState.appStyle,
            message = "Sin acceso a la música del dispositivo no se puede mostrar la biblioteca. " +
                "Concede el permiso desde los ajustes de la aplicación.",
            actionLabel = "Abrir ajustes",
            onAction = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            },
        )

        AudioPermissionStatus.NotRequested, AudioPermissionStatus.Denied -> PermissionRationale(
            style = uiState.appStyle,
            message = "Reproductor de Música necesita acceso al audio del dispositivo para " +
                "mostrar tu biblioteca de canciones.",
            actionLabel = "Conceder permiso",
            onAction = permissionState::request,
        )
    }
}

/**
 * Lightweight per-style pass (background/typography/button) — no bespoke mockup exists for this
 * screen in the design canvas, unlike every other screen in the app.
 */
@Composable
private fun PermissionRationale(style: AppStyle, message: String, actionLabel: String, onAction: () -> Unit) {
    when (style) {
        AppStyle.PAPEL -> Column(
            modifier = Modifier.fillMaxSize().background(PapelColors.Background).padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(message, style = PapelType.BodyMedium, color = PapelColors.OnSurface, textAlign = TextAlign.Center)
            Spacer(Modifier.height(20.dp))
            Text(
                actionLabel.uppercase(),
                style = PapelType.SectionLabel,
                color = PapelColors.OnSurface,
                modifier = Modifier.clickable(onClick = onAction).padding(8.dp),
            )
        }

        AppStyle.STICKERS -> Column(
            modifier = Modifier.fillMaxSize().background(StickerColors.Paper).padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(message, style = StickerType.Handwritten, color = StickerColors.Ink, textAlign = TextAlign.Center)
            Spacer(Modifier.height(18.dp))
            Box(
                Modifier.background(StickerColors.Pink, RoundedCornerShape(18.dp)).clickable(onClick = onAction).padding(horizontal = 20.dp, vertical = 12.dp),
            ) { Text(actionLabel, style = StickerType.TitleMedium, color = Color.White) }
        }

        AppStyle.FANZINE -> Column(
            modifier = Modifier.fillMaxSize().background(FanzineColors.Slate).photocopyGrain().padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(message, fontFamily = FanzineFonts.SpecialElite, fontSize = 14.sp, color = FanzineColors.Faded, textAlign = TextAlign.Center)
            Spacer(Modifier.height(18.dp))
            Text(
                actionLabel.uppercase(),
                fontFamily = FanzineFonts.Anton,
                fontSize = 15.sp,
                color = FanzineColors.Ink,
                modifier = Modifier.background(FanzineColors.Red).clickable(onClick = onAction).padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
    }
}
