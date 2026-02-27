package com.example.zenload.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.zenload.presentation.navigation.ActiveRoute
import com.example.zenload.presentation.navigation.HomeRoute
import com.example.zenload.presentation.navigation.LibraryRoute
import com.example.zenload.presentation.navigation.SettingsRoute

data class BottomNavItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: Any
)

object BottomTabs {
    val items = listOf(
        BottomNavItem("Home", Icons.Filled.Home, Icons.Outlined.Home, HomeRoute),
        BottomNavItem("Active", Icons.Filled.Download, Icons.Outlined.Download, ActiveRoute),
        BottomNavItem("Library", Icons.Filled.Folder, Icons.Outlined.Folder, LibraryRoute),
        BottomNavItem("Settings", Icons.Filled.Settings, Icons.Outlined.Settings, SettingsRoute)
    )
}