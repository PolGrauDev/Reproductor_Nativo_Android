package com.PolGrauDev.reproductor_nativo_android.ui.components.style.sticker

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.StickerColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.StickerHardShadowBox
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.sticker.StickerType

@Composable
fun PlaybackErrorSnackbarSticker(data: SnackbarData) {
    StickerHardShadowBox(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = StickerColors.Paper,
        borderColor = StickerColors.Ink,
        borderWidth = 3.dp,
        shadowOffsetX = 4.dp,
        shadowOffsetY = 4.dp,
    ) {
        Row(Modifier.padding(14.dp, 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Warning, contentDescription = null, tint = StickerColors.Pink)
            Spacer(Modifier.width(9.dp))
            Text(data.visuals.message, style = StickerType.HandwrittenSmall, color = StickerColors.Ink)
        }
    }
}
