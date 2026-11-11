package com.myfocus.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.myfocus.app.data.local.entity.FocusSessionEntity
import com.myfocus.app.data.local.entity.NotificationLogEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) untuk mengakses tabel sesi fokus dan log notifikasi lokal.
 */
@Dao
interface FocusDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFocusSession(session: FocusSessionEntity): Long

    @Query("SELECT * FROM focus_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<FocusSessionEntity>>

    @Query("SELECT COUNT(*) FROM focus_sessions WHERE completed = 1")
    fun getCompletedSessionCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotificationLog(log: NotificationLogEntity): Long

    @Query("SELECT * FROM notification_logs ORDER BY timestamp DESC")
    fun getAllNotificationLogs(): Flow<List<NotificationLogEntity>>
}

