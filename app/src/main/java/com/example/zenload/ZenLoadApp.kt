package com.example.zenload

import android.app.Application
import android.util.Log
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDL
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ZenLoadApp : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            // Engine initialize kar rahe hain
            YoutubeDL.getInstance().init(this)
            FFmpeg.getInstance().init(this)
        } catch (e: Throwable) {
            // 'Throwable' emulator ke har hard-crash ko rok lega
            Log.e("ZenLoadApp", "Failed to initialize YoutubeDL/FFmpeg: ${e.message}")
        }
    }
}