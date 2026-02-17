package com.example.zenload.domain.usecase

import com.example.zenload.domain.repository.DownloaderRepository

class StartDownloadUseCase(private val repository: DownloaderRepository) {
    operator fun invoke(url: String, formatId: String, title: String): String {
        return repository.startDownload(url, formatId, title)
    }
}