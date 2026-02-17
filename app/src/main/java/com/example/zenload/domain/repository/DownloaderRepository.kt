package com.example.zenload.domain.repository

import com.example.zenload.domain.model.VideoDetails

// Contract for data operations
interface DownloaderRepository {

    // Fetches title, thumbnail, and all formats from a URL
    suspend fun fetchVideoDetails(url: String): Result<VideoDetails>

    // Starts the download and returns a unique WorkManager ID
    fun startDownload(url: String, formatId: String, title: String): String

    // Pauses the ongoing download (keeps the temporary .part file)
    fun pauseDownload(downloadId: String)

    // Cancels the download and deletes temporary files
    fun cancelDownload(downloadId: String)
}