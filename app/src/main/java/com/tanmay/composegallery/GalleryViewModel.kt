package com.tanmay.composegallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.tanmay.composegallery.data.db.GalleryDatabase
import com.tanmay.composegallery.data.paging.GalleryPagingSource
import com.tanmay.composegallery.data.repository.GalleryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val repository: GalleryRepository,
    private val db: GalleryDatabase
) : ViewModel() {

    private val _isRefreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _showPhotos: MutableStateFlow<ShowPhotoStates> =
        MutableStateFlow(ShowPhotoStates.SplashScreen)
    val showPhotos: StateFlow<ShowPhotoStates> = _showPhotos.asStateFlow()

    fun updatePhotoState(value: ShowPhotoStates) {
        _showPhotos.value = value
    }

    fun getPhotos() = Pager(
        config = PagingConfig(
            initialLoadSize = 60,
            pageSize = 60,
        ),
        pagingSourceFactory = {
            GalleryPagingSource(db)
        }
    ).flow.cachedIn(viewModelScope)

    fun getPhotosFromSystem() = viewModelScope.launch{
        _isRefreshing.value = true
        try {
            val photos = repository.getPhotosFromSystem()
            if (photos.isNotEmpty()) {
                updatePhotoState(ShowPhotoStates.Gallery)
            } else {
                updatePhotoState(ShowPhotoStates.PermissionDenied)
            }
        } catch (e: Exception) {
            // Handle error
            updatePhotoState(ShowPhotoStates.PermissionDenied)
        } finally {
            _isRefreshing.value = false
        }
    }


    /*suspend fun getPhotosFromSystem() {
        withContext(Dispatchers.IO) {
        Log.i("Threads", Thread.currentThread().name)
            _isRefreshing.value = true
            val projection =
                arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME)
            val sortOrder = "${MediaStore.Images.Media._ID} DESC"
            val query = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().build()

            val images = mutableListOf<PhotoItem>()
            app.applicationContext.contentResolver.query(query, projection, null, null, sortOrder)
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

            _isRefreshing.value = when (true) {
                images.isNotEmpty() -> {
                    db.withTransaction {
                        db.galleryDao().deleteAllPhotos()
                        db.galleryDao().insertAllPhotos(images)
                    }
                    updatePhotoState(ShowPhotoStates.Gallery)
                    false
                }

                else -> {
                    updatePhotoState(ShowPhotoStates.PermissionDenied)
                    false
                }
            }
        }
    }*/



}

enum class ShowPhotoStates {
    Loading,
    Gallery,
    PermissionDenied,
    SplashScreen
}