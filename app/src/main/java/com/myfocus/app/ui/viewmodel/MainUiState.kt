package com.myfocus.app.ui.viewmodel

/**
 * State UI immutable untuk layar utama MyFocus.
 * Dikonsumsi oleh Jetpack Compose via StateFlow.
 */
data class MainUiState(
    val isFocusActive: Boolean = false,
    val remainingTimeText: String = "25:00",
    val selectedDurationMinutes: Int = 25,
    val completedSessions: Int = 0,
    val streakCount: Int = 0,
    val progress: Float = 1.0f
)

