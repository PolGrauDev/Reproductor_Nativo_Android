package com.PolGrauDev.reproductor_nativo_android

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.PolGrauDev.reproductor_nativo_android.ui.navigation.NavGraph
import com.PolGrauDev.reproductor_nativo_android.ui.permissions.AudioPermissionStatus
import com.PolGrauDev.reproductor_nativo_android.ui.permissions.rememberAudioPermissionState
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
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        AudioLibraryGate(viewModel = musicViewModel)
                    }
                }
            }
        }
    }
}

@Composable
private fun AudioLibraryGate(viewModel: MusicViewModel) {
    val context = LocalContext.current
    val permissionState = rememberAudioPermissionState()

    LaunchedEffect(Unit) {
        if (permissionState.status == AudioPermissionStatus.NotRequested) {
            permissionState.request()
        }
    }

    when (permissionState.status) {
        AudioPermissionStatus.Granted -> NavGraph(viewModel = viewModel)

        AudioPermissionStatus.PermanentlyDenied -> PermissionRationale(
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
            message = "Reproductor de Música necesita acceso al audio del dispositivo para " +
                "mostrar tu biblioteca de canciones.",
            actionLabel = "Conceder permiso",
            onAction = permissionState::request,
        )
    }
}

@Composable
private fun PermissionRationale(message: String, actionLabel: String, onAction: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(message, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onAction) { Text(actionLabel) }
    }
}
