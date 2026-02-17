package com.example.zenload.di

import android.content.Context
import com.example.zenload.data.downloader.DownloaderRepositoryImpl
import com.example.zenload.domain.repository.DownloaderRepository
import com.example.zenload.domain.usecase.CancelDownloadUseCase
import com.example.zenload.domain.usecase.GetVideoDetailsUseCase
import com.example.zenload.domain.usecase.PauseDownloadUseCase
import com.example.zenload.domain.usecase.ResumeDownloadUseCase
import com.example.zenload.domain.usecase.StartDownloadUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Tells Hilt how to create and provide instances of our classes
@Module
@InstallIn(SingletonComponent::class) // These instances will live as long as the app is alive
object AppModule {

    // 1. Provide the Repository implementation
    @Provides
    @Singleton
    fun provideDownloaderRepository(
        @ApplicationContext context: Context
    ): DownloaderRepository {
        return DownloaderRepositoryImpl(context)
    }

    // 2. Provide the UseCases so ViewModel can use them easily

    @Provides
    @Singleton
    fun provideGetVideoDetailsUseCase(repository: DownloaderRepository): GetVideoDetailsUseCase {
        return GetVideoDetailsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideStartDownloadUseCase(repository: DownloaderRepository): StartDownloadUseCase {
        return StartDownloadUseCase(repository)
    }

    @Provides
    @Singleton
    fun providePauseDownloadUseCase(repository: DownloaderRepository): PauseDownloadUseCase {
        return PauseDownloadUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideResumeDownloadUseCase(repository: DownloaderRepository): ResumeDownloadUseCase {
        return ResumeDownloadUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideCancelDownloadUseCase(repository: DownloaderRepository): CancelDownloadUseCase {
        return CancelDownloadUseCase(repository)
    }
}