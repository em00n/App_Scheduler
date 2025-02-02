package com.emon.appscheduler.utils.extensions

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.emon.appscheduler.data.model.AppInfo
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun getInstalledAppsInfo(context: Context): List<AppInfo> {
    val pm = context.packageManager
    val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
    }

    // Get the list of resolved activities
    val resolvedInfos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        pm.queryIntentActivities(mainIntent, PackageManager.ResolveInfoFlags.of(0L))
    } else {
        pm.queryIntentActivities(mainIntent, 0)
    }

    // Map the resolved info to AppInfo objects
    return resolvedInfos.map {
        val packageName = it.activityInfo.packageName
        val appName = it.loadLabel(pm).toString()
        val appIcon = it.loadIcon(pm)
        AppInfo(packageName, appName, appIcon)
    }
}

fun getNextScheduledTime(hour: Int, minute: Int): Long {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)

        // If the time has already passed today, schedule it for tomorrow
        if (timeInMillis <= System.currentTimeMillis()) {
            add(Calendar.DAY_OF_MONTH, 1)
        }
    }
    return calendar.timeInMillis
}

fun timeFormat(scheduleTime: Long): String{
   return SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(
        Date(scheduleTime)
    )
}