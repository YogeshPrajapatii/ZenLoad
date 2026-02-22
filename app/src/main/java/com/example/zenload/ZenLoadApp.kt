package com.example.zenload

import android.app.Application
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDL
import dagger.hilt.android.HiltAndroidApp
import kotlin.concurrent.thread

@HiltAndroidApp
class ZenLoadApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Using a simple thread to ensure initialization happens immediately
        thread(priority = Thread.MAX_PRIORITY) {
            try {
                YoutubeDL.getInstance().init(this)
                FFmpeg.getInstance().init(this)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}