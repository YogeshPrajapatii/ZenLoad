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
            val zenLoadDir = File(downloadFolder, "ZenLoad").apply { if (!exists()) mkdirs() }
            val cleanTitle = title.replace(Regex("[^a-zA-Z0-9]"), "_")

            // Strong detection of Audio only
            val isAudio = formatId.contains("kbps") || formatId.startsWith("audio")

            val request = YoutubeDLRequest(url).apply {
                if (isAudio) {
                    addOption("-f", "bestaudio/best")
                    addOption("-x")
                    addOption("--audio-format", "mp3")
                    addOption("-o", "${zenLoadDir.absolutePath}/$cleanTitle.mp3")
                } else {
                    // Snaptube Level: Combine selected Video + Best Audio
                    addOption("-f", "$formatId+bestaudio/best")
                    addOption("--merge-output-format", "mp4")
                    addOption("-o", "${zenLoadDir.absolutePath}/$cleanTitle.mp4")
                }
                addOption("--no-mtime")
            }

            YoutubeDL.getInstance().execute(request, id.toString()) { progress, _, _ ->
                setProgressAsync(workDataOf("PROGRESS" to progress.toInt(), "TITLE" to title))
            }

            MediaScannerConnection.scanFile(context, arrayOf(zenLoadDir.absolutePath), null, null)
            Result.success()
        } catch (e: Exception) {
            // Failure usually means YouTube blocked the request or storage full
            Result.retry()
        }
    }
}