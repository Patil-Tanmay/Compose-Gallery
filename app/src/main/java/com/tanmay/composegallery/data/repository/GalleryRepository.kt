package com.tanmay.composegallery.data.repository

import androidx.room.withTransaction
import com.tanmay.composegallery.data.db.GalleryDatabase
import com.tanmay.composegallery.data.model.PhotoItem
import com.tanmay.composegallery.data.paging.SystemPhotosDataSource
import javax.inject.Inject

class GalleryRepository @Inject constructor(
    private val systemPhotosDataSource: SystemPhotosDataSource,
    private val db: GalleryDatabase
) {

    suspend fun getPhotosFromSystem(): List<PhotoItem> {
        val photos = systemPhotosDataSource.getPhotosFromSystem()
        db.withTransaction {
            db.galleryDao().deleteAllPhotos()
            db.galleryDao().insertAllPhotos(photos)
        }
        return photos
    }

}