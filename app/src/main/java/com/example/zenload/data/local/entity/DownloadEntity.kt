package com.example.zenload.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey val id: String,
    val title: String,
    val filePath: String,
    val resolution: String,
    val sizeText: String,
    val thumbnailUrl: String,
    val timestamp: Long = System.currentTimeMillis()
)