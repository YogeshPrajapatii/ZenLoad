package com.example.zenload.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zenload.domain.model.MediaFormat
import com.example.zenload.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DownloadUiState {
    object Idle : DownloadUiState()
    object Loading : DownloadUiState()
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
    private val getVideoDetailsUseCase: GetVideoDetailsUseCase,
    private val startDownloadUseCase: StartDownloadUseCase,
    private val pauseDownloadUseCase: PauseDownloadUseCase,
    private val cancelDownloadUseCase: CancelDownloadUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<DownloadUiState>(DownloadUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun fetchVideoDetails(url: String) {
        _uiState.value = DownloadUiState.Loading
        viewModelScope.launch {
            val result = getVideoDetailsUseCase(url)
            result.fold(
                onSuccess = { videoDetails ->
                    // Industry Standard: Filter kbps for Audio, p for Video
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
                    _uiState.value = DownloadUiState.Error(error.localizedMessage ?: "Analysis Failed")
                }
            )
        }
    }

    fun startDownload(url: String, fId: String, title: String) = startDownloadUseCase(url, fId, title)
    fun cancelDownload(id: String) = cancelDownloadUseCase(id)
    fun resetState() { _uiState.value = DownloadUiState.Idle }
}