package com.emon.appscheduler.domain.usecase

import com.emon.appscheduler.data.repository.ScheduleRepository
import com.emon.appscheduler.data.model.Schedule
import com.emon.appscheduler.utils.ScheduleStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ScheduleUseCase @Inject constructor(
    private val repository: ScheduleRepository
) {
    fun getAllSchedules(): Flow<List<Schedule>> {
        return repository.getAllSchedules()
    }

    suspend fun addSchedule(schedule: Schedule) :Long{
       return repository.insert(schedule)
    }

    suspend fun isTimeAvailable(scheduleTime: Long): Boolean{
        return repository.isTimeAvailable(scheduleTime)
    }

    fun getSchedulesByStatus(scheduleStatus: ScheduleStatus): Flow<List<Schedule>>{
        return repository.getSchedulesByStatus(scheduleStatus)
    }

    suspend fun updateScheduleStatus(scheduleId: Int, scheduleStatus: ScheduleStatus) {
        repository.updateStatus(scheduleId, scheduleStatus)
    }

    suspend fun updateScheduleTime(scheduleId: Int, newTime: Long) {
        repository.updateScheduleTime(scheduleId, newTime)
    }
}
