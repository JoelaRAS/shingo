package com.crypticsignals.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.crypticsignals.data.model.Direction
import com.crypticsignals.ui.theme.LongGreen
import com.crypticsignals.ui.theme.ShortRed

@Composable
fun DirectionLabel(
    direction: Direction,
    modifier: Modifier = Modifier
) {
    val color = if (direction == Direction.LONG) LongGreen else ShortRed
    val icon = if (direction == Direction.LONG) Icons.Default.ArrowCircleUp else Icons.Default.ArrowCircleDown
    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Icon(icon, contentDescription = null, tint = color)
            Text(
                text = direction.name,
                style = MaterialTheme.typography.labelLarge,
                color = color
            )
        }
    }
}
