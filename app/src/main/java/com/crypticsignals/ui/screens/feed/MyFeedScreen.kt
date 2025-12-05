package com.crypticsignals.ui.screens.feed

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.crypticsignals.data.mock.MockData
import com.crypticsignals.data.model.Signal
import com.crypticsignals.data.model.SignalStatus
import com.crypticsignals.data.model.Trader
import com.crypticsignals.ui.components.DirectionBadge
import com.crypticsignals.ui.components.GlassCard
import com.crypticsignals.ui.components.StatusBadge

@Composable
fun MyFeedScreen(
    traders: List<Trader>,
    signals: List<Signal>,
    onSignalClick: (String) -> Unit,
    onMarketClick: () -> Unit,
    onTraderClick: (String) -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val allSignals = if (signals.isEmpty()) MockData.signals else signals
    val traderMap = (if (traders.isEmpty()) MockData.traders else traders).associateBy { it.id }

    val active = allSignals.filter { it.status == SignalStatus.ACTIVE }
    val expired = allSignals.filter { it.status != SignalStatus.ACTIVE }
    val grouped = (if (active.isNotEmpty()) active else expired).groupBy { it.traderId }
    val showingExpired = active.isEmpty()

    val entries = grouped.entries.mapNotNull { (id, sigs) ->
        traderMap[id]?.let { trader ->
            TraderEntry(
                id = id,
                name = trader.name,
                avatarUrl = trader.avatarUrl,
                signals = sigs
            )
        }
    }.sortedBy { it.name }

    if (entries.isEmpty()) {
        EmptyState(onMarketClick)
        return
    }

    val bottomInset = contentPadding.calculateBottomPadding()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = bottomInset + 40.dp)
    ) {
        item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ShingoGlyph(size = 24.dp, color = Color.White)
                    Text(
                        if (showingExpired) "Recent Signals" else "Live Feed",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                }
                Text(
                    text = "${grouped.values.sumOf { it.size }} signals",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }

        items(entries.size) { index ->
            val entry = entries[index]
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTraderClick(entry.id) }
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TraderAvatar(name = entry.name, avatarUrl = entry.avatarUrl)
                        Column {
                            Text(entry.name, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                            Text(
                                "${entry.signals.size} ${if (entry.signals.size > 1) "signals" else "signal"}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Outlined.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        entry.signals.forEach { signal ->
                            SignalRow(signal = signal, onSignalClick = onSignalClick)
                        }
                    }
                }
            }
        }
    }
}

private data class TraderEntry(
    val id: String,
    val name: String,
    val avatarUrl: String?,
    val signals: List<Signal>
)

@Composable
private fun TraderAvatar(name: String, avatarUrl: String?) {
    val fallback = remember(name) { name.take(2).uppercase() }
    Box(
        modifier = Modifier
            .height(44.dp)
            .width(44.dp)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (avatarUrl != null) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = name,
                modifier = Modifier
                    .height(44.dp)
                    .width(44.dp)
                    .background(Color.Transparent, RoundedCornerShape(12.dp))
            )
        } else {
            Text(
                text = fallback,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun SignalRow(signal: Signal, onSignalClick: (String) -> Unit) {
    GlassCard(onClick = { onSignalClick(signal.id) }) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(signal.pair, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            signal.timeframe,
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                        StatusBadge(status = signal.status)
                    }
                }
                DirectionBadge(direction = signal.direction)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text("Entry", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Text(signal.entryPrice.toString(), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Target", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Text(
                        signal.takeProfits.lastOrNull()?.price?.toString().orEmpty(),
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF10B981)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState(onMarketClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .height(80.dp)
                .width(80.dp)
                .background(
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f),
                    shape = MaterialTheme.shapes.extraLarge
                ),
            contentAlignment = Alignment.Center
        ) {
        ShingoGlyph(size = 40.dp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("No active subscriptions", color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.titleMedium)
        Text(
            "Subscribe to traders to see their real-time signals here.",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
    Button(
        onClick = onMarketClick,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = Color(0xFF021012))
    ) { Text("Go to Marketplace") }
    }
}

@Composable
fun ShingoGlyph(size: androidx.compose.ui.unit.Dp = 24.dp, color: Color = Color.White) {
    val neon = color
    Canvas(
        modifier = Modifier
            .height(size)
            .width(size)
    ) {
        val dim = size.toPx()
        val rOuter = dim * 0.083f
        val rCenter = dim * 0.138f
        val cx = this.size.width / 2f
        val cy = this.size.height / 2f
        val stroke = 2.dp.toPx()

        drawCircle(color = neon.copy(alpha = 0.9f), radius = rOuter, center = Offset(cx, rOuter * 3f))
        drawCircle(color = neon.copy(alpha = 0.9f), radius = rOuter, center = Offset(this.size.width - rOuter * 3f, cy))
        drawCircle(color = neon.copy(alpha = 0.9f), radius = rOuter, center = Offset(cx, this.size.height - rOuter * 3f))
        drawCircle(color = neon.copy(alpha = 0.9f), radius = rOuter, center = Offset(rOuter * 3f, cy))

        drawCircle(color = neon, radius = rCenter, center = Offset(cx, cy))

        drawLine(neon.copy(alpha = 0.3f), Offset(cx, rOuter * 3f), Offset(cx, cy), strokeWidth = stroke)
        drawLine(neon.copy(alpha = 0.3f), Offset(this.size.width - rOuter * 3f, cy), Offset(cx, cy), strokeWidth = stroke)
        drawLine(neon.copy(alpha = 0.3f), Offset(cx, this.size.height - rOuter * 3f), Offset(cx, cy), strokeWidth = stroke)
        drawLine(neon.copy(alpha = 0.3f), Offset(rOuter * 3f, cy), Offset(cx, cy), strokeWidth = stroke)

        // arc flourish
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(cx + rCenter, cy - rCenter * 2)
            cubicTo(cx + rCenter * 3, cy - rCenter * 1.5f, cx + rCenter * 3, cy + rCenter * 0.5f, cx + rCenter, cy + rCenter * 2)
        }
        drawPath(path, color = neon.copy(alpha = 0.6f), style = androidx.compose.ui.graphics.drawscope.Stroke(stroke, cap = androidx.compose.ui.graphics.StrokeCap.Round))
    }
}
