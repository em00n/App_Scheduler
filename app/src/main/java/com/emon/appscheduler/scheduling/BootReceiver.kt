package com.emon.appscheduler.scheduling

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.emon.appscheduler.utils.ScheduleStatus
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            val hiltEntryPoint = EntryPointAccessors.fromApplication(
                context.applicationContext, AppLaunchReceiverEntryPoint::class.java
            )
            val useCase = hiltEntryPoint.scheduleUseCase()
            val appScheduler = hiltEntryPoint.appScheduler()

            CoroutineScope(Dispatchers.IO).launch {
                useCase.getSchedulesByStatus(ScheduleStatus.PENDING).collect { schedules ->
                    schedules.forEach { schedule ->
                        appScheduler.setAppSchedule(schedule)
                    }
                }
            }
        }
    }
}
