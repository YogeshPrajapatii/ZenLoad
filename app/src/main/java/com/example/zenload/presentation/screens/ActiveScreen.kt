package com.example.zenload.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.zenload.presentation.components.DownloadProgressBar
import com.example.zenload.presentation.components.GlassCard
import com.example.zenload.presentation.viewmodels.ActiveViewModel

@Composable
fun ActiveScreen(
    viewModel: ActiveViewModel = hiltViewModel()
) {
    val downloadTasks by viewModel.activeDownloads.collectAsState(initial = emptyList())

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 24.dp, vertical = 24.dp)) {
            Text(text = "Active Downloads", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(bottom = 32.dp))

            if (downloadTasks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No active downloads", color = Color.Gray)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    items(downloadTasks, key = { it.id }) { task ->
                        ActiveDownloadCard(title = task.title, progress = task.progress)
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
private fun ActiveDownloadCard(title: String, progress: Float) {
    GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 24.dp, elevation = 8.dp) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold, fontSize = 20.sp), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f), maxLines = 1)
            Spacer(modifier = Modifier.height(16.dp))
            DownloadProgressBar(progress = progress)
            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.End) {
                Text(text = "${(progress * 100).toInt()}%", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}