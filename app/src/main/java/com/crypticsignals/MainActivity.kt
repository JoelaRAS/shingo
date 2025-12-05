package com.crypticsignals

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.crypticsignals.ui.theme.CrypticSignalsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        setContent {
            CrypticSignalsTheme(useDarkTheme = true) {
                CrypticSignalsApp()
            }
        }
    }
}
