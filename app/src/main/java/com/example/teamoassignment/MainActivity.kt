package com.example.teamoassignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.example.teamoassignment.components.RecordingScreen
import com.example.teamoassignment.ui.theme.TeamoAssignmentTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            TeamoAssignmentTheme {
                RecordingScreen()
            }
        }
    }
}
