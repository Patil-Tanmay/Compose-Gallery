package com.tanmay.composegallery.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.tanmay.composegallery.data.db.GalleryDatabase
import com.tanmay.composegallery.data.paging.SystemPhotosDataSourceImpl
import com.tanmay.composegallery.data.paging.SystemPhotosInterface
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GalleryPagingSourceModule {

//    @Provides
//    fun provideGalleryPagingSource(@ApplicationContext application: Application): GalleryPagingSource {
//        return GalleryPagingSource(application)
//    }

    @Provides
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideGalleryDatabase(@ApplicationContext context: Context): GalleryDatabase {
        return Room.databaseBuilder(context, GalleryDatabase::class.java, "GalleryDatabase").build()
    }

//    @Provides
//    @Singleton
//    fun provideSystemPhotosDataSource(@ApplicationContext context: Context): SystemPhotosDataSourceImpl {
//        return SystemPhotosDataSourceImpl(context)
//    }

}


@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Binds
    abstract fun bindSystemPhotosDataSource(systemPhotosDataSourceImpl: SystemPhotosDataSourceImpl): SystemPhotosInterface
}