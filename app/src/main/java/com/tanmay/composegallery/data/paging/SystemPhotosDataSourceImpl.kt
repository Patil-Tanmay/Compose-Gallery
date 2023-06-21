package com.tanmay.composegallery.data.paging

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.tanmay.composegallery.data.model.FolderAndPhotos
import com.tanmay.composegallery.data.model.FolderItem
import com.tanmay.composegallery.data.model.PhotoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SystemPhotosDataSourceImpl @Inject constructor(
    private val context: Context
): SystemPhotosInterface {

    override suspend fun getFolders(): List<FolderItem> {
        return emptyList()
    }

    override suspend fun getBothFOlderAndPhotos(): FolderAndPhotos =
        withContext(Dispatchers.IO) {
            val projection =
                arrayOf(
                    MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.BUCKET_ID,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
//                    "COUNT(*) AS IMAGE_COUNT"
                )

            val sortOrder = "${MediaStore.Images.Media._ID} DESC"
            val query = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().build()

            val images = mutableListOf<PhotoItem>()
            val folderList = mutableListOf<FolderItem>()
            context.contentResolver.query(query, projection, null, null, sortOrder)
                ?.use { cursor ->
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    val nameColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idColumn)

                        val name = cursor.getString(nameColumn)

                        val contentUri =
                            ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

//                        val folderId =
//                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID))
//
//                        val folderName =
//                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
//                        val imageCount = cursor.getInt(cursor.getColumnIndexOrThrow("IMAGE_COUNT"))

//                        folderList.add(GalleryItem(folderId, folderName, 0))

                        images.add(
                            PhotoItem(
                                uri = contentUri.toString(),
                                displayName = name
//                                folderName = folderName
                            )
                        )
                    }
                }

            FolderAndPhotos(photos = images, folders = folderList.distinct())
        }

    override suspend fun getPhotosFromSystem(): List<PhotoItem> {
        return emptyList<PhotoItem>()
    }
}