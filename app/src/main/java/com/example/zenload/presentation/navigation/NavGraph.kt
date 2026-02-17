// File: presentation/navigation/NavGraph.kt
package com.example.zenload.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.zenload.presentation.screens.DownloadsScreen
import com.example.zenload.presentation.screens.HomeScreen

// Manages the flow between all screens
@Composable
fun AppNavigation(
    navController: NavHostController,
    sharedLink: String = ""
) {
    NavHost(
        navController = navController,
        startDestination = Home
    ) {
        // Defines the Home Screen route
        composable<Home> {
            HomeScreen(
                navController = navController,
                sharedLink = sharedLink
            )
        }

        // Defines the Downloads Screen route
        composable<Downloads> {
            DownloadsScreen(
                navController = navController
            )
        }
    }
}