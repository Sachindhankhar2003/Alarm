package com.example.wakeupmath.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wakeupmath.alarm.AlarmScheduler
import com.example.wakeupmath.data.local.AlarmDatabase
import com.example.wakeupmath.data.local.AlarmEntity
import com.example.wakeupmath.data.repository.AlarmRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.example.wakeupmath.data.repository.WakeStatRepository

class AlarmViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AlarmRepository
    private val wakeStatRepository: WakeStatRepository

    val alarms: StateFlow<List<AlarmEntity>>
    val totalSolvedCount: StateFlow<Int>
    val averageSolveTime: StateFlow<Double?>

    init {
        val db = AlarmDatabase.getInstance(application)
        repository = AlarmRepository(db.alarmDao())
        wakeStatRepository = WakeStatRepository(db.wakeStatDao())

        alarms = repository.allAlarms.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        totalSolvedCount = wakeStatRepository.totalSolvedCount.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )
        averageSolveTime = wakeStatRepository.averageSolveTime.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    }

    fun addAlarm(
        hour: Int,
        minute: Int,
        label: String,
        difficulty: String,
        repeatDays: String,
        sound: String,
        onComplete: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val alarm = AlarmEntity(
                hour = hour,
                minute = minute,
                label = label,
                difficulty = difficulty,
                repeatDays = repeatDays,
                sound = sound,
                enabled = true
            )
            val id = repository.insertAlarm(alarm)
            val savedAlarm = alarm.copy(id = id.toInt())
            AlarmScheduler.schedule(getApplication(), savedAlarm)
            onComplete()
        }
    }

    fun toggleAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            val updated = alarm.copy(enabled = !alarm.enabled)
            repository.updateAlarm(updated)
            if (updated.enabled) {
                AlarmScheduler.schedule(getApplication(), updated)
            } else {
                AlarmScheduler.cancel(getApplication(), updated)
            }
        }
    }

    fun deleteAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            repository.deleteAlarm(alarm)
            AlarmScheduler.cancel(getApplication(), alarm)
        }
    }
}
