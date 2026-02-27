package com.example.zenload

import android.app.Application
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDL
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class ZenLoadApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize engine once globally on startup to prevent multi-threading locks
        CoroutineScope(Dispatchers.IO).launch {
            try {
                YoutubeDL.getInstance().init(this@ZenLoadApp)
                FFmpeg.getInstance().init(this@ZenLoadApp)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}