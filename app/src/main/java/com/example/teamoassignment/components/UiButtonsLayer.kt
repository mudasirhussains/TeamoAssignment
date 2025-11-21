package com.example.teamoassignment.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha

@Composable
fun UiButtonsLayer(
    animatedMode: Float,
    isRecording: Boolean,
    recordingTime: Long,
    recordingCount: Int,
    onRecord: () -> Unit,
    onUpload: () -> Unit
) {
    val portraitAlpha = 1f - animatedMode
    val landscapeAlpha = animatedMode

    if (portraitAlpha > 0f) {
        Box(
            Modifier
                .fillMaxSize()
                .alpha(portraitAlpha)
        ) {
            PortraitLayout(
                isRecording = isRecording,
                recordingTime = recordingTime,
                recordingCount = recordingCount,
                onRecordClick = onRecord,
                onUploadClick = onUpload
            )
        }
    }

    if (landscapeAlpha > 0f) {
        Box(
            Modifier
                .fillMaxSize()
                .alpha(landscapeAlpha)
        ) {
            LandscapeLayout(
                isRecording = isRecording,
                recordingTime = recordingTime,
                recordingCount = recordingCount,
                onRecordClick = onRecord,
                onUploadClick = onUpload
            )
        }
    }
}