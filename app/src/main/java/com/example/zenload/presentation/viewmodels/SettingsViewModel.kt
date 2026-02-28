package com.example.zenload.presentation.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDL
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _downloadPath = MutableStateFlow("/storage/emulated/0/Download/ZenLoad")
    val downloadPath = _downloadPath.asStateFlow()

    private val _parallelDownloads = MutableStateFlow(3f)
    val parallelDownloads = _parallelDownloads.asStateFlow()

    private val _isEngineLoading = MutableStateFlow(false)
    val isEngineLoading = _isEngineLoading.asStateFlow()

    fun updateDownloadPath(newPath: String) {
        _downloadPath.value = newPath
    }

    fun updateParallelDownloads(value: Float) {
        _parallelDownloads.value = value
    }

    fun reinitializeEngine() {
        viewModelScope.launch {
            _isEngineLoading.value = true
            withContext(Dispatchers.IO) {
                try {
                    YoutubeDL.getInstance().init(context)
                    FFmpeg.getInstance().init(context)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            _isEngineLoading.value = false
        }
    }
}