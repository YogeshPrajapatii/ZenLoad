package com.example.zenload.presentation.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ActiveViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {

    private val workManager = WorkManager.getInstance(context)

    // Observes all active downloads from WorkManager
    val activeDownloads = workManager.getWorkInfosByTagFlow("all_downloads").map { infos ->
        infos.filter { !it.state.isFinished }.map { info ->
            val progress = info.progress.getInt("PROGRESS", 0)
            val title = info.progress.getString("TITLE") ?: "Fetching..."
            DownloadTaskUiModel(
                id = info.id.toString(),
                title = title,
                progress = progress / 100f
            )
        }
    }
}

data class DownloadTaskUiModel(val id: String, val title: String, val progress: Float)