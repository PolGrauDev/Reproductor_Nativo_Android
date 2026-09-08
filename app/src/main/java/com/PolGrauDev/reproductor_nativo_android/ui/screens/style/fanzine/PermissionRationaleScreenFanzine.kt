package com.PolGrauDev.reproductor_nativo_android.ui.screens.style.fanzine

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
import androidx.compose.ui.unit.sp
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.FanzineColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.FanzineFonts
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.photocopyGrain

@Composable
fun PermissionRationaleScreenFanzine(message: String, actionLabel: String, onAction: () -> Unit) {
    Column(
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
