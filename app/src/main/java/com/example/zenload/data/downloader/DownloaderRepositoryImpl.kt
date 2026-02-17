package com.example.zenload.data.downloader

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.zenload.domain.model.MediaFormat
import com.example.zenload.domain.model.VideoDetails
import com.example.zenload.domain.repository.DownloaderRepository
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Implements the rules defined in the Domain layer
class DownloaderRepositoryImpl(private val context: Context) : DownloaderRepository {

    // 1. Fetch available formats (Audio & Video) and details from the URL
    override suspend fun fetchVideoDetails(url: String): Result<VideoDetails> {
        return withContext(Dispatchers.IO) {
            try {
                val request = YoutubeDLRequest(url)
                val info = YoutubeDL.getInstance().getInfo(request)

                // Map raw library data to our clean Domain model
                val formatsList = info.formats?.map { format ->
                    MediaFormat(
                        formatId = format.formatId ?: "",
                        // Check if height exists (e.g., 1080), else mark as "Audio"
                        resolution = format.formatNote ?: (format.height?.let { "${it}p" } ?: "Audio"),
                        extension = format.ext ?: "unknown",
                        fileSize = format.fileSize?.let { size -> formatBytes(size) } ?: "Unknown",
                        hasAudio = format.acodec != "none"
                    )
                } ?: emptyList()

                val videoDetails = VideoDetails(
                    title = info.title ?: "Unknown Title",
                    thumbnailUrl = info.thumbnail ?: "",
                    duration = info.duration.toLong(),
                    formats = formatsList
                )

                Result.success(videoDetails)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // 2. Start background download using WorkManager
    override fun startDownload(url: String, formatId: String, title: String): String {
        val downloadId = url.hashCode().toString() // Unique ID for this task

        // Pass data to the background worker
        val inputData = Data.Builder()
            .putString("URL", url)
            .putString("FORMAT_ID", formatId)
            .putString("TITLE", title)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<VideoDownloadWorker>()
            .setInputData(inputData)
            .addTag("all_downloads")
            .addTag(downloadId) // Tag helps in pausing/canceling later
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            downloadId,
            ExistingWorkPolicy.REPLACE, // Replaces if the same download is started again
            workRequest
        )

        return downloadId
    }

    // 3. Pause the download (Stops WorkManager, keeps the .part file)
    override fun pauseDownload(downloadId: String) {
        WorkManager.getInstance(context).cancelAllWorkByTag(downloadId)
    }

    // 4. Cancel the download completely
    override fun cancelDownload(downloadId: String) {
        WorkManager.getInstance(context).cancelAllWorkByTag(downloadId)
        // Note: Actual file deletion logic will be added later in a utility function
    }

    // Helper to convert bytes to MB
    private fun formatBytes(bytes: Long): String {
        val mb = bytes / (1024.0 * 1024.0)
        return String.format("%.2f MB", mb)
    }
}