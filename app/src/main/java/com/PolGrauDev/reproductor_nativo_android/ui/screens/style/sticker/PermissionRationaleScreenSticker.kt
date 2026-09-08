package com.PolGrauDev.reproductor_nativo_android.ui.screens.style.sticker

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.StickerColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.StickerType

@Composable
fun PermissionRationaleScreenSticker(message: String, actionLabel: String, onAction: () -> Unit) {
    Column(
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
}
