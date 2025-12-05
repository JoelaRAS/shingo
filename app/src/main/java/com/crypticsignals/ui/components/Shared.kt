package com.crypticsignals.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.crypticsignals.data.model.Direction
import com.crypticsignals.data.model.SignalStatus
import com.crypticsignals.ui.theme.LongGreen
import com.crypticsignals.ui.theme.PrimaryBlue
import com.crypticsignals.ui.theme.ShortRed

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                .padding(14.dp)
        ) {
            content()
        }
    }
}

@Composable
fun NeonBadge(
    text: String,
    color: Color = PrimaryBlue
) {
    Text(
        text = text,
        color = color,
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    )
}

@Composable
fun StatusBadge(status: SignalStatus) {
    val isExpired = status == SignalStatus.EXPIRED
    val color = if (isExpired) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else PrimaryBlue
    val icon: ImageVector = if (isExpired) Icons.Outlined.LockOpen else Icons.Outlined.Lock
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = if (isExpired) 0.05f else 0.1f))
            .border(1.dp, color.copy(alpha = 0.12f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Icon(icon, contentDescription = null, tint = color)
        Text(
            text = status.name,
            color = color,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(start = 6.dp)
        )
    }
}

@Composable
fun DirectionBadge(direction: Direction) {
    val isLong = direction == Direction.LONG
    val color = if (isLong) LongGreen else ShortRed
    val icon: ImageVector = if (isLong) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.08f))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Icon(icon, contentDescription = null, tint = color)
        Text(
            text = direction.name,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = color
        )
    }
}

@Composable
fun ConfidenceBar(level: Int, modifier: Modifier = Modifier) {
    val capped = level.coerceIn(0, 10)
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Confidence",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = "$capped/10",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
            repeat(10) { idx ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (idx < capped) {
                                if (capped > 7) LongGreen else PrimaryBlue
                            } else MaterialTheme.colorScheme.surfaceVariant
                        )
                )
            }
        }
    }
}

@Composable
fun ConfidenceBarDots(confidence: Int, modifier: Modifier = Modifier) {
    ConfidenceBar(level = confidence.coerceIn(0, 10), modifier = modifier)
}
