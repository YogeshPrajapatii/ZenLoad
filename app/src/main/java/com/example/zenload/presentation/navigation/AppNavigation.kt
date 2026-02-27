package com.example.zenload.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.zenload.presentation.screens.ActiveScreen
import com.example.zenload.presentation.screens.HomeScreen
import com.example.zenload.presentation.screens.LibraryScreen
import com.example.zenload.presentation.screens.SettingsScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    sharedLink: String = "",
    isDarkMode: Boolean,
    onDarkModeChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier
    ) {
        composable<HomeRoute> {
            HomeScreen(
                sharedLink = sharedLink,
                onNavigateToDownloads = { }
            )
        }

        composable<ActiveRoute> {
            ActiveScreen()
        }

        composable<LibraryRoute> {
            LibraryScreen()
        }

        composable<SettingsRoute> {
            SettingsScreen(
                isDarkMode = isDarkMode,
                onDarkModeChanged = onDarkModeChanged
            )
        }
    }
}