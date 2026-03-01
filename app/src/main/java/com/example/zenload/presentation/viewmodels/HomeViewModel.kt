package com.example.zenload.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zenload.domain.model.MediaFormat
import com.example.zenload.domain.usecase.GetVideoDetailsUseCase
import com.example.zenload.domain.usecase.StartDownloadUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HomeUiState {
    object Idle : HomeUiState()
    object Loading : HomeUiState()
    data class Success(
        val title: String,
        val thumbnailUrl: String,
        val videoFormats: List<MediaFormat>,
        val audioFormats: List<MediaFormat>
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getVideoDetailsUseCase: GetVideoDetailsUseCase,
    private val startDownloadUseCase: StartDownloadUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun fetchVideoDetails(url: String) {
        if (url.isBlank()) {
            _uiState.value = HomeUiState.Error("Please enter a valid link")
            return
        }

        _uiState.value = HomeUiState.Loading
        Log.d("ZenLoad_Debug", "Started fetching formats for URL: $url")

        viewModelScope.launch {
            val result = getVideoDetailsUseCase(url)
            result.fold(
                onSuccess = { details ->
                    val videoList = details.formats.filter { !it.resolution.contains("kbps") }
                    val audioList = details.formats.filter { it.resolution.contains("kbps") }

                    Log.d("ZenLoad_Debug", "Success! Found ${videoList.size} Video and ${audioList.size} Audio formats.")

                    if (videoList.isEmpty() && audioList.isEmpty()) {
                        Log.e("ZenLoad_Debug", "Error: Formats list is empty for this link.")
                        _uiState.value = HomeUiState.Error("No downloadable formats found for this link.")
                    } else {
                        _uiState.value = HomeUiState.Success(details.title, details.thumbnailUrl, videoList, audioList)
                    }
                },
                onFailure = { error ->
                    Log.e("ZenLoad_Debug", "Fetch Failed completely: ${error.message}", error)
                    _uiState.value = HomeUiState.Error(error.localizedMessage ?: "Failed to fetch formats. Please try again.")
                }
            )
        }
    }

    fun startDownload(url: String, formatId: String, title: String) {
        Log.d("ZenLoad_Debug", "Starting download for format ID: $formatId")
        startDownloadUseCase(url, formatId, title)
    }

    fun resetState() {
        _uiState.value = HomeUiState.Idle
    }
}