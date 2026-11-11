package com.myfocus.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.myfocus.app.data.repository.FocusRepository
import com.myfocus.app.timer.FocusTimerHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * ViewModel untuk layar MyFocus.
 * Mengelola StateFlow dan interaksi dengan Room & DataStore (Aturan #4).
 * Memakai SystemClock.elapsedRealtime() untuk hitung mundur (Aturan #3).
 */
class MainViewModel(
    private val repository: FocusRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var activeTargetElapsed: Long = 0L
    private var activeTotalDurationMillis: Long = 0L

    init {
        observeActiveSession()
        observeStats()
    }

    private fun observeActiveSession() {
        viewModelScope.launch {
            repository.isFocusActiveFlow.collectLatest { isActive ->
                _uiState.update { it.copy(isFocusActive = isActive) }
                if (!isActive) {
                    timerJob?.cancel()
                    _uiState.update {
                        it.copy(
                            remainingTimeText = FocusTimerHelper.formatRemainingTime(
                                it.selectedDurationMinutes * 60 * 1000L
                            ),
                            progress = 1.0f
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            repository.targetElapsedRealtimeFlow.collectLatest { target ->
                activeTargetElapsed = target
                if (target > 0L && _uiState.value.isFocusActive) {
                    startTimerTicker()
                }
            }
        }

        viewModelScope.launch {
            repository.totalDurationMillisFlow.collectLatest { duration ->
                activeTotalDurationMillis = duration
            }
        }
    }

    private fun observeStats() {
        viewModelScope.launch {
            repository.completedSessionCountFlow.collectLatest { count ->
                _uiState.update { it.copy(completedSessions = count) }
            }
        }
        viewModelScope.launch {
            repository.streakCountFlow.collectLatest { streak ->
                _uiState.update { it.copy(streakCount = streak) }
            }
        }
    }

    private fun startTimerTicker() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                val remaining = FocusTimerHelper.getRemainingMillis(activeTargetElapsed)
                val totalDuration = if (activeTotalDurationMillis > 0L) activeTotalDurationMillis else 1L
                val progress = (remaining.toFloat() / totalDuration.toFloat()).coerceIn(0.0f, 1.0f)

                _uiState.update {
                    it.copy(
                        remainingTimeText = FocusTimerHelper.formatRemainingTime(remaining),
                        progress = progress
                    )
                }

                if (remaining <= 0L) {
                    val minutes = (activeTotalDurationMillis / (60 * 1000L)).toInt()
                    repository.completeFocusSession(minutes)
                    break
                }
                delay(1000L)
            }
        }
    }

    fun startFocus(durationMinutes: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedDurationMinutes = durationMinutes) }
            repository.startFocusSession(durationMinutes)
        }
    }

    fun stopFocus() {
        viewModelScope.launch {
            repository.cancelFocusSession()
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }

    class Factory(private val repository: FocusRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

