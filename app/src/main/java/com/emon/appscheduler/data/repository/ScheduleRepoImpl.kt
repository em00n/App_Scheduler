package com.emon.appscheduler.data.repository

import com.emon.appscheduler.data.local.ScheduleDao
import com.emon.appscheduler.data.model.Schedule
import com.emon.appscheduler.utils.ScheduleStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ScheduleRepoImpl @Inject constructor(
    private val scheduleDao: ScheduleDao
) : ScheduleRepository {

    override suspend fun getAllSchedules(): Flow<List<Schedule>> {
        return scheduleDao.getAll()
    }
    override suspend fun insert(schedule: Schedule):Long {
        return scheduleDao.insert(schedule)
    }

    override suspend fun update(schedule: Schedule) {
        scheduleDao.update(schedule)
    }

    override suspend fun delete(schedule: Schedule) {
        scheduleDao.delete(schedule)
    }

    override suspend fun getSchedulesByStatus(scheduleStatus: ScheduleStatus) {
        scheduleDao.getSchedulesByStatus(scheduleStatus)
    }

    override suspend fun isTimeAvailable(scheduleTime: Long, excludeId: Int?) {
        scheduleDao.isTimeAvailable(scheduleTime)
    }
}