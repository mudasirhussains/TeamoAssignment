package com.example.teamoassignment.components

import android.Manifest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.example.teamoassignment.utils.getCameraProvider
import com.example.teamoassignment.utils.rotationToUiMode
import com.example.teamoassignment.utils.startRecording
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.delay

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RecordingScreen() {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val permissions = listOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    val permissionState = rememberMultiplePermissionsState(permissions)

    LaunchedEffect(Unit) {
        if (!permissionState.allPermissionsGranted)
            permissionState.launchMultiplePermissionRequest()
    }

    if (!permissionState.allPermissionsGranted) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Requesting permissions…")
        }
        return
    }

    val cameraProvider = remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var cameraBound by rememberSaveable { mutableStateOf(false) }
    var videoCapture by remember { mutableStateOf<VideoCapture<Recorder>?>(null) }

    LaunchedEffect(Unit) {
        cameraProvider.value = getCameraProvider(context)
    }

    var isRecording by rememberSaveable { mutableStateOf(false) }
    var recording by remember { mutableStateOf<Recording?>(null) }
    var recordingTime by rememberSaveable { mutableStateOf(0L) }
    var recordingCount by rememberSaveable { mutableStateOf(0) }

    LaunchedEffect(isRecording) {
        while (isRecording) {
            delay(1000)
            recordingTime++
        }
        if (!isRecording) recordingTime = 0
    }

    val rotation = rememberDeviceOrientation()
    val isPortrait = rotationToUiMode(rotation)

    val animatedMode by animateFloatAsState(
        targetValue = if (isPortrait) 0f else 1f,
        animationSpec = tween(300)
    )

    Box(Modifier.fillMaxSize().background(Color.Black)) {


        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            cameraProvider = cameraProvider.value,
            lifecycleOwner = lifecycleOwner,
            cameraBound = cameraBound,
            onCameraBound = { cameraBound = true },
            onVideoCaptureReady = { videoCapture = it }
        )

        UiButtonsLayer(
            animatedMode = animatedMode,
            isRecording = isRecording,
            recordingTime = recordingTime,
            recordingCount = recordingCount,
            onRecord = {
                if (isRecording) {
                    recording?.stop()
                    recording = null
                    isRecording = false
                } else {
                    videoCapture?.let { capture ->
                        startRecording(
                            context = context,
                            videoCapture = capture,
                            onRecordingStarted = {
                                recording = it
                                isRecording = true
                            },
                            onRecordingComplete = { recordingCount++ }
                        )
                    }
                }
            },
            onUpload = {}
        )
    }
}
