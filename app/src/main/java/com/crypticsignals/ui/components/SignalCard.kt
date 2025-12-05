package com.crypticsignals.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.crypticsignals.data.model.Signal
import com.crypticsignals.data.model.SignalStatus
import com.crypticsignals.ui.theme.CardSurface
import com.crypticsignals.ui.theme.LongGreen
import com.crypticsignals.ui.theme.ShortRed

@Composable
fun SignalCard(
    signal: Signal,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val statusColor = if (signal.status == SignalStatus.ACTIVE) LongGreen else ShortRed
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardSurface.copy(alpha = 0.92f)),
        shape = RoundedCornerShape(18.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                )
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(
                        brush = Brush.verticalGradient(
                            if (signal.status == SignalStatus.ACTIVE)
                                listOf(statusColor, MaterialTheme.colorScheme.primary)
                            else listOf(statusColor, MaterialTheme.colorScheme.tertiary)
                        ),
                        shape = RoundedCornerShape(topStart = 18.dp, bottomStart = 18.dp)
                    )
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = signal.pair,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${signal.timeframe} • ${signal.entryType.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    TagChip(
                        text = signal.status.name.lowercase().replaceFirstChar { it.uppercase() },
                        color = statusColor.copy(alpha = 0.25f),
                        contentColor = statusColor
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PriceColumn(title = "Entrée", value = signal.entryPrice)
                    PriceColumn(title = "Stop", value = signal.stopLoss)
                    ConfidencePill(confidence = signal.confidence)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    signal.takeProfits.take(3).forEach { tp ->
                        TagChip(
                            text = buildString {
                                append("TP${tp.level} @ ${formatPrice(tp.price)}")
                                tp.percentage?.let { pct -> append(" (${pct}%)") }
                                if (tp.sizePct != 0) append(" (${tp.sizePct}%)")
                            },
                            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.18f),
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ConfidenceBar(level = signal.confidence, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ConfidencePill(confidence: Int) {
    val percent = confidence * 10
    val color = when {
        percent >= 85 -> LongGreen
        percent >= 70 -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.outline
    }
    TagChip(
        text = "$percent% conf.",
        color = color.copy(alpha = 0.2f),
        contentColor = color
    )
}

@Composable
private fun PriceColumn(title: String, value: Double) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = formatPrice(value),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun formatPrice(value: Double): String {
    val decimals = if (value < 10) 4 else 2
    return "%.${decimals}f".format(value)
}
