package com.example.zenload.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.zenload.ui.theme.DarkGlassBorder
import com.example.zenload.ui.theme.LightBackground
import com.example.zenload.ui.theme.LightGlassBorder

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp, // Match your design's rounded corners
    elevation: Dp = 8.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val isLight = MaterialTheme.colorScheme.background.toArgb() == LightBackground.toArgb()
    val borderColor = if (isLight) LightGlassBorder else DarkGlassBorder

    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), // Soft glowing shadow
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(MaterialTheme.colorScheme.surfaceVariant) // Uses the translucent color from theme
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(cornerRadius)
            )
    ) {
        content()
    }
}