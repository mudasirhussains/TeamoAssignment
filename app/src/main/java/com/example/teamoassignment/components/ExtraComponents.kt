package com.example.teamoassignment.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun Rotated(
    rotation: Float,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier.graphicsLayer {
            rotationZ = rotation
            transformOrigin = TransformOrigin(0.5f, 0.5f)
        }
    ) {
        content()
    }
}