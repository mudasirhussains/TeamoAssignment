package com.example.teamoassignment.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@SuppressLint("DefaultLocale")
@Composable
fun RecordingTimer(timeSec: Long) {
    if (timeSec <= 0) return

    val minutes = timeSec / 60
    val seconds = timeSec % 60

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {

        Box(
            modifier = Modifier
                .padding(top = 32.dp)
                .background(Color(0x66000000), RoundedCornerShape(50))
                .padding(horizontal = 16.dp, vertical = 6.dp),
        ) {
            Text(
                text = String.format("%02d:%02d", minutes, seconds),
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}