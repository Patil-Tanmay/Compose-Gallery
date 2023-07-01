package com.tanmay.composegallery.data.repository

import androidx.room.withTransaction
import com.tanmay.composegallery.data.db.GalleryDatabase
import com.tanmay.composegallery.data.model.Album
import com.tanmay.composegallery.data.model.PhotoItem
import com.tanmay.composegallery.data.paging.SystemPhotosInterface
import javax.inject.Inject

class GalleryRepository @Inject constructor(
    private val systemPhotosDataSource: SystemPhotosInterface,
    private val db: GalleryDatabase
) {

    suspend fun getPhotosFromSystem(): List<PhotoItem> {
        val folderAndPhotos = systemPhotosDataSource.getBothFOlderAndPhotos()
        db.withTransaction {
            db.galleryDao().deleteAllPhotos()
            db.galleryDao().insertAllPhotos(folderAndPhotos.photos)
        }
        return folderAndPhotos.photos
    }


    suspend fun getAllAlbums(): List<Album>{
        val albums = systemPhotosDataSource.getAlbums()
        db.withTransaction {
            db.galleryDao().deleteAllPhotos()
            db.galleryDao().deleteAllFolders()
            db.galleryDao().insertAllFolders(albums)
        }
        return albums
    }



}