@file:Suppress("UNUSED_EXPRESSION")

package com.example.teamoassignment.components

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teamoassignment.R

@Composable
fun UploadButtonWithCounter(count: Int, onClick: () -> Unit) {
    Box {
        Image(
            modifier = Modifier.clickable { onClick },
            painter = painterResource(id = R.drawable.upload_button),
            contentDescription = "My Image"
        )

        if (count > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(8.dp, (-8).dp)
                    .size(20.dp)
                    .background(Color.Red, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = count.toString(),
                    color = Color.White,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun RecordImageButton(
    isRecording: Boolean,
    onClick: () -> Unit
) {
    val icon = if (isRecording) {
        R.drawable.recording
    } else {
        R.drawable.record_idlesvg
    }

    Image(
        painter = painterResource(id = icon),
        contentDescription = "Record",
        modifier = Modifier
            .size(90.dp)
            .clickable { onClick() }
    )
}

@Composable
fun CloseButton(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Image(
        painter = painterResource(id = R.drawable.cancel_button),
        contentDescription = "Close",
        modifier = modifier
            .clickable {
                (context as? Activity)?.finish()
            }
    )
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
            Text(
                text = label,
                color = Color.White,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}