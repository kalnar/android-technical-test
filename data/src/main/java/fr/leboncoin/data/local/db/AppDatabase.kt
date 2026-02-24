package fr.leboncoin.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import fr.leboncoin.data.local.dao.AlbumDao
import fr.leboncoin.data.local.entity.AlbumEntity

@Database(entities = [AlbumEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun albumDao(): AlbumDao
}
