package com.example.zenload.domain.repository

import com.example.zenload.domain.model.VideoDetails

interface DownloaderRepository {
    suspend fun fetchVideoDetails(url: String): Result<VideoDetails>
    fun startDownload(url: String, formatId: String, title: String, thumbnailUrl: String): String
    fun pauseDownload(downloadId: String)
    fun cancelDownload(downloadId: String)
}