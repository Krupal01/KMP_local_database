package com.care.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.care.schedule.domain.model.ScheduleModel
import com.care.schedule.domain.model.TodoModel
import com.care.schedule.service.ScheduleNotifier
import com.care.schedule.service.TaskScheduler
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

// ScheduleViewModel.kt
class ScheduleViewModel(
    private val taskScheduler: TaskScheduler,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState

    private var timerJob: Job? = null

    fun onScheduleTask(todo: TodoModel, scheduledTimeMillis: Long, durationMinutes: Int) {
        val schedule = ScheduleModel(
            scheduleId = "${todo.id}_${scheduledTimeMillis}",
            taskId = todo.id,
            taskTitle = todo.title,
            taskPriority = todo.priority,
            subjectName = todo.description,
            scheduledTimeMillis = scheduledTimeMillis,
            durationMinutes = durationMinutes
        )

        taskScheduler.schedule(schedule)
        startCountdown(scheduledTimeMillis)

        _uiState.update { it.copy(isScheduled = true, currentSchedule = schedule) }
    }

    fun onCancelSchedule() {
        _uiState.value.currentSchedule?.let { schedule ->
            taskScheduler.cancel(schedule.scheduleId)
            timerJob?.cancel()
            _uiState.update {
                it.copy(
                    isScheduled = false,
                    remainingTime = "00:00:00",
                    currentSchedule = null
                )
            }
        }
    }

    private fun startCountdown(scheduledTimeMillis: Long) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                val remaining = scheduledTimeMillis - Clock.System.now().toEpochMilliseconds()
                if (remaining <= 0) {
                    _uiState.update { it.copy(remainingTime = "00:00:00", isScheduled = false) }
                    break
                }
                _uiState.update { it.copy(remainingTime = formatTime(remaining)) }
                delay(1000L)
            }
        }
    }

    private fun formatTime(millis: Long): String {
        val hours = millis / 3_600_000
        val minutes = (millis % 3_600_000) / 60_000
        val seconds = (millis % 60_000) / 1_000
        return "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
    }

    data class ScheduleUiState(
        val isScheduled: Boolean = false,
        val remainingTime: String = "00:00:00",
        val currentSchedule: ScheduleModel? = null
    )
}