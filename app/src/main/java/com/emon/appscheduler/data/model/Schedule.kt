package com.emon.appscheduler.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.emon.appscheduler.utils.ScheduleStatus

@Entity(tableName = "schedule_table")
data class Schedule(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val appPackageName: String,
    val appName: String,
    val scheduleTime: Long,
    val status: ScheduleStatus = ScheduleStatus.PENDING,
)