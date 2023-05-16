package com.tanmay.composegallery.data.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "AllPhotos",)
data class PhotoItem(
    @PrimaryKey(autoGenerate = true) public var id: Int ?= null,
    var uri : String ? = "",
    var displayName: String?= "",
    val folderName : String?= ""
){
//    constructor() : this(0, "", "")
}

data class FolderAndPhotos(
    val photos: List<PhotoItem>,
    val folders: List<GalleryItem>
)

