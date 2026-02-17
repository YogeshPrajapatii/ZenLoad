package com.example.zenload.data.downloader

import android.content.Context
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

class VideoDownloadWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val url = inputData.getString("URL") ?: return Result.failure()
        val formatId = inputData.getString("FORMAT_ID") ?: return Result.failure()
        val title = inputData.getString("TITLE") ?: "ZenLoad_Media"

        return withContext(Dispatchers.IO) {
            try {
                // Save to public Downloads directory
                val publicDownloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val zenLoadDir = File(publicDownloadsDir, "ZenLoad")
                if (!zenLoadDir.exists()) zenLoadDir.mkdirs()

                // Safe filename to prevent OS errors
                val safeTitle = title.replace(Regex("[\\\\/:*?\"<>|]"), "_")

                val request = YoutubeDLRequest(url)
                request.addOption("-f", formatId)
                request.addOption("-o", "${zenLoadDir.absolutePath}/$safeTitle.%(ext)s")

                // Execute and update progress safely (prevents -1%)
                YoutubeDL.getInstance().execute(request, id.toString()) { progress, _, _ ->
                    val safeProgress = max(0, progress.toInt())
                    setProgressAsync(workDataOf("PROGRESS" to safeProgress, "TITLE" to title))
                }

                Log.d("Worker", "Download Success: ${zenLoadDir.absolutePath}")
                Result.success()
            } catch (e: Exception) {
                Log.e("Worker", "Download Failed: ${e.message}")
                e.printStackTrace()
                Result.failure()
            }
        }
    }
}