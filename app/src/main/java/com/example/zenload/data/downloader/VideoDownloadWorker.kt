package com.example.zenload.data.downloader

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
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

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val channelId = "zenload_downloads"

    override suspend fun getForegroundInfo(): ForegroundInfo {
        createNotificationChannel()
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Starting Download...")
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setOngoing(true)
            .build()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(id.hashCode(), notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            ForegroundInfo(id.hashCode(), notification)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Downloads", NotificationManager.IMPORTANCE_LOW)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun updateNotification(title: String, progress: Int) {
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(if (progress < 100) "Downloading: $progress%" else "Processing...")
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setProgress(100, progress, false)
            .setOngoing(true)
            .build()
        notificationManager.notify(id.hashCode(), notification)
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val url = inputData.getString("URL") ?: return@withContext Result.failure()
        val formatId = inputData.getString("FORMAT_ID") ?: return@withContext Result.failure()
        val title = inputData.getString("TITLE") ?: "ZenLoad_Media"
        val thumb = inputData.getString("THUMB") ?: "" // Add thumb if you pass it from home

        try {
            setForeground(getForegroundInfo())

            val isAudioOnly = formatId.contains("kbps") || formatId.startsWith("audio")
            val displayFormat = if (isAudioOnly) "MP3" else "MP4"

            // Ensure initial zero state is passed completely
            setProgressAsync(workDataOf("PROGRESS" to 0, "TITLE" to title, "FORMAT" to displayFormat, "THUMB" to thumb))

            val downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val zenLoadDir = File(downloadFolder, "ZenLoad").apply { if (!exists()) mkdirs() }
            val cleanTitle = title.replace(Regex("[^a-zA-Z0-9]"), "_")

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

                setProgressAsync(workDataOf("PROGRESS" to displayProgress, "TITLE" to title, "FORMAT" to displayFormat, "THUMB" to thumb))
                updateNotification(title, displayProgress)
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
                notificationManager.cancel(id.hashCode())
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("ZenLoad_Debug", "Worker Download Error: ${e.message}", e)
            notificationManager.cancel(id.hashCode())
            Result.retry()
        }
    }
}