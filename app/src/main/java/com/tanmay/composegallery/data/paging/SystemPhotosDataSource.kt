package com.tanmay.composegallery.data.paging

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.tanmay.composegallery.data.model.PhotoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SystemPhotosDataSource constructor(
    private val context: Context
) {

    suspend fun getPhotosFromSystem() =
        withContext(Dispatchers.IO) {
            val projection =
                arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME)
            val sortOrder = "${MediaStore.Images.Media._ID} DESC"
            val query = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().build()

            val images = mutableListOf<PhotoItem>()
            context.contentResolver.query(query, projection, null, null, sortOrder)
                ?.use { cursor ->
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    val nameColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idColumn)
                        val name = cursor.getString(nameColumn)
                        val contentUri =
                            ContentUris.withAppendedId(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                id
                            )
                        images.add(PhotoItem(uri = contentUri.toString(), displayName = name))
                    }
                }

            images
        }
}