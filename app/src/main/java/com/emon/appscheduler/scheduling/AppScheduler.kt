package com.emon.appscheduler.scheduling

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.emon.appscheduler.data.model.Schedule
import timber.log.Timber
import javax.inject.Inject

class AppScheduler @Inject constructor(
    private val context: Context,
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    @SuppressLint("ScheduleExactAlarm")
    fun setAppSchedule(schedule: Schedule) {

        val intent = Intent(context, AppLaunchReceiver::class.java).apply {
            putExtra("packageName", schedule.appPackageName)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context, schedule.id, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, schedule.scheduleTime, pendingIntent)
    }

    fun cancelAppSchedule(schedule: Schedule) {

        val intent = Intent(context, AppLaunchReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context, schedule.id, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        Timber.d("Scheduler", "Schedule with ID ${schedule.id} canceled")
    }

    fun modifyAppSchedule(schedule: Schedule) {

        cancelAppSchedule(schedule) // Cancel old schedule
        setAppSchedule(schedule) // Set new time
    }

}
