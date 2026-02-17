package com.example.zenload.data.downloader

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.zenload.domain.model.MediaFormat
import com.example.zenload.domain.model.VideoDetails
import com.example.zenload.domain.repository.DownloaderRepository
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs

class DownloaderRepositoryImpl(private val context: Context) : DownloaderRepository {

    private var isEngineInitialized = false

    override suspend fun fetchVideoDetails(url: String): Result<VideoDetails> {
        return withContext(Dispatchers.IO) {
            try {
                // Safe engine init fallback
                if (!isEngineInitialized) {
                    try {
                        YoutubeDL.getInstance().init(context.applicationContext)
                        FFmpeg.getInstance().init(context.applicationContext)
                        isEngineInitialized = true
                    } catch (e: Exception) {
                        isEngineInitialized = true
                    }
                }

                val request = YoutubeDLRequest(url)
                val info = YoutubeDL.getInstance().getInfo(request)
                val durationSeconds = info.duration.toLong()

                // Maps to keep only the best MP4/M4A formats and remove duplicates
                val bestVideoFormats = mutableMapOf<String, MediaFormat>()
                val bestAudioFormats = mutableMapOf<String, MediaFormat>()

                val standardVideoHeights = listOf(144, 240, 360, 480, 720, 1080, 1440, 2160)

                info.formats?.forEach { format ->
                    val ext = format.ext ?: ""
                    val vcodec = format.vcodec ?: "none"
                    val acodec = format.acodec ?: "none"
                    val formatNote = format.formatNote ?: ""
                    val height = format.height ?: 0

                    // Skip garbage MHTML and Storyboards
                    if (ext == "mhtml" || formatNote.contains("storyboard", true)) return@forEach

                    // 🔥 FIXED: Removed approxFileSize to resolve Android Studio compilation error
                    var finalSize = format.fileSize
                    if (finalSize == null || finalSize == 0L) {
                        val totalBitrate = format.tbr?.toDouble() ?: 0.0
                        if (totalBitrate > 0.0 && durationSeconds > 0) {
                            finalSize = ((totalBitrate * 1024) / 8).toLong() * durationSeconds
                        }
                    }
                    val sizeStr = if ((finalSize ?: 0) > 0) formatBytes(finalSize!!) else "Unknown Size"

                    // 1. Audio Formats (Standardize to 64k, 128k, 256k)
                    if (vcodec == "none" && acodec != "none") {
                        val rawAbr = format.abr?.toInt() ?: 0
                        if (rawAbr > 0) {
                            val standardKbps = when {
                                rawAbr <= 70 -> "64kbps"
                                rawAbr <= 140 -> "128kbps"
                                else -> "256kbps"
                            }

                            val newAudio = MediaFormat(
                                formatId = format.formatId ?: "",
                                resolution = standardKbps,
                                extension = ext,
                                fileSize = sizeStr,
                                hasAudio = true
                            )

                            // Prefer M4A over WEBM for better mobile audio
                            val existing = bestAudioFormats[standardKbps]
                            if (existing == null || (ext == "m4a" && existing.extension != "m4a")) {
                                bestAudioFormats[standardKbps] = newAudio
                            }
                        }
                    }
                    // 2. Video Formats (Only standard heights like 360p, 720p)
                    else if (vcodec != "none" && height in standardVideoHeights) {
                        val resKey = "${height}p"
                        val newVideo = MediaFormat(
                            formatId = format.formatId ?: "",
                            resolution = resKey,
                            extension = ext,
                            fileSize = sizeStr,
                            hasAudio = acodec != "none"
                        )

                        // Prefer MP4 over WEBM for mobile video
                        val existing = bestVideoFormats[resKey]
                        if (existing == null || (ext == "mp4" && existing.extension != "mp4")) {
                            bestVideoFormats[resKey] = newVideo
                        }
                    }
                }

                val finalFormats = bestVideoFormats.values.toList() + bestAudioFormats.values.toList()

                val videoDetails = VideoDetails(
                    title = info.title ?: "Unknown Title",
                    thumbnailUrl = info.thumbnail ?: "",
                    duration = durationSeconds,
                    formats = finalFormats
                )

                Result.success(videoDetails)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override fun startDownload(url: String, formatId: String, title: String): String {
        val downloadId = abs(url.hashCode()).toString()

        val inputData = Data.Builder()
            .putString("URL", url)
            .putString("FORMAT_ID", formatId)
            .putString("TITLE", title)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<VideoDownloadWorker>()
            .setInputData(inputData)
            .addTag("all_downloads")
            .addTag(downloadId)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(downloadId, ExistingWorkPolicy.REPLACE, workRequest)
        return downloadId
    }

    override fun pauseDownload(downloadId: String) {
        WorkManager.getInstance(context).cancelAllWorkByTag(downloadId)
    }

    override fun cancelDownload(downloadId: String) {
        WorkManager.getInstance(context).cancelAllWorkByTag(downloadId)
    }

    private fun formatBytes(bytes: Long): String {
        val mb = bytes / (1024.0 * 1024.0)
        return String.format("%.2f MB", mb)
    }
}