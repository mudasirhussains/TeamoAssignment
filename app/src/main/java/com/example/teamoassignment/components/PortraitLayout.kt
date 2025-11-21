package com.example.teamoassignment.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.teamoassignment.R

@Composable
fun PortraitLayout(
    isRecording: Boolean,
    recordingTime: Long,
    recordingCount: Int,
    onRecordClick: () -> Unit,
    onUploadClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {

        if (isRecording) {
            RecordingTimer(recordingTime)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (!isRecording) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CloseButton()

                    UploadButtonWithCounter(count = recordingCount, onClick = onUploadClick)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ControlButton(R.drawable.cog, stringResource(R.string.settings)) {}
                RecordImageButton(isRecording, onRecordClick)
                ControlButton(R.drawable.camera, stringResource(R.string._24mm)) {}
            }
        }
    }
}