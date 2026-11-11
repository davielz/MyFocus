package com.myfocus.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "myfocus_preferences")

/**
 * DataStoreManager mengelola persistensi state penting ke DataStore Preferences.
 * 
 * ATURAN WAJIB #4:
 * State penting seperti sesi aktif dan target waktu disimpan di Room/DataStore,
 * bukan hanya di memori, agar state tidak hilang jika background service
 * atau proses aplikasi dimatikan oleh sistem Android.
 */
class DataStoreManager(private val context: Context) {

    companion object {
        val KEY_IS_FOCUS_ACTIVE = booleanPreferencesKey("is_focus_active")
        val KEY_TARGET_ELAPSED_REALTIME = longPreferencesKey("target_elapsed_realtime")
        val KEY_TOTAL_DURATION_MILLIS = longPreferencesKey("total_duration_millis")
        val KEY_STREAK_COUNT = intPreferencesKey("streak_count")
    }

    val isFocusActiveFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_IS_FOCUS_ACTIVE] ?: false
    }

    val targetElapsedRealtimeFlow: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[KEY_TARGET_ELAPSED_REALTIME] ?: 0L
    }

    val totalDurationMillisFlow: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[KEY_TOTAL_DURATION_MILLIS] ?: 0L
    }

    val streakCountFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[KEY_STREAK_COUNT] ?: 0
    }

    suspend fun saveActiveFocusState(isActive: Boolean, targetElapsedRealtime: Long, durationMillis: Long) {
        context.dataStore.edit { preferences ->
            preferences[KEY_IS_FOCUS_ACTIVE] = isActive
            preferences[KEY_TARGET_ELAPSED_REALTIME] = targetElapsedRealtime
            preferences[KEY_TOTAL_DURATION_MILLIS] = durationMillis
        }
    }

    suspend fun clearFocusState() {
        context.dataStore.edit { preferences ->
            preferences[KEY_IS_FOCUS_ACTIVE] = false
            preferences[KEY_TARGET_ELAPSED_REALTIME] = 0L
            preferences[KEY_TOTAL_DURATION_MILLIS] = 0L
        }
    }

    suspend fun incrementStreak() {
        context.dataStore.edit { preferences ->
            val current = preferences[KEY_STREAK_COUNT] ?: 0
            preferences[KEY_STREAK_COUNT] = current + 1
        }
    }
}

