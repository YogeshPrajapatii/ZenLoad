package com.example.zenload

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDL
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class ZenLoadApp : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                YoutubeDL.getInstance().init(this@ZenLoadApp)
                FFmpeg.getInstance().init(this@ZenLoadApp)

                YoutubeDL.getInstance().updateYoutubeDL(this@ZenLoadApp, YoutubeDL.UpdateChannel.STABLE)
                Log.d("ZenLoad_Debug", "Engine Init & Updated")
            } catch (e: Exception) {
                Log.e("ZenLoad_Debug", "Engine Error: ${e.message}")
            }
        }
    }
}