package com.crypticsignals.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.crypticsignals.ui.screens.feed.ShingoGlyph

@Composable
fun AuthRequiredScreen(
    title: String = "Wallet required",
    subtitle: String = "Connect with Privy to continue.",
    isConnecting: Boolean,
    error: String? = null,
    onConnect: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ShingoGlyph(size = 48.dp, color = MaterialTheme.colorScheme.onSurface)
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 8.dp)
        )
        error?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFE23B3B),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        Button(
            onClick = onConnect,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onSurface,
                contentColor = MaterialTheme.colorScheme.background
            ),
            modifier = Modifier.padding(top = 20.dp)
        ) {
            Text(if (isConnecting) "Connecting…" else "Connect wallet")
        }
    }
}
}
