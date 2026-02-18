package com.example.zenload.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.zenload.domain.model.MediaFormat
import com.example.zenload.presentation.navigation.Downloads
import com.example.zenload.presentation.viewmodels.DownloadUiState
import com.example.zenload.presentation.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    sharedLink: String = "",
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var urlText by remember { mutableStateOf(sharedLink) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("ZenLoad", fontWeight = FontWeight.ExtraBold) },
                actions = {
                    IconButton(onClick = { navController.navigate(Downloads) }) {
                        Icon(Icons.Default.List, contentDescription = "Downloads")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Download, null, modifier = Modifier.size(100.dp), tint = MaterialTheme.colorScheme.primary)

            Spacer(Modifier.height(40.dp))

            OutlinedTextField(
                value = urlText,
                onValueChange = { urlText = it },
                label = { Text("Paste Link Here") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                trailingIcon = {
                    if (urlText.isNotEmpty()) {
                        IconButton(onClick = { urlText = "" }) { Icon(Icons.Default.Clear, null) }
                    }
                }
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { if (urlText.isNotBlank()) viewModel.fetchVideoDetails(urlText) },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(20.dp),
                enabled = uiState !is DownloadUiState.Loading
            ) {
                if (uiState is DownloadUiState.Loading) {
                    CircularProgressIndicator(Modifier.size(24.dp), color = Color.White)
                    Text("  Analyzing...", fontSize = 18.sp)
                } else {
                    Text("Get Formats", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (uiState is DownloadUiState.Success) {
        ModalBottomSheet(onDismissRequest = { viewModel.resetState() }, sheetState = sheetState) {
            BottomSheetContent(uiState as DownloadUiState.Success) { format ->
                viewModel.startDownload(urlText, format.formatId, (uiState as DownloadUiState.Success).title)
                viewModel.resetState()
                navController.navigate(Downloads)
            }
        }
    }
}

@Composable
fun BottomSheetContent(state: DownloadUiState.Success, onSelect: (MediaFormat) -> Unit) {
    var tabIndex by remember { mutableIntStateOf(0) }
    Column(Modifier.fillMaxWidth().padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(state.thumbnailUrl, null, Modifier.size(90.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
            Spacer(Modifier.width(16.dp))
            Text(state.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 2)
        }

        Spacer(Modifier.height(20.dp))

        TabRow(selectedTabIndex = tabIndex) {
            Tab(tabIndex == 0, { tabIndex = 0 }, text = { Text("Video") })
            Tab(tabIndex == 1, { tabIndex = 1 }, text = { Text("Audio") })
        }

        val formats = if (tabIndex == 0) state.videoFormats else state.audioFormats
        LazyColumn(Modifier.fillMaxHeight(0.6f).padding(top = 10.dp)) {
            items(formats) { format ->
                Card(
                    Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable { onSelect(format) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(format.resolution, fontWeight = FontWeight.Bold)
                            Text(format.extension.uppercase(), fontSize = 12.sp)
                        }
                        Text(format.fileSize, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}