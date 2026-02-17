package com.example.zenload.domain.usecase

import com.example.zenload.domain.model.VideoDetails
import com.example.zenload.domain.repository.DownloaderRepository

class GetVideoDetailsUseCase(private val repository: DownloaderRepository) {
    suspend operator fun invoke(url: String): Result<VideoDetails> {
        return repository.fetchVideoDetails(url)
    }
}