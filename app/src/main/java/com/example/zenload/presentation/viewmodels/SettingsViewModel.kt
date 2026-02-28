package com.example.zenload.presentation.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {
    private val _downloadPath = MutableStateFlow("/storage/emulated/0/Download/ZenLoad")
    val downloadPath = _downloadPath.asStateFlow()

    private val _parallelDownloads = MutableStateFlow(3)
    val parallelDownloads = _parallelDownloads.asStateFlow()

    fun updateParallelDownloads(value: Int) {
        _parallelDownloads.value = value
    }
}