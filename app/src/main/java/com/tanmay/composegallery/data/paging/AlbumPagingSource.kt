package com.tanmay.composegallery.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.room.withTransaction
import com.tanmay.composegallery.data.db.GalleryDatabase
import com.tanmay.composegallery.data.model.Album

class AlbumPagingSource constructor(
    private val db: GalleryDatabase
) : PagingSource<Int, Album>() {

    override fun getRefreshKey(state: PagingState<Int, Album>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Album> {
        val page = params.key ?: 1
        val offset = (page - 1) * params.loadSize

        val albumList = arrayListOf<Album>()

        val pagedList = db.withTransaction {
            db.galleryDao().getPagedAlbums(params.loadSize, offset)
        }

        albumList.addAll(pagedList)

        val prevKey = if (page == 1) null else page - 1
        val nextKey = if (albumList.size < params.loadSize) null else page + 1
        return when (true) {
            albumList.isNotEmpty() -> {
                LoadResult.Page(albumList, prevKey, nextKey)
            }

            else -> {
                LoadResult.Error(Exception("No Data Found"))
            }
        }
    }

}