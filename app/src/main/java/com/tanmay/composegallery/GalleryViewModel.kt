package com.tanmay.composegallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.tanmay.composegallery.data.db.GalleryDatabase
import com.tanmay.composegallery.data.paging.AlbumPagingSource
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
                updatePhotoState(ShowPhotoStates.Albums)
            } else {
                updatePhotoState(ShowPhotoStates.EmptyScreen)
            }
        } catch (e: Exception) {
            // Handle error
            updatePhotoState(ShowPhotoStates.PermissionDenied)
        } finally {
            _isRefreshing.value = false
        }
    }


    fun getAllAlbums()= viewModelScope.launch {
        try {
            val albums = repository.getAllAlbums()
            if (albums.isNotEmpty()) {
                updatePhotoState(ShowPhotoStates.Albums)
            } else {
                updatePhotoState(ShowPhotoStates.EmptyScreen)
            }
        } catch (e: Exception) {
            // Handle error
            updatePhotoState(ShowPhotoStates.PermissionDenied)
        }
    }


    fun getPagedAlbums() = Pager(
        config = PagingConfig(
            initialLoadSize = 60,
            pageSize = 60,
        ),
        pagingSourceFactory = {
            AlbumPagingSource(db)
        }
    ).flow.cachedIn(viewModelScope)

}

enum class ShowPhotoStates {
    Loading,
    Albums,
    AlbumPhotos,
    PermissionDenied,
    EmptyScreen,
    SplashScreen
}