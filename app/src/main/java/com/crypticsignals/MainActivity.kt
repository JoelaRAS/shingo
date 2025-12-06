package com.crypticsignals

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.crypticsignals.ui.theme.CrypticSignalsTheme
import com.crypticsignals.wallet.LocalActivityResultSender
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender

class MainActivity : ComponentActivity() {
    private lateinit var activityResultSender: ActivityResultSender

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        activityResultSender = ActivityResultSender(this)
        setContent {
            CrypticSignalsTheme(useDarkTheme = true) {
                androidx.compose.runtime.CompositionLocalProvider(
                    LocalActivityResultSender provides activityResultSender
                ) {
                    CrypticSignalsApp()
                }
            }
        }
    }
}
