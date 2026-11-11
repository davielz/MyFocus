package com.myfocus.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.myfocus.app.MyFocusApp
import com.myfocus.app.ui.screen.HomeScreen
import com.myfocus.app.ui.theme.MyFocusTheme
import com.myfocus.app.ui.viewmodel.MainViewModel

/**
 * Activity tunggal (Single-Activity Architecture) sebagai entry point UI MyFocus.
 */
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        val app = application as MyFocusApp
        MainViewModel.Factory(app.repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyFocusTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                HomeScreen(
                    uiState = uiState,
                    onStartFocus = { duration -> viewModel.startFocus(duration) },
                    onStopFocus = { viewModel.stopFocus() }
                )
            }
        }
    }
}

