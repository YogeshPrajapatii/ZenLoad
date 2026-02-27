package com.example.zenload.presentation.screens

import android.content.res.Configuration
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zenload.R
import com.example.zenload.presentation.components.GlassCard
import com.example.zenload.ui.theme.DarkGlassBorder
import com.example.zenload.ui.theme.LightBackground
import com.example.zenload.ui.theme.LightGlassBorder
import com.example.zenload.ui.theme.ZenLoadTheme

private data class DownloadedItemDummy(
    val id: String,
    val title: String,
    val resolution: String,
    val speedText: String,
    val sizeText: String,
    val thumbnailUrl: String
)

@Composable
fun LibraryScreen() {

    // Temporary Dummy State
    val downloadedItems = remember {
        mutableStateListOf(
            DownloadedItemDummy(
                "1",
                "Amazing Nature in 4K DASH DASH DASH (yt-dlp core core core)",
                "4K DASH DASH DASH DASH DASH DASH",
                "Downloaded • 1.2 GB",
                "Downloaded • 1.2 GB • DASH DASH",
                "DASH DASH DASH DASH DASH DASH DASH DASH DASH DASH DASH DASH"
            ),
            DownloadedItemDummy(
                "2",
                "Epic Space Journey - Part 1 DASH DASH DASH DASH (muxing streams)",
                "1080p DASH DASH DASH DASH DASH DASH DASH DASH DASH DASH DASH",
                "Downloaded • 250 MB",
                "Downloaded • 250 MB • DASH DASH",
                "DASH DASH DASH DASH DASH DASH DASH DASH DASH DASH DASH DASH"
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
                text = "My Library",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            if (downloadedItems.isEmpty()) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Your downloaded media will appear here.\nStart downloading now!",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(downloadedItems) { item ->
                        LibraryItemCard(
                            item = item,
                            onClick = { /* TODO: viewModel.openFile(item.id) */ },
                            modifier = Modifier.padding(horizontal = 4.dp) // Subtle gap from screen edges
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun LibraryItemCard(
    item: DownloadedItemDummy,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        elevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Text Column with specific design constraints
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold, fontSize = 20.sp),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f),
                    maxLines = 1, // Replicating design
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = item.resolution,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    maxLines = 1,
                    modifier = Modifier.padding(top = 4.dp).fillMaxWidth()
                )

                Text(
                    text = item.speedText,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Normal),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    maxLines = 1,
                    modifier = Modifier.padding(top = 10.dp).fillMaxWidth()
                )

                Text(
                    text = item.sizeText,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    maxLines = 1,
                    modifier = Modifier.padding(top = 4.dp).fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Special Colored Thumbnail Placeholder Box
            Surface(
                modifier = Modifier.size(width = 80.dp, height = 60.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) // subtle glowing background
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_download),
                        contentDescription = "Downloaded",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
private fun LibraryScreenPreviewLight() {
    ZenLoadTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            LibraryScreen()
        }
    }
}

@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LibraryScreenPreviewDark() {
    ZenLoadTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            LibraryScreen()
        }
    }
}

@Preview(name = "Light Mode BS", showBackground = true)
@Composable
private fun LibraryScreenEmptyPreviewLight() {
    ZenLoadTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            LibraryScreen() // Using empty logic implicitly by not passing data (DUMMY LIST ISremember-mutable-state based)
        }
    }
}