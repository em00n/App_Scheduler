package com.emon.appscheduler.domain.usecase

import com.emon.appscheduler.data.repository.ScheduleRepository
import com.emon.appscheduler.data.model.Schedule
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ScheduleUseCase @Inject constructor(
    private val repository: ScheduleRepository
) {
    suspend fun getAllSchedules(): Flow<List<Schedule>> {
        return repository.getAllSchedules()
    }

    suspend fun getSchedule(appPackageName: String): Schedule? {
        var schedule: Schedule? = null
        repository.getAllSchedules().collect { data ->
            schedule = data.firstOrNull { it.appPackageName == appPackageName }
        }
        return schedule
    }

    suspend fun updateSchedule(schedule: Schedule) {
        repository.update(schedule)
    }

    suspend fun addSchedule(schedule: Schedule) :Long{
       return repository.insert(schedule)
    }

}
