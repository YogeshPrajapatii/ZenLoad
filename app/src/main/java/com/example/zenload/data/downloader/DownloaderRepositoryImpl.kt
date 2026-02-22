package com.example.zenload.data.downloader

import android.content.Context
import androidx.work.*
import com.example.zenload.domain.model.MediaFormat
import com.example.zenload.domain.model.VideoDetails
import com.example.zenload.domain.repository.DownloaderRepository
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs

class DownloaderRepositoryImpl(private val context: Context) : DownloaderRepository {

    override suspend fun fetchVideoDetails(url: String): Result<VideoDetails> {
        return withContext(Dispatchers.IO) {
            try {
                // Ensure engine is alive before fetch
                val request = YoutubeDLRequest(url)
                val info = YoutubeDL.getInstance().getInfo(request)
                val duration = info.duration.toLong()

                val videoMap = mutableMapOf<String, MediaFormat>()
                val audioMap = mutableMapOf<String, MediaFormat>()
                val standardHeights = listOf(144, 240, 360, 480, 720, 1080, 1440, 2160)

                info.formats?.forEach { format ->
                    if (format.ext == "mhtml" || format.formatNote?.contains("storyboard") == true) return@forEach
                    val size = calculateSize(format.fileSize, format.tbr?.toDouble() ?: 0.0, duration)
                    val sizeLabel = if (size > 0) formatBytes(size) else "Unknown"

                    if (format.vcodec == "none" && format.acodec != "none") {
                        val label = when {
                            (format.abr?.toInt() ?: 0) <= 70 -> "64kbps"
                            (format.abr?.toInt() ?: 0) <= 140 -> "128kbps"
                            else -> "256kbps"
                        }
                        if (!audioMap.containsKey(label) || format.ext == "m4a") {
                            audioMap[label] = MediaFormat(format.formatId!!, label, format.ext!!, sizeLabel, true)
                        }
                    } else if (format.vcodec != "none" && format.height in standardHeights) {
                        val label = "${format.height}p"
                        if (!videoMap.containsKey(label) || format.ext == "mp4") {
                            videoMap[label] = MediaFormat(format.formatId!!, label, format.ext!!, sizeLabel, format.acodec != "none")
                        }
                    }
                }
                Result.success(VideoDetails(info.title!!, info.thumbnail!!, duration, videoMap.values.toList() + audioMap.values.toList()))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override fun startDownload(url: String, formatId: String, title: String): String {
        val downloadId = abs(url.hashCode().toLong()).toString()
        val data = Data.Builder()
            .putString("URL", url)
            .putString("FORMAT_ID", formatId)
            .putString("TITLE", title)
            .build()

        val work = OneTimeWorkRequestBuilder<VideoDownloadWorker>()
            .setInputData(data)
            .addTag("all_downloads")
            .addTag(downloadId)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 10, java.util.concurrent.TimeUnit.SECONDS) // Auto-retry if fail
            .build()

        // Industry standard: KEEP ensures one doesn't kill another
        WorkManager.getInstance(context).enqueueUniqueWork(downloadId, ExistingWorkPolicy.KEEP, work)
        return downloadId
    }

    override fun pauseDownload(id: String) { WorkManager.getInstance(context).cancelAllWorkByTag(id) }
    override fun cancelDownload(id: String) { WorkManager.getInstance(context).cancelAllWorkByTag(id) }

    private fun calculateSize(fileSize: Long?, tbr: Double, duration: Long): Long = fileSize ?: ((tbr * 1024) / 8).toLong() * duration
    private fun formatBytes(b: Long) = String.format("%.2f MB", b / (1024.0 * 1024.0))
}