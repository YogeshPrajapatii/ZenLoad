package com.example.zenload.domain.usecase

import com.example.zenload.domain.repository.DownloaderRepository

class PauseDownloadUseCase(private val repository: DownloaderRepository) {
    operator fun invoke(downloadId: String) {
        repository.pauseDownload(downloadId)
    }
}