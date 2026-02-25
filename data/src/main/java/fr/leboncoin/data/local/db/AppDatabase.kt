package fr.leboncoin.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import fr.leboncoin.data.local.dao.AlbumDao
import fr.leboncoin.data.local.entity.AlbumEntity

@Database(entities = [AlbumEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun albumDao(): AlbumDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE albums ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}
