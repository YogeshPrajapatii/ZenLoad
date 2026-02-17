// File: MainActivity.kt
package com.example.zenload

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.zenload.presentation.navigation.AppNavigation
import com.example.zenload.ui.theme.ZenLoadTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // Required by Hilt to inject dependencies
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Check if the app was opened via Android's Share menu
        var sharedUrl = ""
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            sharedUrl = intent.getStringExtra(Intent.EXTRA_TEXT) ?: ""
        }

        setContent {
            ZenLoadTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Initialize NavController and pass it to the NavGraph
                    val navController = rememberNavController()
                    AppNavigation(
                        navController = navController,
                        sharedLink = sharedUrl
                    )
                }
            }
        }
    }
}