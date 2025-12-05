package com.crypticsignals.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.crypticsignals.data.model.Direction
import com.crypticsignals.data.model.TakeProfit
import com.crypticsignals.ui.theme.LongGreen
import com.crypticsignals.ui.theme.ShortRed

@Composable
fun TakeProfitTimeline(
    takeProfits: List<TakeProfit>,
    direction: Direction,
    modifier: Modifier = Modifier
) {
    val accent = if (direction == Direction.LONG) LongGreen else ShortRed
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        takeProfits.sortedBy { it.level }.forEachIndexed { index, tp ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .background(color = accent, shape = CircleShape)
                    )
                    if (index != takeProfits.lastIndex) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(28.dp)
                                .background(color = accent.copy(alpha = 0.4f))
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Take profit ${tp.level}",
                        style = MaterialTheme.typography.titleMedium,
                        color = accent
                    )
                    Text(
                        text = "${tp.sizePct}% à ${formatPrice(tp.price)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

private fun formatPrice(value: Double): String {
    val decimals = if (value < 10) 4 else 2
    return "%.${decimals}f".format(value)
}
