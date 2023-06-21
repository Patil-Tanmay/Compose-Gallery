package com.tanmay.composegallery.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "AllFolders")
data class FolderItem(
    @PrimaryKey(autoGenerate = false) val folderId: String,
    val folderName: String,
    val imagesCount: Int
)
