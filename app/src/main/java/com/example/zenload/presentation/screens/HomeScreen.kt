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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.OndemandVideo
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.zenload.R
import com.example.zenload.presentation.components.GlassCard
import com.example.zenload.presentation.components.SegmentedControl
import com.example.zenload.ui.theme.ZenLoadTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    sharedLink: String = "",
    onNavigateToDownloads: () -> Unit
) {
    var linkInput by remember { mutableStateOf(sharedLink) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var formatsFetched by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "ZenLoad",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 32.dp,
                elevation = 12.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = linkInput,
                        onValueChange = { linkInput = it },
                        placeholder = {
                            Text(
                                "Paste media link here",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.ContentPaste,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(50),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f),
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                            cursorColor = MaterialTheme.colorScheme.primary,
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            formatsFetched = true
                            showBottomSheet = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp, pressedElevation = 4.dp)
                    ) {
                        Text(
                            text = "Fetch Formats",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PlatformIcon(Icons.Rounded.OndemandVideo)
                        PlatformIcon(Icons.Rounded.PhotoCamera)
                        PlatformIcon(Icons.Rounded.Movie)
                        PlatformIcon(Icons.Rounded.MusicNote)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (!formatsFetched) {
                TaglinePlaceholder()
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface,
                scrimColor = Color.Black.copy(alpha = 0.5f),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                dragHandle = { Box(modifier = Modifier.padding(16.dp).width(40.dp).height(4.dp).clip(CircleShape).background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))) }
            ) {
                QualitySelectorContent {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) showBottomSheet = false
                    }
                }
            }
        }
    }
}

@Composable
private fun PlatformIcon(icon: ImageVector) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
        modifier = Modifier.size(24.dp)
    )
}

@Composable
private fun TaglinePlaceholder() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_rocket),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Ready to download your favorite media?",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Paste a link above and tap 'Fetch Formats' to start.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun QualitySelectorContent(onDownloadClicked: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }

    LazyColumn(
        modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.media_thumbnail),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp))
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Amazing Nature in 4K",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "YouTube • 1.2 GB • 08:34",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }
        }

        item {
            SegmentedControl(
                items = listOf("Video", "Audio"),
                selectedIndex = selectedTab,
                onValueChange = { selectedTab = it },
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }

        item {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 24.dp)) {
                if (selectedTab == 0) {
                    FormatCard(resolution = "1080p", format = "MP4", size = "45 MB", onDownloadClicked)
                    FormatCard(resolution = "720p", format = "MP4", size = "22 MB", onDownloadClicked)
                    FormatCard(resolution = "480p", format = "MP4", size = "12 MB", onDownloadClicked)
                } else {
                    FormatCard(resolution = "320kbps", format = "MP3", size = "8 MB", onDownloadClicked)
                    FormatCard(resolution = "128kbps", format = "MP3", size = "3 MB", onDownloadClicked)
                }
            }
        }
    }
}

@Composable
private fun FormatCard(resolution: String, format: String, size: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "$resolution • $format",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = size,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_download),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
private fun HomeScreenPreviewLight() {
    ZenLoadTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            HomeScreen(onNavigateToDownloads = {})
        }
    }
}

@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeScreenPreviewDark() {
    ZenLoadTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            HomeScreen(onNavigateToDownloads = {})
        }
    }
}

@Preview(name = "Light Mode BS", showBackground = true)
@Composable
private fun HomeScreenBSPreviewLight() {
    ZenLoadTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.surface) {
            QualitySelectorContent {}
        }
    }
}

@Preview(name = "Dark Mode BS", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeScreenBSPreviewDark() {
    ZenLoadTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.surface) {
            QualitySelectorContent {}
        }
    }
}