package com.example.zenload.data.downloader

import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class VideoDownloadWorker(private val context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val url = inputData.getString("URL") ?: return@withContext Result.failure()
        val formatId = inputData.getString("FORMAT_ID") ?: return@withContext Result.failure()
        val title = inputData.getString("TITLE") ?: "ZenLoad_Media"

        try {
            val downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val zenLoadDir = File(downloadFolder, "ZenLoad")
            if (!zenLoadDir.exists()) zenLoadDir.mkdirs()

            // Remove illegal characters from title
            val cleanTitle = title.replace(Regex("[^a-zA-Z0-9]"), "_")
            val request = YoutubeDLRequest(url).apply {
                addOption("-f", formatId)
                addOption("-o", "${zenLoadDir.absolutePath}/$cleanTitle.%(ext)s")
                addOption("--no-mtime")
            }

            var finalPath = ""
            YoutubeDL.getInstance().execute(request, id.toString()) { progress, _, line ->
                // Capture file path from logs to update gallery
                if (line.contains("Destination:")) finalPath = line.substringAfter("Destination: ").trim()
                setProgressAsync(workDataOf("PROGRESS" to progress.toInt(), "TITLE" to title))
            }

            // Sync with MediaStore so it shows up in Gallery instantly
            if (finalPath.isNotEmpty()) {
                MediaScannerConnection.scanFile(context, arrayOf(finalPath), null, null)
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}