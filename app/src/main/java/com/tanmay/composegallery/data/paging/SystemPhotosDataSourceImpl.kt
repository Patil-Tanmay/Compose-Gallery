package com.tanmay.composegallery.data.paging

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.tanmay.composegallery.data.model.Album
import com.tanmay.composegallery.data.model.FolderAndPhotos
import com.tanmay.composegallery.data.model.PhotoItem
import com.tanmay.composegallery.utils.MimeType
import com.tanmay.composegallery.utils.equalsMimeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.ArrayList
import javax.inject.Inject

class SystemPhotosDataSourceImpl @Inject constructor(
    private val context: Context
) : SystemPhotosInterface {

    @SuppressLint("Range")
    override suspend fun getAlbums(): List<Album> = withContext(Dispatchers.IO) {
        val albumDataMap = LinkedHashMap<Long, AlbumData>()
        val orderBy = "${MediaStore.Images.Media._ID} DESC"
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.BUCKET_ID
        )

        val c = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            orderBy
        )

        var totalCount = 0
        var allViewThumbnailPath: Uri = Uri.EMPTY

        c?.let {
            while (c.moveToNext()) {

                val bucketId =
                    c.getInt(c.getColumnIndex(MediaStore.Images.Media.BUCKET_ID)).toLong()
                val bucketDisplayName =
                    c.getString(c.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                        ?: continue
                val bucketMimeType =
                    c.getString(c.getColumnIndex(MediaStore.Images.Media.MIME_TYPE)) ?: continue
                val imgId = c.getInt(c.getColumnIndex(MediaStore.MediaColumns._ID))

                /*if (isExceptImage(
                        bucketMimeType,
                        bucketDisplayName,
                            exceptMimeTypeList,
                        specifyFolderList
                    )
                ) continue*/

                val albumData = albumDataMap[bucketId]

                if (albumData == null) {
                    val imagePath =
                        Uri.withAppendedPath(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            "" + imgId
                        )

                    albumDataMap[bucketId] =
                        AlbumData(
                            bucketDisplayName,
                            imagePath,
                            1
                        )

                    if (allViewThumbnailPath == Uri.EMPTY) allViewThumbnailPath = imagePath

                } else {
                    albumData.imageCount++
                }

                totalCount++

            }
            c.close()
        }

        if (totalCount == 0) albumDataMap.clear()


        val albumList = ArrayList<Album>()

        if (albumDataMap.isNotEmpty())
            albumList.add(
                0, Album(
                    0,
                    "All Views",
                    totalCount,
                    allViewThumbnailPath.toString()
                )
            )

        albumDataMap.map {
            val value = it.value
            Album(
                it.key,
                value.displayName,
                value.imageCount,
                value.thumbnailPath.toString()
            )
        }.also {
            albumList.addAll(it)
        }

        albumList
//        emptyList()
    }

    @SuppressLint("Range")
    override suspend fun getPhotosFromSystem(bucketId: Long): List<PhotoItem> =
        withContext(Dispatchers.IO) {
            val imageUris = arrayListOf<PhotoItem>()
            val selection = "${MediaStore.Images.Media.BUCKET_ID} = ?"
            val bucketId: String = bucketId.toString()
            val sort = "${MediaStore.Images.Media._ID} DESC"
            val selectionArgs = arrayOf(bucketId)

            val images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val c = if (bucketId != "0") {
                context.contentResolver.query(images, null, selection, selectionArgs, sort)
            } else {
                context.contentResolver.query(images, null, null, null, sort)
            }
            c?.let {
                try {
                    if (c.moveToFirst()) {
                        do {
                            val mimeType =
                                c.getString(c.getColumnIndex(MediaStore.Images.Media.MIME_TYPE))
                                    ?: continue
                            val folderName =
                                c.getString(c.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                                    ?: continue

                            /*if (
    //                            isExceptMemeType(exceptMimeTypeList, mimeType)
                                true
                                || isNotContainsSpecifyFolderList(specifyFolderList, folderName)
                            ) continue*/

                            val imgId = c.getInt(c.getColumnIndex(MediaStore.MediaColumns._ID))
                            val path = Uri.withAppendedPath(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + imgId
                            )
                            imageUris.add(PhotoItem(uri = path.toString(), folderName = ""))
                        } while (c.moveToNext())
                    }
                } finally {
                    Log.d("ImageDataSOurceImpl", "getAllBucketImageUri: ${imageUris.size}")
                    if (!c.isClosed) c.close()
                }
            }
            imageUris
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
            val folderList = mutableListOf<Album>()
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

    private fun isExceptMemeType(
        mimeTypes: List<MimeType>,
        mimeType: String
    ): Boolean {
        for (type in mimeTypes) {
            if (type.equalsMimeType(mimeType)) return true
        }
        return false
    }

    private fun isNotContainsSpecifyFolderList(
        specifyFolderList: List<String>,
        displayBundleName: String
    ): Boolean {
        return if (specifyFolderList.isEmpty()) false
        else !specifyFolderList.contains(displayBundleName)
    }

    private fun isExceptImage(
        bucketMimeType: String,
        bucketDisplayName: String,
        exceptMimeTypeList: List<MimeType>,
        specifyFolderList: List<String>
    ) = (isExceptMemeType(exceptMimeTypeList, bucketMimeType)
            || isNotContainsSpecifyFolderList(specifyFolderList, bucketDisplayName)
            )

    private data class AlbumData(
        val displayName: String,
        val thumbnailPath: Uri,
        var imageCount: Int
    )
}