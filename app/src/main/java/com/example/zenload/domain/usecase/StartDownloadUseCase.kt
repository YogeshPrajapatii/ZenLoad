package com.example.zenload.domain.usecase

import com.example.zenload.domain.repository.DownloaderRepository
import javax.inject.Inject

class StartDownloadUseCase @Inject constructor(
    private val repository: DownloaderRepository
) {
    operator fun invoke(url: String, formatId: String, title: String, thumbnailUrl: String): String {
        return repository.startDownload(url, formatId, title, thumbnailUrl)
    }
}