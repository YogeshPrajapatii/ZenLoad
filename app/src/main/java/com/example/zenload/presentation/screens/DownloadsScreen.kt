package com.example.zenload.presentation.screens

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.zenload.presentation.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsScreen(navController: NavController, viewModel: MainViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val workInfos by WorkManager.getInstance(context)
        .getWorkInfosByTagFlow("all_downloads")
        .collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Downloads", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        if (workInfos.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Your download list is empty", color = Color.Gray)
            }
        } else {
            LazyColumn(Modifier.padding(padding).padding(16.dp)) {
                items(workInfos) { info ->
                    DownloadCard(info, viewModel)
                }
            }
        }
    }
}

@Composable
fun DownloadCard(info: WorkInfo, viewModel: MainViewModel) {
    val progress = info.progress.getInt("PROGRESS", 0)
    val title = info.progress.getString("TITLE") ?: "Fetching Title..."
    val id = info.tags.firstOrNull { it != "all_downloads" && !it.contains(".") } ?: "Task"

    Card(
        Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, maxLines = 1)
            Text("ID: $id", fontSize = 11.sp, color = Color.Gray)

            Spacer(Modifier.height(12.dp))

            if (!info.state.isFinished) {
                LinearProgressIndicator(
                    progress = { progress / 100f },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("$progress%", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = { viewModel.cancelDownload(id) }) {
                        Icon(Icons.Default.Cancel, null, tint = Color.Red)
                    }
                }
            } else {
                val color = if (info.state == WorkInfo.State.SUCCEEDED) Color(0xFF4CAF50) else Color.Red
                Text("Status: ${info.state.name}", color = color, fontWeight = FontWeight.Bold)
            }
        }
    }
}