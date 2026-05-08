package io.tvelu77.freya

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import io.tvelu77.freya.presentation.navigation.NavGraph
import io.tvelu77.freya.ui.theme.FreyaTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FreyaTheme {
                NavGraph()
            }
        }
    }
}