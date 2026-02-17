package com.example.zenload.domain.usecase

import com.example.zenload.domain.repository.DownloaderRepository

class ResumeDownloadUseCase(private val repository: DownloaderRepository) {
    // YoutubeDL is smart. If we start a download and a .part file exists,
    // it automatically resumes from where it left off.
    operator fun invoke(url: String, formatId: String, title: String): String {
        return repository.startDownload(url, formatId, title)
    }
}