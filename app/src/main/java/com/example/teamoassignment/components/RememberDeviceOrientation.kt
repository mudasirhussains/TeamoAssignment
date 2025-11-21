package com.example.teamoassignment.components

import android.view.OrientationEventListener
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberDeviceOrientation(): Int {
    val context = LocalContext.current
    val orientation = remember { mutableIntStateOf(android.view.Surface.ROTATION_0) }

    DisposableEffect(Unit) {
        val listener = object : OrientationEventListener(context) {
            override fun onOrientationChanged(angle: Int) {
                if (angle == ORIENTATION_UNKNOWN) return

                val newRotation = when {
                    angle in 315..360 || angle in 0..45 -> android.view.Surface.ROTATION_0
                    angle in 46..134 -> android.view.Surface.ROTATION_270
                    angle in 135..224 -> android.view.Surface.ROTATION_180
                    angle in 225..314 -> android.view.Surface.ROTATION_90
                    else -> orientation.value
                }

                orientation.value = newRotation
            }
        }

        listener.enable()
        onDispose { listener.disable() }
    }

    return orientation.value
}