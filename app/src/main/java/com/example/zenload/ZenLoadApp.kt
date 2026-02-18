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

    companion object {
        @Volatile
        var isEngineUpdated = false
    }

    override fun onCreate() {
        super.onCreate()

        // Industry Logic: Start engine in background immediately
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Initialize binaries
                YoutubeDL.getInstance().init(this@ZenLoadApp)
                FFmpeg.getInstance().init(this@ZenLoadApp)

                // Check and apply updates in background (10-15 sec task)
                Log.d("ZenLoadApp", "Checking for updates...")
                YoutubeDL.getInstance().updateYoutubeDL(this@ZenLoadApp)
                isEngineUpdated = true
                Log.d("ZenLoadApp", "Engine updated and ready.")
            } catch (e: Exception) {
                Log.e("ZenLoadApp", "Engine Init Error: ${e.message}")
                // Even if update fails, we mark ready to use base version
                isEngineUpdated = true
            }
        }
    }
}