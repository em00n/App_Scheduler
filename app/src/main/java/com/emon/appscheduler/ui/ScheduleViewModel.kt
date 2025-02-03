package com.emon.appscheduler.ui

import androidx.lifecycle.viewModelScope
import com.emon.appscheduler.data.model.Schedule
import com.emon.appscheduler.domain.usecase.ScheduleUseCase
import com.emon.appscheduler.scheduling.AppScheduler
import com.emon.appscheduler.utils.ScheduleStatus
import com.emon.appscheduler.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val scheduleUseCase: ScheduleUseCase,
    private val appScheduler: AppScheduler
) : BaseViewModel() {

    private val _scheduleList = MutableStateFlow<List<Schedule>>(emptyList())
    val scheduleList: StateFlow<List<Schedule>> get() = _scheduleList

    fun addSchedule(schedule: Schedule) {
        viewModelScope.launch {
            val newId = scheduleUseCase.addSchedule(schedule).toInt()
            appScheduler.setAppSchedule(schedule.copy(id = newId))
            getScheduleList(schedule.status)
        }
    }

    fun checkTimeAvailability(newTime: Long, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val isAvailable = scheduleUseCase.isTimeAvailable(newTime)
            callback(isAvailable)
        }
    }

    fun updateScheduleStatus(schedule: Schedule) {
        viewModelScope.launch {
            scheduleUseCase.updateScheduleStatus(schedule.id, ScheduleStatus.CANCEL)
            appScheduler.cancelAppSchedule(schedule)
            getScheduleList(ScheduleStatus.PENDING)
        }
    }

    fun updateScheduleTime(schedule: Schedule) {
        viewModelScope.launch {
            scheduleUseCase.updateScheduleTime(schedule.id,schedule.scheduleTime)
            appScheduler.modifyAppSchedule(schedule)
            getScheduleList(ScheduleStatus.PENDING)
        }
    }

    fun getScheduleList(scheduleStatus: ScheduleStatus) {
        viewModelScope.launch {
            scheduleUseCase.getSchedulesByStatus(scheduleStatus).collect { result ->
                _scheduleList.value = result
            }
        }
    }
}