package com.example.zenload.data.downloader

import android.content.Context
import android.os.Environment
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

// Runs entirely in the background
class VideoDownloadWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        // Retrieve data sent from Repository
        val url = inputData.getString("URL") ?: return Result.failure()
        val formatId = inputData.getString("FORMAT_ID") ?: return Result.failure()
        val title = inputData.getString("TITLE") ?: "ZenLoad_Media"

        return withContext(Dispatchers.IO) {
            try {
                // Create 'ZenLoad' directory inside the phone's public Downloads folder
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val zenLoadDir = File(downloadsDir, "ZenLoad")
                if (!zenLoadDir.exists()) {
                    zenLoadDir.mkdirs()
                }

                // Setup the download request
                val request = YoutubeDLRequest(url)
                request.addOption("-f", formatId) // Select the chosen quality (Audio/Video)

                // Set the output path and filename
                request.addOption("-o", "${zenLoadDir.absolutePath}/%(title)s.%(ext)s")

                // Execute the download and listen for progress
                YoutubeDL.getInstance().execute(request, id.toString()) { progress, _, _ ->
                    // Send progress back to UI
                    setProgressAsync(workDataOf("PROGRESS" to progress.toInt()))
                }

                Result.success() // Download finished successfully
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure() // Download failed or internet disconnected
            }
        }
    }
}