package com.example.zenload.domain.model

// Represents the complete details of the fetched media link
data class VideoDetails(
    val title: String,
    val thumbnailUrl: String,
    val duration: Long,
    val formats: List<MediaFormat> // List of all available video and audio formats
)