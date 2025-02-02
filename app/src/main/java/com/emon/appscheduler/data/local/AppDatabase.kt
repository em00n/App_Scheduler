package com.emon.appscheduler.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.emon.appscheduler.data.model.Schedule

@Database(entities = [Schedule::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
}