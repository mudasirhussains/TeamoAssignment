package com.example.teamoassignment.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teamoassignment.R

@SuppressLint("DefaultLocale")
@Composable
fun LandscapeLayout(
    isRecording: Boolean,
    recordingTime: Long,
    recordingCount: Int,
    onRecordClick: () -> Unit,
    onUploadClick: () -> Unit
) {
    val deviceRotation = rememberDeviceRotation()

    val uiRotation by animateFloatAsState(
        targetValue = when (deviceRotation) {
            90 -> -90f
            270 -> 90f
            else -> -90f
        },
        animationSpec = tween(durationMillis = 300), label = ""
    )

    Column {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {

            if (isRecording) {
                val timerAlignment = when (deviceRotation) {
                    90 -> Alignment.CenterStart
                    270 -> Alignment.CenterEnd
                    else -> Alignment.TopCenter
                }

                Box(
                    modifier = Modifier
                        .align(timerAlignment)
                        .graphicsLayer { rotationZ = uiRotation }
                        .background(Color(0x66000000), RoundedCornerShape(50))
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = String.format("%02d:%02d", recordingTime / 60, recordingTime % 60),
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .padding(
                        top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 8.dp
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Rotated(rotation = uiRotation) {
                    CloseButton()
                }

                Rotated(rotation = uiRotation) {
                    UploadButtonWithCounter(
                        count = recordingCount,
                        onClick = onUploadClick
                    )
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(
                        end = WindowInsets.navigationBars.asPaddingValues().calculateEndPadding(
                            LayoutDirection.Ltr) + 8.dp,
                        bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 8.dp
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Rotated(rotation = uiRotation) {
                    ControlButton(
                        icon = R.drawable.cog,
                        label = stringResource(R.string.settings)
                    ) {}
                }
                Rotated(rotation = uiRotation) {
                    RecordImageButton(
                        isRecording = isRecording,
                        onClick = onRecordClick
                    )
                }
                Rotated(rotation = uiRotation) {
                    ControlButton(
                        icon = R.drawable.camera,
                        label = stringResource(R.string._24mm)
                    ) {}
                }
            }
        }
    }
}