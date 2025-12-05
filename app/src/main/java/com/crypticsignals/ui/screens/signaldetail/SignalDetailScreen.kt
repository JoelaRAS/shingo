package com.crypticsignals.ui.screens.signaldetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.crypticsignals.data.model.Signal
import com.crypticsignals.data.model.SignalStatus
import com.crypticsignals.data.model.Trader
import com.crypticsignals.ui.components.ActionState
import com.crypticsignals.ui.components.ConfidenceBar
import com.crypticsignals.ui.components.DirectionBadge
import com.crypticsignals.ui.components.GlassCard
import com.crypticsignals.ui.components.NeonBadge
import com.crypticsignals.ui.components.SignalActionButton
import com.crypticsignals.ui.components.StatusBadge
import com.crypticsignals.ui.theme.PrimaryBlue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun SignalDetailScreen(
    signal: Signal?,
    trader: Trader?
) {
    if (signal == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Signal not found", color = MaterialTheme.colorScheme.onSurface)
        }
        return
    }

    val isActive = signal.status == SignalStatus.ACTIVE
    var actionState by remember { mutableStateOf(if (isActive) ActionState.Idle else ActionState.Expired) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(actionState) {
        if (actionState == ActionState.Success) {
            delay(1600)
            actionState = if (isActive) ActionState.Idle else ActionState.Expired
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0A0B12), Color(0xFF0F1321))
                    )
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .padding(bottom = 140.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Header(signal = signal, trader = trader)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                NeonBadge(text = signal.timeframe)
                NeonBadge(text = signal.entryType.name, color = MaterialTheme.colorScheme.secondary)
                StatusBadge(status = signal.status)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                InfoTile(
                    title = "Entry",
                    value = formatPrice(signal.entryPrice),
                    icon = Icons.Filled.MyLocation,
                    highlight = false,
                    modifier = Modifier.weight(1f)
                )
                InfoTile(
                    title = "Stop Loss",
                    value = formatPrice(signal.stopLoss),
                    icon = Icons.Filled.Warning,
                    highlight = true,
                    modifier = Modifier.weight(1f)
                )
            }
            ConfidenceBar(level = signal.confidence)
            ProfitTargets(takeProfits = signal.takeProfits)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Created", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Text(formatTimestamp(signal.createdAt), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Expires", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Text(formatTimestamp(signal.expiresAt), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            SignalActionButton(
                state = actionState,
                onExecute = {
                    if (!isActive) {
                        actionState = ActionState.Expired
                        return@SignalActionButton
                    }
                    scope.launch {
                        actionState = ActionState.Loading
                        delay(1200)
                        actionState = ActionState.Success
                    }
                }
            )
        }
    }
}

@Composable
private fun Header(signal: Signal, trader: Trader?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = trader?.avatarUrl,
                contentDescription = trader?.name,
                modifier = Modifier
                    .height(48.dp)
                    .width(48.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Column {
                Text(
                    text = signal.pair,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "by ${trader?.name ?: "Unknown"}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
        DirectionBadge(direction = signal.direction)
    }
}

@Composable
private fun InfoTile(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    highlight: Boolean,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (highlight) Color(0xFFFB7185) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (highlight) Color(0xFFFB7185) else MaterialTheme.colorScheme.onSurface
                )
            }
            androidx.compose.material3.Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (highlight) Color(0xFFFB7185) else PrimaryBlue
            )
        }
    }
}

@Composable
private fun ProfitTargets(takeProfits: List<com.crypticsignals.data.model.TakeProfit>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Profit Targets",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        takeProfits.forEachIndexed { idx, tp ->
            GlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF10B981).copy(alpha = 0.12f))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text("TP${idx + 1}", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = Color(0xFF10B981))
                        }
                        Text(
                            text = formatPrice(tp.price),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "+${tp.percentage ?: tp.sizePct.takeIf { it > 0 } ?: 0}%",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF10B981)
                    )
                }
            }
        }
    }
}

private fun formatPrice(value: Double): String {
    val decimals = if (value < 1) 6 else if (value < 10) 4 else 2
    return "%.${decimals}f".format(value)
}

private fun formatTimestamp(timestamp: Long): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM, HH:mm").withZone(ZoneId.systemDefault())
    return formatter.format(Instant.ofEpochMilli(timestamp))
}
