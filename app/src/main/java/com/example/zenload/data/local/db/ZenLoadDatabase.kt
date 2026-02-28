package com.example.zenload.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DownloadEntity::class], version = 1, exportSchema = false)
abstract class ZenLoadDatabase : RoomDatabase() {
    abstract fun downloadDao(): DownloadDao
}