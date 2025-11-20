package com.example.teamoassignment

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresPermission
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RecordingScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    } else {
        listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    val permissionsState = rememberMultiplePermissionsState(permissions)

    var isRecording by remember { mutableStateOf(false) }
    var recordingCount by remember { mutableStateOf(0) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var videoCapture by remember { mutableStateOf<VideoCapture<Recorder>?>(null) }
    var recording by remember { mutableStateOf<Recording?>(null) }

    val configuration = LocalConfiguration.current
    val isPortrait = configuration.screenHeightDp > configuration.screenWidthDp

    LaunchedEffect(Unit) {
        cameraProvider = getCameraProvider(context)
    }

    if (!permissionsState.allPermissionsGranted) {
        LaunchedEffect(Unit) {
            permissionsState.launchMultiplePermissionRequest()
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Requesting permissions...")
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            cameraProvider = cameraProvider,
            lifecycleOwner = lifecycleOwner,
            onVideoCaptureReady = { videoCapture = it }
        )

        RecordingUI(
            isPortrait = isPortrait,
            isRecording = isRecording,
            recordingCount = recordingCount,
            onRecordClick = {
                if (isRecording) {
                    recording?.stop()
                    recording = null
                    isRecording = false
                } else {
                    val hasAudioPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.RECORD_AUDIO
                    ) == PackageManager.PERMISSION_GRANTED

                    val hasCameraPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED

                    if (!hasCameraPermission || !hasAudioPermission) {
                        return@RecordingUI
                    }

                    videoCapture?.let { capture ->
                        startRecording(
                            context = context,
                            videoCapture = capture,
                            onRecordingStarted = { rec ->
                                recording = rec
                                isRecording = true
                            },
                            onRecordingComplete = { recordingCount++ }
                        )
                    }
                }
            },
            onUploadClick = {}
        )

    }
}

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    cameraProvider: ProcessCameraProvider?,
    lifecycleOwner: LifecycleOwner,
    onVideoCaptureReady: (VideoCapture<Recorder>) -> Unit
) {
    val context = LocalContext.current
    val previewView = remember { PreviewView(context) }

    LaunchedEffect(cameraProvider) {
        cameraProvider?.let { provider ->
            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }

            val qualitySelector = QualitySelector.fromOrderedList(
                listOf(Quality.FHD, Quality.HD, Quality.SD),
                FallbackStrategy.lowerQualityOrHigherThan(Quality.FHD)
            )

            val recorder = Recorder.Builder()
                .setQualitySelector(qualitySelector)
                .setExecutor(ContextCompat.getMainExecutor(context))
                .build()

            val videoCapture = VideoCapture.withOutput(recorder)

            onVideoCaptureReady(videoCapture)

            try {
                provider.unbindAll()
                provider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    videoCapture
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier
    )
}

@Composable
fun RecordingUI(
    isPortrait: Boolean,
    isRecording: Boolean,
    recordingCount: Int,
    onRecordClick: () -> Unit,
    onUploadClick: () -> Unit
) {
    AnimatedContent(
        targetState = isPortrait,
        transitionSpec = {
            fadeIn(tween(300)) togetherWith fadeOut(tween(300))
        },
        label = "orientation_ui"
    ) { portrait ->
        if (portrait) {
            PortraitLayout(
                isRecording = isRecording,
                recordingCount = recordingCount,
                onRecordClick = onRecordClick,
                onUploadClick = onUploadClick
            )
        } else {
            LandscapeLayout(
                isRecording = isRecording,
                recordingCount = recordingCount,
                onRecordClick = onRecordClick,
                onUploadClick = onUploadClick
            )
        }
    }
}


@Composable
fun PortraitLayout(
    isRecording: Boolean,
    recordingCount: Int,
    onRecordClick: () -> Unit,
    onUploadClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = {}) {
                Image(
                    painter = painterResource(id = R.drawable.cancel_button),
                    contentDescription = "My Image"
                )
            }

            UploadButtonWithCounter(count = recordingCount, onClick = onUploadClick)
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ControlButton(R.drawable.cog, stringResource(R.string.settings)) {}
            RecordButton(isRecording, onRecordClick, Modifier.size(64.dp))
            ControlButton(R.drawable.camera, stringResource(R.string._24mm)) {}
        }
    }
}

@Composable
fun LandscapeLayout(
    isRecording: Boolean,
    recordingCount: Int,
    onRecordClick: () -> Unit,
    onUploadClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .weight(0.2f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(onClick = {}) {
                Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
            }

            UploadButtonWithCounter(count = recordingCount, onClick = onUploadClick)
        }

        Spacer(modifier = Modifier.weight(1f))

        RecordButton(isRecording, onRecordClick, Modifier.size(72.dp))

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier
                .padding(16.dp)
                .weight(0.2f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ControlButton(R.drawable.cog, stringResource(R.string.settings)) {}
            ControlButton(R.drawable.camera, stringResource(R.string._24mm)) {}
        }
    }
}

@Composable
fun RecordButton(
    isRecording: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        if (isRecording) 1.1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(if (isRecording) Color.Red else Color(0xFFE53935))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size((72 * scale).dp)
                .clip(CircleShape)
                .background(if (isRecording) Color.Red else Color(0xFFE53935))
        )

        if (isRecording) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(Color.White, MaterialTheme.shapes.small)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(Color.White, CircleShape)
            )
        }
    }
}

@Composable
fun UploadButtonWithCounter(count: Int, onClick: () -> Unit) {
    Box {
        Image(
            modifier = Modifier.clickable { onClick },
            painter = painterResource(id = R.drawable.upload_button),
            contentDescription = "My Image"
        )

        if (count > 0) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(8.dp, (-8).dp),
                shape = CircleShape,
                color = Color.Red
            ) {
                Text(
                    text = count.toString(),
                    modifier = Modifier.padding(4.dp),
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun ControlButton(icon: Int, label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .wrapContentSize()
            .background(
                color = Color(0x99000000),
                shape = RoundedCornerShape(50)
            )
            .clickable {
                onClick
            },
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = "My Image"
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(label, color = Color.White, fontSize = 12.sp)
        }
    }
}

suspend fun getCameraProvider(context: Context): ProcessCameraProvider =
    suspendCoroutine { cont ->
        val future = ProcessCameraProvider.getInstance(context)
        future.addListener(
            { cont.resume(future.get()) },
            ContextCompat.getMainExecutor(context)
        )
    }

@RequiresPermission(Manifest.permission.RECORD_AUDIO)
fun startRecording(
    context: Context,
    videoCapture: VideoCapture<Recorder>,
    onRecordingStarted: (Recording) -> Unit,
    onRecordingComplete: () -> Unit
) {
    val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
        .format(System.currentTimeMillis())

    val values = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/MyTestingApplication")
        }
    }

    val output = MediaStoreOutputOptions.Builder(
        context.contentResolver,
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    )
        .setContentValues(values)
        .build()

    var rec: Recording? = null

    rec = videoCapture.output
        .prepareRecording(context, output)
        .withAudioEnabled()
        .start(ContextCompat.getMainExecutor(context)) { event ->
            when (event) {
                is VideoRecordEvent.Start -> onRecordingStarted(rec!!)
                is VideoRecordEvent.Finalize ->
                    if (!event.hasError()) onRecordingComplete()
            }
        }
}
