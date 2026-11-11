package com.myfocus.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entitas yang merepresentasikan riwayat sesi fokus pengguna di database Room.
 * Semua data disimpan 100% lokal di perangkat (Sesuai Aturan #1 & #4).
 */
@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val durationMinutes: Int,
    val completed: Boolean,
    val timestamp: Long
)

