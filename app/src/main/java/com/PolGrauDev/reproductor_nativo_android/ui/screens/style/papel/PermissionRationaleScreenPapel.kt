package com.PolGrauDev.reproductor_nativo_android.ui.screens.style.papel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.papel.PapelType

@Composable
fun PermissionRationaleScreenPapel(message: String, actionLabel: String, onAction: () -> Unit) {
    Column(
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
}
