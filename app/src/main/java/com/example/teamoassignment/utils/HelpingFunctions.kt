package com.example.teamoassignment.utils

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresPermission
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun rotationToUiMode(rotation: Int): Boolean {
    return rotation == android.view.Surface.ROTATION_0 || rotation == android.view.Surface.ROTATION_180
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