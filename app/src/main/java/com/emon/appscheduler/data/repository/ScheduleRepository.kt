package com.emon.appscheduler.data.repository

import com.emon.appscheduler.data.model.Schedule
import com.emon.appscheduler.utils.ScheduleStatus
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {

    fun getAllSchedules(): Flow<List<Schedule>>

    suspend fun insert(schedule: Schedule):Long

    fun getSchedulesByStatus(scheduleStatus: ScheduleStatus): Flow<List<Schedule>>

    suspend fun isTimeAvailable(scheduleTime: Long): Boolean

    suspend fun updateStatus(scheduleId: Int, scheduleStatus: ScheduleStatus)

    suspend fun updateScheduleTime(scheduleId: Int, newTime: Long)
}
