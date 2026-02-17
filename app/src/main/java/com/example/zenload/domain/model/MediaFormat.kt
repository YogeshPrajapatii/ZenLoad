package com.example.zenload.domain.model

// Represents a single downloadable quality option (e.g., 1080p Video or 128kbps Audio)
data class MediaFormat(
    val formatId: String,   // Exact ID required by YoutubeDL to download
    val resolution: String, // E.g., "1080p", "720p", or "Audio"
    val extension: String,  // E.g., "mp4", "m4a", "webm"
    val fileSize: String,   // Formatted file size like "45.2 MB"
    val hasAudio: Boolean   // True if the format contains sound
)