package com.example.zenload.data.downloader

import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.zenload.data.local.DownloadDao
import com.example.zenload.data.local.DownloadEntity
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.max

@HiltWorker
class VideoDownloadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val downloadDao: DownloadDao
) : CoroutineWorker(context, params) {

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
            YoutubeDL.getInstance().execute(request, id.toString()) { progress, _, _ ->
                val currentProgress = max(0, progress.toInt())
                val displayProgress = if (isAudioOnly) {
                    currentProgress
                } else {
                    if (currentProgress == 100 && !isSecondStream) {
                        isSecondStream = true
                        50
                    } else if (!isSecondStream) {
                        currentProgress / 2
                    } else {
                        50 + (currentProgress / 2)
                    }
                }
                setProgressAsync(workDataOf("PROGRESS" to displayProgress, "TITLE" to title))
            }

            val finalExt = if (isAudioOnly) "mp3" else "mp4"
            val finalFile = File(zenLoadDir, "$cleanTitle.$finalExt")

            if (finalFile.exists()) {
                MediaScannerConnection.scanFile(context, arrayOf(finalFile.absolutePath), null, null)

                downloadDao.insertDownload(
                    DownloadEntity(
                        id = id.toString(),
                        title = title,
                        filePath = finalFile.absolutePath,
                        resolution = if (isAudioOnly) "Audio" else formatId,
                        sizeText = String.format("%.2f MB", finalFile.length() / (1024.0 * 1024.0))
                    )
                )
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}