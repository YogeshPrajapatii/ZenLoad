package com.example.zenload.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.OndemandVideo
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.zenload.R
import com.example.zenload.domain.model.MediaFormat
import com.example.zenload.presentation.components.GlassCard
import com.example.zenload.presentation.components.SegmentedControl
import com.example.zenload.presentation.viewmodels.HomeUiState
import com.example.zenload.presentation.viewmodels.HomeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    sharedLink: String = "",
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToDownloads: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var linkInput by remember { mutableStateOf(sharedLink) }
    var showBottomSheet by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState) {
        if (uiState is HomeUiState.Success) {
            showBottomSheet = true
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "ZenLoad",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 32.dp, elevation = 12.dp) {
                Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    OutlinedTextField(
                        value = linkInput,
                        onValueChange = { linkInput = it },
                        placeholder = { Text("Paste media link here", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) },
                        leadingIcon = { Icon(imageVector = Icons.Default.ContentPaste, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp)) },
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
                        onClick = { if (linkInput.isNotBlank()) viewModel.fetchVideoDetails(linkInput) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(50),
                        enabled = uiState !is HomeUiState.Loading,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = Color.White),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp, pressedElevation = 4.dp)
                    ) {
                        if (uiState is HomeUiState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text(text = "Fetch Formats", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        PlatformIcon(Icons.Rounded.OndemandVideo)
                        PlatformIcon(Icons.Rounded.PhotoCamera)
                        PlatformIcon(Icons.Rounded.Movie)
                        PlatformIcon(Icons.Rounded.MusicNote)
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            if (uiState !is HomeUiState.Loading && uiState !is HomeUiState.Success) TaglinePlaceholder()
        }

        if (showBottomSheet && uiState is HomeUiState.Success) {
            val data = uiState as HomeUiState.Success
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                    viewModel.resetState()
                },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface,
                scrimColor = Color.Black.copy(alpha = 0.5f),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                dragHandle = { Box(modifier = Modifier.padding(16.dp).width(40.dp).height(4.dp).clip(CircleShape).background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))) }
            ) {
                QualitySelectorContent(
                    title = data.title,
                    thumbnailUrl = data.thumbnailUrl,
                    videoFormats = data.videoFormats,
                    audioFormats = data.audioFormats,
                    onDownloadClicked = { format ->
                        viewModel.startDownload(linkInput, format.formatId, data.title)
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showBottomSheet = false
                            viewModel.resetState()
                            linkInput = ""
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun PlatformIcon(icon: ImageVector) {
    Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f), modifier = Modifier.size(24.dp))
}

@Composable
private fun TaglinePlaceholder() {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(painter = painterResource(id = R.drawable.ic_rocket), contentDescription = null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Ready to download your favorite media?", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onBackground, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Paste a link above and tap 'Fetch Formats' to start.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f), textAlign = TextAlign.Center)
    }
}

@Composable
private fun QualitySelectorContent(
    title: String,
    thumbnailUrl: String,
    videoFormats: List<MediaFormat>,
    audioFormats: List<MediaFormat>,
    onDownloadClicked: (MediaFormat) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }

    LazyColumn(modifier = Modifier.fillMaxWidth().navigationBarsPadding(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(model = thumbnailUrl, contentDescription = null, modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = title, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold, fontSize = 16.sp), color = MaterialTheme.colorScheme.onBackground, maxLines = 2)
                }
            }
        }
        item {
            SegmentedControl(items = listOf("Video", "Audio"), selectedIndex = selectedTab, onValueChange = { selectedTab = it }, modifier = Modifier.padding(horizontal = 24.dp))
        }
        val currentFormats = if (selectedTab == 0) videoFormats else audioFormats
        items(currentFormats) { format ->
            Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                FormatCard(resolution = format.resolution, format = format.extension.uppercase(), size = format.fileSize, onClick = { onDownloadClicked(format) })
            }
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun FormatCard(resolution: String, format: String, size: String, onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)).clickable { onClick() }.padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(text = "$resolution • $format", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onBackground)
                Text(text = size, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
            }
            Icon(painter = painterResource(id = R.drawable.ic_download), contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
        }
    }
}