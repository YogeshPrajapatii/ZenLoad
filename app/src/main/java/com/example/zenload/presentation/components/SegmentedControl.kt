package com.example.zenload.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SegmentedControl(
    items: List<String>,
    selectedIndex: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
            .padding(4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, item ->
            val isSelected = index == selectedIndex

            val backgroundAnimateColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                label = "seg_bg_color"
            )
            val textAnimateColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                label = "seg_text_color"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(CircleShape)
                    .background(backgroundAnimateColor)
                    .clickable { onValueChange(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item,
                    color = textAnimateColor,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}