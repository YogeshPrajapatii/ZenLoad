package com.example.zenload.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.zenload.R
import com.example.zenload.presentation.components.GlassCard
import com.example.zenload.presentation.viewmodels.LibraryViewModel

@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val downloadedItems by viewModel.downloadedItems.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 24.dp, vertical = 24.dp)) {
            Text(text = "My Library", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(bottom = 32.dp))

            if (downloadedItems.isEmpty()) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(text = "Your downloaded media will appear here.\nStart downloading now!", style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    items(downloadedItems) { item ->
                        LibraryItemCard(title = item.title, res = item.resolution, size = item.sizeText, onClick = { /* Open File logic */ })
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
private fun LibraryItemCard(title: String, res: String, size: String, onClick: () -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 24.dp, elevation = 8.dp) {
        Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold, fontSize = 20.sp), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f), maxLines = 1)
                Text(text = res, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f), modifier = Modifier.padding(top = 4.dp))
                Text(text = "Downloaded • $size", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f), modifier = Modifier.padding(top = 10.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Surface(modifier = Modifier.size(width = 80.dp, height = 60.dp), shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(painter = painterResource(id = R.drawable.ic_download), contentDescription = "Downloaded", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}