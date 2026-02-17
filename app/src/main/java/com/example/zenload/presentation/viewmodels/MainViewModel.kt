package com.example.zenload.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zenload.domain.model.MediaFormat
import com.example.zenload.domain.usecase.CancelDownloadUseCase
import com.example.zenload.domain.usecase.GetVideoDetailsUseCase
import com.example.zenload.domain.usecase.PauseDownloadUseCase
import com.example.zenload.domain.usecase.ResumeDownloadUseCase
import com.example.zenload.domain.usecase.StartDownloadUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Defines the possible states of our UI (Loading, Success, Error)
sealed class DownloadUiState {
    object Idle : DownloadUiState()
    object Loading : DownloadUiState()

    data class Success(
        val title: String,
        val thumbnailUrl: String,
        val videoFormats: List<MediaFormat>, // Holds only Video qualities (1080p, 720p)
        val audioFormats: List<MediaFormat>  // Holds only Audio qualities (128kbps, etc.)
    ) : DownloadUiState()

    data class Error(val message: String) : DownloadUiState()
}

@HiltViewModel // Tells Hilt to automatically inject dependencies here
class MainViewModel @Inject constructor(
    private val getVideoDetailsUseCase: GetVideoDetailsUseCase,
    private val startDownloadUseCase: StartDownloadUseCase,
    private val pauseDownloadUseCase: PauseDownloadUseCase,
    private val resumeDownloadUseCase: ResumeDownloadUseCase,
    private val cancelDownloadUseCase: CancelDownloadUseCase
) : ViewModel() {

    // StateFlow to update the UI efficiently in Jetpack Compose
    private val _uiState = MutableStateFlow<DownloadUiState>(DownloadUiState.Idle)
    val uiState: StateFlow<DownloadUiState> = _uiState.asStateFlow()

    /**
     * 1. Fetches details and separates them into Video & Audio lists
     * Called when user pastes a link OR shares a link from YouTube
     */
    fun fetchVideoDetails(url: String) {
        _uiState.value = DownloadUiState.Loading

        // Launch in ViewModel scope to handle background threading
        viewModelScope.launch {
            val result = getVideoDetailsUseCase(url)

            result.fold(
                onSuccess = { videoDetails ->
                    // Magic happens here: Splitting the formats!
                    // If resolution is "Audio", put it in audioList, else in videoList
                    val audioList = videoDetails.formats.filter { it.resolution == "Audio" }
                    val videoList = videoDetails.formats.filter { it.resolution != "Audio" }

                    _uiState.value = DownloadUiState.Success(
                        title = videoDetails.title,
                        thumbnailUrl = videoDetails.thumbnailUrl,
                        videoFormats = videoList,
                        audioFormats = audioList
                    )
                },
                onFailure = { error ->
                    _uiState.value = DownloadUiState.Error(
                        message = error.localizedMessage ?: "Failed to fetch details. Check link or internet."
                    )
                }
            )
        }
    }

    /**
     * 2. Start a new download
     */
    fun startDownload(url: String, formatId: String, title: String) {
        startDownloadUseCase(url, formatId, title)
    }

    /**
     * 3. Pause an ongoing download
     */
    fun pauseDownload(downloadId: String) {
        pauseDownloadUseCase(downloadId)
    }

    /**
     * 4. Resume a paused download
     */
    fun resumeDownload(url: String, formatId: String, title: String) {
        resumeDownloadUseCase(url, formatId, title)
    }

    /**
     * 5. Cancel and remove download
     */
    fun cancelDownload(downloadId: String) {
        cancelDownloadUseCase(downloadId)
    }

    /**
     * Resets state to Idle (Useful when user closes the BottomSheet)
     */
    fun resetState() {
        _uiState.value = DownloadUiState.Idle
    }
}