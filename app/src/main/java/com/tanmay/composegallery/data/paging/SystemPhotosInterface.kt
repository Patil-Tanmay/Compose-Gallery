package com.tanmay.composegallery.data.paging

import com.tanmay.composegallery.data.model.Album
import com.tanmay.composegallery.data.model.FolderAndPhotos
import com.tanmay.composegallery.data.model.PhotoItem

interface SystemPhotosInterface {
    suspend fun getPhotosFromSystem(bucketId: Long) : List<PhotoItem>

    suspend fun getAlbums() : List<Album>

    suspend fun getBothFOlderAndPhotos(): FolderAndPhotos
}