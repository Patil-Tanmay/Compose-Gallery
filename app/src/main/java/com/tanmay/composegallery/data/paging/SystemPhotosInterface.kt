package com.tanmay.composegallery.data.paging

import android.provider.ContactsContract.Contacts.Photo
import com.tanmay.composegallery.data.model.FolderAndPhotos
import com.tanmay.composegallery.data.model.FolderItem
import com.tanmay.composegallery.data.model.PhotoItem

interface SystemPhotosInterface {
    suspend fun getPhotosFromSystem() : List<PhotoItem>

    suspend fun getFolders() : List<FolderItem>

    suspend fun getBothFOlderAndPhotos(): FolderAndPhotos
}