package com.emon.appscheduler.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.emon.appscheduler.data.model.Schedule
import com.emon.appscheduler.utils.ScheduleStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(schedule: Schedule): Long

    @Update
    suspend fun update(schedule: Schedule)

    @Delete
    suspend fun delete(schedule: Schedule)

    @Query("SELECT * FROM schedule_table")
    fun getAll(): Flow<List<Schedule>>

    @Query("SELECT * FROM schedule_table WHERE status = :status")
    fun getSchedulesByStatus(status: ScheduleStatus): Flow<List<Schedule>>

    @Query("SELECT * FROM schedule_table WHERE scheduleTime = :scheduleTime")
    fun isTimeAvailable(scheduleTime: Long,): Flow<List<Schedule>>
}
