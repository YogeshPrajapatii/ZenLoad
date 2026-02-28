package com.example.zenload.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zenload.data.local.DownloadDao
import com.example.zenload.data.local.DownloadEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val downloadDao: DownloadDao
) : ViewModel() {

    val downloadedItems = downloadDao.getAllDownloads()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteItem(item: DownloadEntity) {
        viewModelScope.launch {
            val file = File(item.filePath)
            if (file.exists()) file.delete()
            downloadDao.deleteDownload(item)
        }
    }
}