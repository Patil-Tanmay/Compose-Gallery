package com.tanmay.composegallery.data.db

import androidx.room.*
import com.tanmay.composegallery.data.model.PhotoItem

@Dao
interface GalleryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPhotos(gallery: List<PhotoItem>)

//    ORDER BY id ASC

    @Query("SELECT * FROM AllPhotos LIMIT :limit OFFSET :offset")
    suspend fun getPagedList(limit: Int, offset: Int): List<PhotoItem>

    @Query("SELECT * FROM AllPhotos")
    suspend fun getAllPhotos(): List<PhotoItem>

    // TODO: check whether we need to use update or not
//    @Update

    @Query("DELETE FROM AllPhotos")
    suspend fun deleteAllPhotos()


}