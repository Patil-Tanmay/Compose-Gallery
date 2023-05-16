package com.tanmay.composegallery.data.repository

import androidx.room.withTransaction
import com.tanmay.composegallery.data.db.GalleryDatabase
import com.tanmay.composegallery.data.model.GalleryItem
import com.tanmay.composegallery.data.model.PhotoItem
import com.tanmay.composegallery.data.paging.SystemPhotosDataSource
import javax.inject.Inject

class GalleryRepository @Inject constructor(
    private val systemPhotosDataSource: SystemPhotosDataSource,
    private val db: GalleryDatabase
) {

    suspend fun getPhotosFromSystem(): List<PhotoItem> {
        val folderAndPhotos = systemPhotosDataSource.getPhotosFromSystem()
        db.withTransaction {
            db.galleryDao().deleteAllPhotos()
            db.galleryDao().insertAllPhotos(folderAndPhotos.photos)
        }
        return folderAndPhotos.photos
    }

    suspend fun getFolderAndPhotos(): List<GalleryItem>{
        val folderAndPhotos = systemPhotosDataSource.getPhotosFromSystem()
        db.withTransaction {
            db.galleryDao().deleteAllPhotos()
            db.galleryDao().deleteAllFolders()
            db.galleryDao().insertAllFolders(folderAndPhotos.folders)
            db.galleryDao().insertAllPhotos(folderAndPhotos.photos)
        }
        return folderAndPhotos.folders
    }



}