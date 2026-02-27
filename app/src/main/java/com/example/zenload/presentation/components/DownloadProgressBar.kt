package com.example.zenload.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

@Composable
fun DownloadProgressBar(
    progress: Float, // 0f to 1f
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        label = "download_progress_anim"
    )

    LinearProgressIndicator(
        progress = { animatedProgress },
        modifier = modifier
            .fillMaxWidth()
            .height(10.dp) // Modern, thicker indicator
            .clip(MaterialTheme.shapes.extraSmall), // Pill shape ends
        color = MaterialTheme.colorScheme.primary, // Premium color from theme
        trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
        strokeCap = StrokeCap.Round
    )
}