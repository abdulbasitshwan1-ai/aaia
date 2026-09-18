package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.ui.screens.MainBasokaScreen
import com.example.ui.theme.BasokaTheme
import com.example.ui.theme.ObsidianBg
import com.example.ui.viewmodel.BasokaViewModel
import com.example.ui.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {

    private val basokaViewModel: BasokaViewModel by viewModels()
    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BasokaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ObsidianBg
                ) {
                    MainBasokaScreen(
                        basokaVm = basokaViewModel,
                        gameVm = gameViewModel
                    )
                }
            }
        }
    }
}

