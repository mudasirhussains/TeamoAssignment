package com.example.teamoassignment.components

import android.view.OrientationEventListener
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberDeviceRotation(): Int {
    val context = LocalContext.current

    val rotation = remember { mutableStateOf(0) }

    DisposableEffect(Unit) {
        val listener = object : OrientationEventListener(context) {
            override fun onOrientationChanged(orientation: Int) {
                rotation.value = when {
                    orientation in 60..140 -> 90
                    orientation in 220..300 -> 270
                    else -> rotation.value
                }
            }
        }
        listener.enable()

        onDispose { listener.disable() }
    }

    return rotation.value
}