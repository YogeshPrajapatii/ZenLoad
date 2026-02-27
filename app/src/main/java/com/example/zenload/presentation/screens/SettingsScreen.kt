package com.example.zenload.presentation.screens

import android.content.res.Configuration
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zenload.presentation.components.GlassCard
import com.example.zenload.ui.theme.ZenLoadTheme

@Composable
fun SettingsScreen(
    isDarkMode: Boolean,
    onDarkModeChanged: (Boolean) -> Unit,
    onNavigateToDownloads: () -> Unit = { }
) {
    var downloadPath by remember { mutableStateOf("/storage/emulated/0/Download/ZenLoad") }
    var parallelDownloads by remember { mutableIntStateOf(3) }
    var maxRetries by remember { mutableFloatStateOf(5f) }
    var engineVersion by remember { mutableStateOf("yt-dlp v2023.11.16") }

    val scrollState = rememberScrollState()

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
                .verticalScroll(scrollState)
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            SettingsSectionHeader(title = "General")
            GlassCard(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 32.dp),
                cornerRadius = 32.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(text = "Download Path", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = downloadPath,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(50),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f),
                            )
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = "Select Path",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(32.dp)
                                .clickable { /* TODO: Open Folder Picker */ }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    SettingsRowWithPillInput(
                        title = "Parallel Downloads",
                        value = parallelDownloads.toString(),
                        onValueUp = { if (parallelDownloads < 5) parallelDownloads++ },
                        onValueDown = { if (parallelDownloads > 1) parallelDownloads-- }
                    )
                }
            }

            SettingsSectionHeader(title = "Advanced")
            GlassCard(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 32.dp),
                cornerRadius = 32.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(text = "Maximum Retries", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Slider(
                            value = maxRetries,
                            onValueChange = { maxRetries = it },
                            valueRange = 1f..10f,
                            steps = 8,
                            modifier = Modifier.weight(1f),
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary,
                                inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                            )
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = maxRetries.toInt().toString(),
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.width(32.dp),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }

            SettingsSectionHeader(title = "Appearance")
            GlassCard(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 32.dp),
                cornerRadius = 32.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(text = "Theme", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Dark Mode", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f))
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { onDarkModeChanged(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.surface,
                                checkedTrackColor = MaterialTheme.colorScheme.primary,
                                uncheckedThumbColor = MaterialTheme.colorScheme.onSurface,
                                uncheckedTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                            )
                        )
                    }
                }
            }

            SettingsSectionHeader(title = "Engine")
            GlassCard(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 32.dp),
                cornerRadius = 32.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Engine Version", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
                        Text(text = engineVersion, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                        onClick = { /* TODO: viewModel.reinitializeEngine() */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = "Re-initialize Engine",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun SettingsRowWithPillInput(
    title: String,
    value: String,
    onValueUp: () -> Unit,
    onValueDown: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .width(120.dp)
                .height(48.dp)
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), RoundedCornerShape(50))
                .padding(horizontal = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Decrease",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp).clickable { onValueDown() }
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Icon(
                imageVector = Icons.Default.ArrowDropUp,
                contentDescription = "Increase",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp).clickable { onValueUp() }
            )
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
private fun SettingsScreenPreviewLight() {
    ZenLoadTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            SettingsScreen(isDarkMode = false, onDarkModeChanged = {})
        }
    }
}

@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SettingsScreenPreviewDark() {
    ZenLoadTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            SettingsScreen(isDarkMode = true, onDarkModeChanged = {})
        }
    }
}