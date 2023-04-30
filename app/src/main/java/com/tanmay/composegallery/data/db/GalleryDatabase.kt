package com.tanmay.composegallery.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tanmay.composegallery.data.model.PhotoItem

@Database(entities = [PhotoItem::class], version = 1)
abstract class GalleryDatabase : RoomDatabase(){

    abstract fun galleryDao(): GalleryDao
}