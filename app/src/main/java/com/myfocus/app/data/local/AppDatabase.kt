package com.myfocus.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.myfocus.app.data.local.dao.FocusDao
import com.myfocus.app.data.local.entity.FocusSessionEntity
import com.myfocus.app.data.local.entity.NotificationLogEntity

/**
 * Database utama Room untuk MyFocus.
 * Menggunakan KSP untuk pemrosesan anotasi Room.
 * Tidak ada data yang keluar perangkat (Aturan #1).
 */
@Database(
    entities = [FocusSessionEntity::class, NotificationLogEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun focusDao(): FocusDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "myfocus.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

