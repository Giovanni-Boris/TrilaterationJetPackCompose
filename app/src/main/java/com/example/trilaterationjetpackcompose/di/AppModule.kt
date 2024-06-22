package com.example.trilaterationjetpackcompose.di

import android.app.Application
import com.example.trilaterationjetpackcompose.services.BeaconScanner
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideBeaconScanner(
        application: Application
    ) : BeaconScanner = BeaconScanner(application)
}