package fr.leboncoin.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.leboncoin.data.local.entity.AlbumEntity

@Dao
interface AlbumDao {

    @Query("SELECT * FROM albums")
    suspend fun getAll(): List<AlbumEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(albums: List<AlbumEntity>)

    @Query("DELETE FROM albums")
    suspend fun deleteAll()

    @Query("SELECT fetchedAt FROM albums LIMIT 1")
    suspend fun getLastFetchedAt(): Long?

    @Query("UPDATE albums SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Int, isFavorite: Boolean)
}
