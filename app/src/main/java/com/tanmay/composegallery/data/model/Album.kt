package com.tanmay.composegallery.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "AllAlbums")
data class Album(
    @PrimaryKey(autoGenerate = false) val bucketId: Long,
    val displayName: String,
    val count: Int,
    val thumbnailPath: String
)
