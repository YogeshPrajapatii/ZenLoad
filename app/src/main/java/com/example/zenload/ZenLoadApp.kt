package com.example.zenload

import android.app.Application
import android.util.Log
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

        // Start and update engine in background to bypass YouTube blocks
        CoroutineScope(Dispatchers.IO).launch {
            try {
                YoutubeDL.getInstance().init(this@ZenLoadApp)
                FFmpeg.getInstance().init(this@ZenLoadApp)
                YoutubeDL.getInstance().updateYoutubeDL(this@ZenLoadApp)
                Log.d("ZenLoadApp", "Engine initialized and updated.")
            } catch (e: Exception) {
                Log.e("ZenLoadApp", "Engine error: ${e.message}")
            }
        }
    }
}