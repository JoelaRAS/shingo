package com.crypticsignals.ui.screens.traders

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.crypticsignals.data.model.Signal
import com.crypticsignals.data.model.SignalStatus
import com.crypticsignals.data.model.Trader
import com.crypticsignals.ui.components.DirectionBadge
import com.crypticsignals.ui.components.GlassCard
import com.crypticsignals.ui.components.NeonBadge
import com.crypticsignals.ui.components.StatusBadge

@Composable
fun TraderDetailScreen(
    trader: Trader?,
    signals: List<Signal>,
    onSignalClick: (String) -> Unit,
    onSubscribeClick: () -> Unit
) {
    val activeSignals = signals.filter { it.status == SignalStatus.ACTIVE }
    val history = signals.filter { it.status == SignalStatus.EXPIRED }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 100.dp)
    ) {
        item { TraderHero(trader = trader, onSubscribeClick = onSubscribeClick) }
        item { PerformanceSection(trader = trader, history = history) }
        item { LiveSignalsSection(trader = trader, signals = activeSignals, onSignalClick = onSignalClick) }
        item { HistorySection(history = history, onSignalClick = onSignalClick) }
        item { Spacer(modifier = Modifier.height(28.dp)) }
    }
}

@Composable
private fun TraderHero(trader: Trader?, onSubscribeClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = trader?.avatarUrl,
                contentDescription = trader?.name,
                modifier = Modifier
                    .height(80.dp)
                    .width(80.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = trader?.name ?: "Trader", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    NeonBadge(text = "${trader?.rating ?: 0.0} ★")
                    NeonBadge(text = "${trader?.reviewsCount ?: 0} reviews", color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
        Text(
            text = trader?.bio.orEmpty(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
        )
        Button(
            onClick = onSubscribeClick,
            modifier = Modifier.fillMaxWidth(),
            colors = if (trader?.isUserSubscribed == true) {
                ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF10B981).copy(alpha = 0.16f),
                    contentColor = Color(0xFF10B981)
                )
            } else {
                ButtonDefaults.buttonColors()
            }
        ) {
            Text(
                text = if (trader?.isUserSubscribed == true) "Active Subscriber" else "Subscribe for $${trader?.price ?: 0}/mo",
                fontWeight = FontWeight.Bold
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            MetricCard(title = "Win Rate", value = "${trader?.winRate ?: 0}%")
            MetricCard(title = "Total Calls", value = "${trader?.totalSignals ?: 0}")
            MetricCard(title = "Subs", value = "${trader?.activeSubscribers ?: 0}")
        }
    }
}

@Composable
private fun PerformanceSection(trader: Trader?, history: List<Signal>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Performance",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            NeonBadge(text = if ((trader?.roi ?: 0) >= 0) "+${trader?.roi ?: 0}%" else "${trader?.roi ?: 0}%")
        }
        GlassCard {
            PerformanceChart(history = history)
        }
    }
}

@Composable
private fun LiveSignalsSection(
    trader: Trader?,
    signals: List<Signal>,
    onSignalClick: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Live Signals",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        if (signals.isEmpty()) {
            Text(
                text = "No active positions currently.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        } else {
            signals.forEach { signal ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    GlassCard(
                        onClick = if (trader?.isUserSubscribed == true) { { onSignalClick(signal.id) } } else null
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = signal.pair,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                StatusBadge(status = signal.status)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                DirectionBadge(direction = signal.direction)
                                Text(
                                    text = signal.timeframe,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                    if (trader?.isUserSubscribed == false) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.65f), RoundedCornerShape(18.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                androidx.compose.material3.Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                                Text(
                                    "Subscribe to unlock",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistorySection(history: List<Signal>, onSignalClick: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "History (${history.size})",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        history.take(20).forEach { signal ->
            GlassCard(onClick = { onSignalClick(signal.id) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        DirectionBadge(direction = signal.direction)
                        Column {
                            Text(
                                text = signal.pair,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = signal.timeframe,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                    Text(
                        text = "${if ((signal.resultPnl ?: 0.0) >= 0) "+" else ""}${signal.resultPnl ?: 0.0}%",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = if ((signal.resultPnl ?: 0.0) >= 0) Color(0xFF10B981) else Color(0xFFef4444)
                    )
                }
            }
        }
    }
}

@Composable
private fun PerformanceChart(history: List<Signal>) {
    val points = remember(history) {
        val sorted = history.sortedBy { it.createdAt }
        val accum = mutableListOf(0f)
        var current = 0f
        sorted.forEach { s ->
            current += (s.resultPnl ?: 0.0).toFloat()
            accum.add(current)
        }
        if (accum.size < 2) listOf(0f, 0f) else accum
    }
    val min = points.minOrNull() ?: 0f
    val max = points.maxOrNull() ?: 1f
    val range = (max - min).takeIf { it != 0f } ?: 1f

    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(180.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.4f))
    ) {
        val stepX = size.width / (points.size - 1)
        val mapY: (Float) -> Float = { value ->
            val normalized = (value - min) / range
            size.height * (1f - normalized * 0.8f) - 12f
        }

        val linePath = Path()
        points.forEachIndexed { index, value ->
            val x = stepX * index
            val y = mapY(value)
            if (index == 0) {
                linePath.moveTo(x, y)
            } else {
                linePath.lineTo(x, y)
            }
        }

        val areaPath = Path().apply {
            addPath(linePath)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }

        drawPath(
            path = areaPath,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF10B981).copy(alpha = 0.35f), Color.Transparent)
            ),
            style = Fill
        )
        drawPath(
            path = linePath,
            color = Color(0xFF10B981),
            style = Stroke(width = 4f, cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun MetricCard(title: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
        Text(text = title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
    }
}
