package com.emon.appscheduler.data.repository

import com.emon.appscheduler.data.model.Schedule
import com.emon.appscheduler.utils.ScheduleStatus
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {

    suspend fun getAllSchedules(): Flow<List<Schedule>>

    suspend fun insert(schedule: Schedule):Long

    suspend fun update(schedule: Schedule)

    suspend fun delete(schedule: Schedule)

    suspend fun getSchedulesByStatus(scheduleStatus: ScheduleStatus)

    suspend fun isTimeAvailable(scheduleTime: Long, excludeId: Int?)
}
