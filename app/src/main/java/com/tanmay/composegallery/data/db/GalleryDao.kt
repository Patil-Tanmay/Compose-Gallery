package com.tanmay.composegallery.data.db

import androidx.room.*
import com.tanmay.composegallery.data.model.Album
import com.tanmay.composegallery.data.model.PhotoItem

@Dao
interface GalleryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPhotos(gallery: List<PhotoItem>)

//    ORDER BY id ASC

    @Query("SELECT * FROM AllPhotos LIMIT :limit OFFSET :offset")
    suspend fun getPagedListPhotos(limit: Int, offset: Int): List<PhotoItem>

    @Query("SELECT * FROM AllPhotos")
    suspend fun getAllPhotos(): List<PhotoItem>

    @Query("DELETE FROM AllPhotos")
    suspend fun deleteAllPhotos()


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFolders(folders: List<Album>)

    @Query("SELECT * FROM AllAlbums")
    suspend fun getAllFolders(): List<Album>

    @Query("SELECT * FROM AllAlbums LIMIT :limit OFFSET :offset")
    suspend fun getPagedAlbums(limit: Int, offset: Int): List<Album>

    @Query("DELETE FROM AllAlbums")
    suspend fun deleteAllFolders()


}