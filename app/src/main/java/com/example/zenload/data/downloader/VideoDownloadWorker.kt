package com.example.zenload.data.downloader

import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.max

class VideoDownloadWorker(private val context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val url = inputData.getString("URL") ?: return@withContext Result.failure()
        val formatId = inputData.getString("FORMAT_ID") ?: return@withContext Result.failure()
        val title = inputData.getString("TITLE") ?: "ZenLoad_Media"

        try {
            val downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val zenLoadDir = File(downloadFolder, "ZenLoad").apply { if (!exists()) mkdirs() }
            val cleanTitle = title.replace(Regex("[^a-zA-Z0-9]"), "_")
            val isAudioOnly = formatId.contains("kbps") || formatId.startsWith("audio")

            val request = YoutubeDLRequest(url).apply {
                if (isAudioOnly) {
                    addOption("-f", "bestaudio/best")
                    addOption("-x")
                    addOption("--audio-format", "mp3")
                    addOption("-o", "${zenLoadDir.absolutePath}/$cleanTitle.mp3")
                } else {
                    addOption("-f", "$formatId+bestaudio/best")
                    addOption("--merge-output-format", "mp4")
                    addOption("-o", "${zenLoadDir.absolutePath}/$cleanTitle.mp4")
                }
                addOption("--no-mtime")
                addOption("--newline")
            }

            var isSecondStream = false
            YoutubeDL.getInstance().execute(request, id.toString()) { progress, _, line ->
                val currentProgress = max(0, progress.toInt()) // Fix: No more -1%

                val displayProgress = if (isAudioOnly) {
                    currentProgress // Audio only is straight 0-100
                } else {
                    // Logic for Video + Audio (50-50 split)
                    if (currentProgress == 100 && !isSecondStream) {
                        isSecondStream = true
                        50
                    } else if (!isSecondStream) {
                        currentProgress / 2 // First half: 0 to 50
                    } else {
                        50 + (currentProgress / 2) // Second half: 51 to 100
                    }
                }

                setProgressAsync(workDataOf(
                    "PROGRESS" to displayProgress,
                    "TITLE" to title
                ))
            }

            // Snaptube Level: Scan file asynchronously without blocking other downloads
            val finalExt = if (isAudioOnly) "mp3" else "mp4"
            val finalFile = File(zenLoadDir, "$cleanTitle.$finalExt")

            MediaScannerConnection.scanFile(context, arrayOf(finalFile.absolutePath), null) { path, _ ->
                Log.d("ZenLoad_System", "Download Complete & Scanned: $path")
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("ZenLoad_System", "Error: ${e.message}")
            Result.retry()
        }
    }
}