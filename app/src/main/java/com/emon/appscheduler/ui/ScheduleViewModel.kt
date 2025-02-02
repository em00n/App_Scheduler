package com.emon.appscheduler.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emon.appscheduler.data.model.Schedule
import com.emon.appscheduler.domain.usecase.ScheduleUseCase
import com.emon.appscheduler.scheduling.AppScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val scheduleUseCase: ScheduleUseCase,
    private val appScheduler: AppScheduler
) : ViewModel() {

    fun addSchedule(schedule: Schedule) {
        viewModelScope.launch {
            val newId = scheduleUseCase.addSchedule(schedule).toInt()
            appScheduler.setAppSchedule(schedule.copy(id = newId))
        }
    }
}
