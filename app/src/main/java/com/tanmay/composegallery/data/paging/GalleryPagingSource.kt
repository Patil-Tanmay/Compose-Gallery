package com.tanmay.composegallery.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.room.withTransaction
import com.tanmay.composegallery.data.db.GalleryDatabase
import com.tanmay.composegallery.data.model.PhotoItem

class GalleryPagingSource constructor(
    private val db: GalleryDatabase
) : PagingSource<Int, PhotoItem>() {
    override fun getRefreshKey(state: PagingState<Int, PhotoItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotoItem> {
        val page = params.key ?: 1
        val offset = (page - 1) * params.loadSize

        val images = arrayListOf<PhotoItem>()

        val pagedList = db.withTransaction {
            db.galleryDao().getPagedList(params.loadSize, offset)
        }

        images.addAll(pagedList)

        val prevKey = if (page == 1) null else page - 1
        val nextKey = if (images.size < params.loadSize) null else page + 1
        return when (true) {
            images.isNotEmpty() -> {
                LoadResult.Page(images, prevKey, nextKey)
            }

            else -> {
                LoadResult.Error(Exception("No Data Found"))
            }
        }
    }

}