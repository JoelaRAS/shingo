package com.crypticsignals.wallet

import androidx.compose.runtime.compositionLocalOf
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender

// Provided by MainActivity to avoid registering launchers while the Activity is already RESUMED.
val LocalActivityResultSender = compositionLocalOf<ActivityResultSender?> { null }
