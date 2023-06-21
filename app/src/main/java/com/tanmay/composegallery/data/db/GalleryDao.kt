package com.tanmay.composegallery.data.db

import androidx.room.*
import com.tanmay.composegallery.data.model.FolderItem
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

    @Query("DELETE FROM AllPhotos")
    suspend fun deleteAllPhotos()


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFolders(folders: List<FolderItem>)

    @Query("SELECT * FROM AllFolders")
    suspend fun getAllFolders(): List<FolderItem>

    @Query("DELETE FROM AllFolders")
    suspend fun deleteAllFolders()


}