package com.emon.appscheduler.data.repository

import com.emon.appscheduler.data.local.ScheduleDao
import com.emon.appscheduler.data.model.Schedule
import com.emon.appscheduler.utils.ScheduleStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ScheduleRepoImpl @Inject constructor(
    private val scheduleDao: ScheduleDao
) : ScheduleRepository {

    override fun getAllSchedules(): Flow<List<Schedule>> {
        return scheduleDao.getAll()
    }
    override suspend fun insert(schedule: Schedule):Long {
        return scheduleDao.insert(schedule)
    }

    override fun getSchedulesByStatus(scheduleStatus: ScheduleStatus): Flow<List<Schedule>>{
        return scheduleDao.getSchedulesByStatus(scheduleStatus)
    }

    override suspend fun isTimeAvailable(scheduleTime: Long):Boolean {
        return scheduleDao.isTimeAvailable(scheduleTime) == 0
    }

    override suspend fun updateStatus(scheduleId: Int, scheduleStatus: ScheduleStatus){
        return scheduleDao.updateStatus(scheduleId, scheduleStatus)
    }

    override suspend fun updateScheduleTime(scheduleId: Int, newTime: Long){
        return scheduleDao.updateScheduleTime(scheduleId, newTime)
    }
}