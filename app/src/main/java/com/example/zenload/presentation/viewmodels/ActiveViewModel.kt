package com.example.zenload.presentation.viewmodels

import android.content.Context
import android.util.Log
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

    val activeDownloads = workManager.getWorkInfosByTagFlow("all_downloads").map { infos ->
        infos.filter { it.state == WorkInfo.State.RUNNING || it.state == WorkInfo.State.ENQUEUED }.map { info ->
            val progress = info.progress.getInt("PROGRESS", 0)
            val title = info.progress.getString("TITLE") ?: "Preparing Media..."
            val format = info.progress.getString("FORMAT") ?: "Unknown"
            val thumb = info.progress.getString("THUMB") ?: ""

            DownloadTaskUiModel(
                id = info.id.toString(),
                title = title,
                format = format,
                thumbnailUrl = thumb,
                progress = progress / 100f
            )
        }
    }
}

data class DownloadTaskUiModel(
    val id: String,
    val title: String,
    val format: String,
    val thumbnailUrl: String,
    val progress: Float
)