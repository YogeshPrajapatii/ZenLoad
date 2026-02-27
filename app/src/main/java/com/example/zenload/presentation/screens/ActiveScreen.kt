package com.example.zenload.presentation.screens

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.NativePaint
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.zenload.presentation.components.DownloadControlButton
import com.example.zenload.presentation.components.GlassCard
import com.example.zenload.ui.theme.ZenLoadTheme

data class DownloadTask(
    val id: String,
    val title: String,
    val resolution: String,
    val speedText: String,
    val sizeText: String,
    val thumbnailUrl: String,
    val progress: Float,
    val isPaused: Boolean = false
)

@Composable
fun PixelPerfectProgressBar(
    progress: Float, color: Color = Color(0xFF8A2BE2), modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(10.dp)
    ) {
        val cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx())

        drawRoundRect(
            color = color.copy(alpha = 0.1f), size = size, cornerRadius = cornerRadius
        )

        val activeWidth = size.width * progress

        drawIntoCanvas { canvas ->
            val paint = Paint().asFrameworkPaint().apply {
                this.color = color.toArgb()
                setShadowLayer(30f, 0f, 0f, color.toArgb())
            }
            canvas.nativeCanvas.drawRoundRect(
                0f, 0f, activeWidth, size.height, cornerRadius.x, cornerRadius.y, paint
            )
        }
    }
}

@Composable
fun ActiveScreen(
    onNavigateToDownloads: () -> Unit = { }
) {
    val downloadTasks = remember {
        mutableStateListOf(
            DownloadTask(
                "1",
                "Amazing Nature...",
                "4K DASH (yt-dlp core)",
                "0:34 / 08:34 (5.5 MB/s)",
                "890 MB / 1.2 GB",
                "https://dummyimage.com/120x90/bbbbbb/ffffff.png&text=Thumbnail",
                0.65f
            ), DownloadTask(
                "2",
                "Epic Space Journey - Part 1",
                "1080p DASH (muxing streams)",
                "1:12 / 04:30 (4.2 MB/s)",
                "250 MB / 500 MB",
                "https://dummyimage.com/120x90/bbbbbb/ffffff.png&text=Thumbnail",
                0.22f,
                isPaused = true
            ), DownloadTask(
                "3",
                "Podcast Episode 4",
                "High Quality MP3 conversion",
                "0:45 / 15:00 (1.1 MB/s)",
                "10 MB / 80 MB",
                "https://dummyimage.com/120x90/bbbbbb/ffffff.png&text=Thumbnail",
                0.9f
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Text(
                text = "Active Downloads",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(downloadTasks) { task ->
                    ActiveDownloadCard(task = task)
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun ActiveDownloadCard(
    task: DownloadTask
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(), cornerRadius = 24.dp, elevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold, fontSize = 20.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f),
                    maxLines = 1
                )

                Text(
                    text = task.resolution,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    text = task.speedText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 8.dp)
                )

                Text(
                    text = task.sizeText,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 2.dp)
                )

                PixelPerfectProgressBar(
                    progress = task.progress, modifier = Modifier.padding(top = 16.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            AsyncImage(
                model = task.thumbnailUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(width = 80.dp, height = 60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
            )
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
private fun ActiveScreenPreviewLight() {
    ZenLoadTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            ActiveScreen()
        }
    }
}

@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ActiveScreenPreviewDark() {
    ZenLoadTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            ActiveScreen()
        }
    }
}