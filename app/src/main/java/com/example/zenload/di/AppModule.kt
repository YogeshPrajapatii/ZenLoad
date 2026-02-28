package com.example.zenload.di

import android.content.Context
import androidx.room.Room
import com.example.zenload.data.downloader.DownloaderRepositoryImpl
import com.example.zenload.data.local.DownloadDao
import com.example.zenload.data.local.ZenLoadDatabase
import com.example.zenload.domain.repository.DownloaderRepository
import com.example.zenload.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ZenLoadDatabase {
        return Room.databaseBuilder(context, ZenLoadDatabase::class.java, "zenload_db").build()
    }

    @Provides
    fun provideDownloadDao(db: ZenLoadDatabase): DownloadDao = db.downloadDao()

    @Provides
    @Singleton
    fun provideDownloaderRepository(
        @ApplicationContext context: Context,
        dao: DownloadDao
    ): DownloaderRepository {
        return DownloaderRepositoryImpl(context, dao)
    }

    @Provides
    fun provideGetVideoDetailsUseCase(repo: DownloaderRepository) = GetVideoDetailsUseCase(repo)

    @Provides
    fun provideStartDownloadUseCase(repo: DownloaderRepository) = StartDownloadUseCase(repo)

    @Provides
    fun provideCancelDownloadUseCase(repo: DownloaderRepository) = CancelDownloadUseCase(repo)

    @Provides
    fun providePauseDownloadUseCase(repo: DownloaderRepository) = PauseDownloadUseCase(repo)

    @Provides
    fun provideResumeDownloadUseCase(repo: DownloaderRepository) = ResumeDownloadUseCase(repo)
}