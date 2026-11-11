package com.myfocus.app.data.repository

import com.myfocus.app.data.local.AppDatabase
import com.myfocus.app.data.local.DataStoreManager
import com.myfocus.app.data.local.entity.FocusSessionEntity
import com.myfocus.app.data.local.entity.NotificationLogEntity
import com.myfocus.app.timer.FocusTimerHelper
import kotlinx.coroutines.flow.Flow

/**
 * Repository layer yang menghubungkan Room Database dan DataStore ke ViewModel.
 * Menjamin semua operasi data bersifat lokal (Aturan #1, #2, #3, #4).
 */
class FocusRepository(
    private val database: AppDatabase,
    private val dataStoreManager: DataStoreManager
) {
    val isFocusActiveFlow: Flow<Boolean> = dataStoreManager.isFocusActiveFlow
    val targetElapsedRealtimeFlow: Flow<Long> = dataStoreManager.targetElapsedRealtimeFlow
    val totalDurationMillisFlow: Flow<Long> = dataStoreManager.totalDurationMillisFlow
    val streakCountFlow: Flow<Int> = dataStoreManager.streakCountFlow

    val completedSessionCountFlow: Flow<Int> = database.focusDao().getCompletedSessionCount()
    val allSessionsFlow: Flow<List<FocusSessionEntity>> = database.focusDao().getAllSessions()

    suspend fun startFocusSession(durationMinutes: Int) {
        val targetElapsedRealtime = FocusTimerHelper.calculateTargetElapsedRealtime(durationMinutes)
        val durationMillis = durationMinutes * 60 * 1000L
        dataStoreManager.saveActiveFocusState(
            isActive = true,
            targetElapsedRealtime = targetElapsedRealtime,
            durationMillis = durationMillis
        )
    }

    suspend fun completeFocusSession(durationMinutes: Int) {
        val session = FocusSessionEntity(
            durationMinutes = durationMinutes,
            completed = true,
            timestamp = System.currentTimeMillis()
        )
        database.focusDao().insertFocusSession(session)
        dataStoreManager.incrementStreak()
        dataStoreManager.clearFocusState()
    }

    suspend fun cancelFocusSession() {
        dataStoreManager.clearFocusState()
    }

    /**
     * Menyimpan metadata notifikasi masuk tanpa isi pesan (Aturan #2).
     */
    suspend fun logNotification(packageName: String, category: String?) {
        val log = NotificationLogEntity(
            packageName = packageName,
            category = category,
            timestamp = System.currentTimeMillis()
        )
        database.focusDao().insertNotificationLog(log)
    }
}

