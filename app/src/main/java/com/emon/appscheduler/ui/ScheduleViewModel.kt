package com.emon.appscheduler.ui

import androidx.lifecycle.ViewModel
import com.emon.appscheduler.data.model.Schedule
import com.emon.appscheduler.domain.usecase.ScheduleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(private val scheduleUseCase: ScheduleUseCase) : ViewModel() {

    suspend fun addSchedule(schedule: Schedule):Long = scheduleUseCase.addSchedule(schedule)
}
