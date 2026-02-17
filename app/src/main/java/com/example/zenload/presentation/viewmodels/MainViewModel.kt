package com.example.zenload.presentation.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zenload.domain.model.MediaFormat
import com.example.zenload.domain.usecase.CancelDownloadUseCase
import com.example.zenload.domain.usecase.GetVideoDetailsUseCase
import com.example.zenload.domain.usecase.PauseDownloadUseCase
import com.example.zenload.domain.usecase.ResumeDownloadUseCase
import com.example.zenload.domain.usecase.StartDownloadUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DownloadUiState {
    object Idle : DownloadUiState()
    object Loading : DownloadUiState() // Directly shows "Analyzing Link..."

    data class Success(
        val title: String,
        val thumbnailUrl: String,
        val videoFormats: List<MediaFormat>,
        val audioFormats: List<MediaFormat>
    ) : DownloadUiState()

    data class Error(val message: String) : DownloadUiState()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getVideoDetailsUseCase: GetVideoDetailsUseCase,
    private val startDownloadUseCase: StartDownloadUseCase,
    private val pauseDownloadUseCase: PauseDownloadUseCase,
    private val resumeDownloadUseCase: ResumeDownloadUseCase,
    private val cancelDownloadUseCase: CancelDownloadUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<DownloadUiState>(DownloadUiState.Idle)
    val uiState: StateFlow<DownloadUiState> = _uiState.asStateFlow()

    fun fetchVideoDetails(url: String) {
        // Engine is already started in Application class, just show Loading
        _uiState.value = DownloadUiState.Loading

        viewModelScope.launch {
            val result = getVideoDetailsUseCase(url)

            result.fold(
                onSuccess = { videoDetails ->

                    // 🔥 TAB FIX: Separate correctly using "kbps" keyword
                    val audioList = videoDetails.formats.filter { it.resolution.contains("kbps") }
                    val videoList = videoDetails.formats.filter { !it.resolution.contains("kbps") }

                    _uiState.value = DownloadUiState.Success(
                        title = videoDetails.title,
                        thumbnailUrl = videoDetails.thumbnailUrl,
                        videoFormats = videoList,
                        audioFormats = audioList
                    )
                },
                onFailure = { error ->
                    _uiState.value = DownloadUiState.Error(
                        message = error.localizedMessage ?: "Failed to fetch media details."
                    )
                }
            )
        }
    }

    fun startDownload(url: String, formatId: String, title: String) = startDownloadUseCase(url, formatId, title)
    fun pauseDownload(downloadId: String) = pauseDownloadUseCase(downloadId)
    fun cancelDownload(downloadId: String) = cancelDownloadUseCase(downloadId)
    fun resetState() { _uiState.value = DownloadUiState.Idle }
}