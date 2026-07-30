package com.PolGrauDev.reproductor_nativo_android.ui.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/** Permiso de audio correcto según la versión de Android: READ_MEDIA_AUDIO en API 33+, READ_EXTERNAL_STORAGE por debajo. */
val audioPermission: String =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

enum class AudioPermissionStatus {
    NotRequested,
    Granted,
    Denied,
    PermanentlyDenied,
}

class AudioPermissionState(
    status: AudioPermissionStatus,
    private val launcher: () -> Unit,
) {
    var status: AudioPermissionStatus = status
        internal set

    fun request() = launcher()
}

@Composable
fun rememberAudioPermissionState(): AudioPermissionState {
    val context = LocalContext.current

    var status by remember {
        mutableStateOf(
            if (isGranted(context)) AudioPermissionStatus.Granted else AudioPermissionStatus.NotRequested
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        status = when {
            granted -> AudioPermissionStatus.Granted
            shouldShowRationale(context) -> AudioPermissionStatus.Denied
            else -> AudioPermissionStatus.PermanentlyDenied
        }
    }

    val state = remember { AudioPermissionState(status) { permissionLauncher.launch(audioPermission) } }
    state.status = status
    return state
}

private fun isGranted(context: Context): Boolean =
    ContextCompat.checkSelfPermission(context, audioPermission) == PackageManager.PERMISSION_GRANTED

private fun shouldShowRationale(context: Context): Boolean {
    val activity = context.findActivity() ?: return false
    return ActivityCompat.shouldShowRequestPermissionRationale(activity, audioPermission)
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
