package com.myfocus.app.timer

import android.os.SystemClock

/**
 * Helper utilitas untuk penghitungan durasi dan hitung mundur sesi fokus.
 * 
 * ATURAN WAJIB #3:
 * Durasi dan hitung mundur memakai SystemClock.elapsedRealtime(), BUKAN jam dinding
 * (System.currentTimeMillis()), agar timer kebal terhadap manipulasi tanggal/jam sistem.
 */
object FocusTimerHelper {

    /**
     * Menghitung target elapsedRealtime ketika sesi fokus selesai.
     * @param durationMinutes durasi fokus dalam menit
     * @return timestamp target berbasis SystemClock.elapsedRealtime() dalam milidetik
     */
    fun calculateTargetElapsedRealtime(durationMinutes: Int): Long {
        val durationMillis = durationMinutes * 60 * 1000L
        return SystemClock.elapsedRealtime() + durationMillis
    }

    /**
     * Menghitung sisa waktu dalam milidetik dari target yang ditentukan.
     * @param targetElapsedRealtime nilai target dari SystemClock.elapsedRealtime()
     * @return sisa milidetik (0 jika sudah selesai/lewat)
     */
    fun getRemainingMillis(targetElapsedRealtime: Long): Long {
        val now = SystemClock.elapsedRealtime()
        val remaining = targetElapsedRealtime - now
        return if (remaining > 0L) remaining else 0L
    }

    /**
     * Mengecek apakah sesi fokus telah selesai.
     */
    fun isFinished(targetElapsedRealtime: Long): Boolean {
        return SystemClock.elapsedRealtime() >= targetElapsedRealtime
    }

    /**
     * Memformat milidetik menjadi representasi string "MM:SS".
     */
    fun formatRemainingTime(millis: Long): String {
        val totalSeconds = (millis / 1000).coerceAtLeast(0)
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}

