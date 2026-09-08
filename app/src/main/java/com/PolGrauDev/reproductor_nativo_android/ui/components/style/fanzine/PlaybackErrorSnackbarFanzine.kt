package com.PolGrauDev.reproductor_nativo_android.ui.components.style.fanzine

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.FanzineColors
import com.PolGrauDev.reproductor_nativo_android.ui.theme.style.fanzine.FanzineFonts

@Composable
fun PlaybackErrorSnackbarFanzine(data: SnackbarData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .rotate(-1f)
            .background(FanzineColors.Paper)
            .border(2.dp, FanzineColors.Hair)
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.width(6.dp).fillMaxHeight().background(FanzineColors.Red))
        Spacer(Modifier.width(11.dp))
        Text(
            data.visuals.message,
            fontFamily = FanzineFonts.SpecialElite,
            fontSize = 13.sp,
            color = FanzineColors.Ink,
            modifier = Modifier.padding(vertical = 11.dp),
        )
    }
}
