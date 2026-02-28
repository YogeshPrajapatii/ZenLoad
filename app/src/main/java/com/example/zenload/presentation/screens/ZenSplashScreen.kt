package com.example.zenload.presentation.screens

import android.content.res.Configuration
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.zenload.R
import com.example.zenload.ui.theme.ZenLoadTheme
import kotlinx.coroutines.delay

@Composable
fun ZenSplashScreen(
    onSplashComplete: () -> Unit = { }
) {
    val scale = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1000,
                easing = LinearEasing
            )
        )
        delay(2000)
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.zenload_app_logo),
            contentDescription = null,
            modifier = Modifier
                .size(width = 160.dp, height = 160.dp) // Maintain aspect ratio
                .scale(scale.value)
        )
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
private fun ZenSplashScreenPreviewLight() {
    ZenLoadTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            ZenSplashScreen()
        }
    }
}

@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ZenSplashScreenPreviewDark() {
    ZenLoadTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            ZenSplashScreen()
        }
    }
}