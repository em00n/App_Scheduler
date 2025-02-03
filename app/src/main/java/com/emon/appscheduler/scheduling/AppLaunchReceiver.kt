package com.emon.appscheduler.scheduling

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.emon.appscheduler.utils.ScheduleStatus
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class AppLaunchReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val packageName = intent.getStringExtra("packageName") ?: return
        val scheduleId = intent.getIntExtra("scheduleId", -1)

        // Get ScheduleUseCase using Hilt EntryPoint API
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext, AppLaunchReceiverEntryPoint::class.java
        )
        val useCase = hiltEntryPoint.scheduleUseCase()

        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            // Start the app
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    useCase.updateScheduleStatus(scheduleId, ScheduleStatus.EXECUTED)
                } catch (e: Exception) {
                    Timber.e("AppLaunchReceiver", "Error updating schedule status", e)
                }
            }
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    useCase.updateScheduleStatus(scheduleId, ScheduleStatus.FAILED)
                } catch (e: Exception) {
                    Timber.e("AppLaunchReceiver", "Error updating schedule status to FAILED", e)
                }
            }
        }
    }
}
