package com.myfocus.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entitas log notifikasi yang masuk selama sesi fokus.
 * 
 * ATURAN WAJIB #2:
 * DILARANG menyimpan isi notifikasi (judul/teks/body) demi privasi penuh pengguna.
 * HANYA simpan metadata: packageName, category, dan timestamp.
 */
@Entity(tableName = "notification_logs")
data class NotificationLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val category: String? = null,
    val timestamp: Long
)

