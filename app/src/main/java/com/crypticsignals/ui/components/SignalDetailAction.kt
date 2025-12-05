package com.crypticsignals.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SignalActionButton(
    state: ActionState,
    onExecute: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (label, enabled, icon) = when (state) {
        ActionState.Idle -> Triple("Copy Trade Now", true, Icons.Filled.PlayCircle)
        ActionState.Loading -> Triple("Executing...", false, Icons.Filled.PlayCircle)
        ActionState.Success -> Triple("Order Placed!", false, Icons.Filled.CheckCircle)
        ActionState.Expired -> Triple("Signal Expired", false, null)
    }
    Button(
        onClick = { if (enabled) onExecute() },
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (state == ActionState.Success) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
            contentColor = Color(0xFF010C12)
        )
    ) {
        if (state == ActionState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(end = 10.dp),
                color = Color(0xFF010C12),
                strokeWidth = 2.dp
            )
        } else if (icon != null) {
            Icon(icon, contentDescription = null)
        }
        Text(text = label)
    }
}

enum class ActionState { Idle, Loading, Success, Expired }
