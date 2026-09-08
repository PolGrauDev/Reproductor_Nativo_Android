package com.PolGrauDev.reproductor_nativo_android

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.PolGrauDev.reproductor_nativo_android.ui.components.PlaybackErrorSnackbar
import com.PolGrauDev.reproductor_nativo_android.ui.navigation.NavGraph
import com.PolGrauDev.reproductor_nativo_android.ui.permissions.AudioPermissionStatus
import com.PolGrauDev.reproductor_nativo_android.ui.permissions.rememberAudioPermissionState
import com.PolGrauDev.reproductor_nativo_android.ui.screens.PermissionRationaleScreen
import com.PolGrauDev.reproductor_nativo_android.ui.theme.Reproductor_Nativo_AndroidTheme
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
                AudioLibraryGate(viewModel = musicViewModel)
            }
        }
    }
}

@Composable
private fun AudioLibraryGate(viewModel: MusicViewModel) {
    val context = LocalContext.current
    val permissionState = rememberAudioPermissionState()
    val snackbarHostState = remember { SnackbarHostState() }

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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data -> PlaybackErrorSnackbar(uiState.appStyle, data) }
        },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (permissionState.status) {
                AudioPermissionStatus.Granted -> NavGraph(viewModel = viewModel)

                AudioPermissionStatus.PermanentlyDenied -> PermissionRationaleScreen(
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

                AudioPermissionStatus.NotRequested, AudioPermissionStatus.Denied -> PermissionRationaleScreen(
                    style = uiState.appStyle,
                    message = "Reproductor de Música necesita acceso al audio del dispositivo para " +
                        "mostrar tu biblioteca de canciones.",
                    actionLabel = "Conceder permiso",
                    onAction = permissionState::request,
                )
            }
        }
    }
}
