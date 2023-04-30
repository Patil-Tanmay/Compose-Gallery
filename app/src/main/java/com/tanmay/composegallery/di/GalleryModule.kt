package com.tanmay.composegallery.di

import android.content.Context
import androidx.room.Room
import com.tanmay.composegallery.data.db.GalleryDatabase
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
    @Singleton
    fun provideGalleryDatabase(@ApplicationContext context: Context): GalleryDatabase {
        return Room.databaseBuilder(context, GalleryDatabase::class.java, "GalleryDatabase").build()
    }
}
