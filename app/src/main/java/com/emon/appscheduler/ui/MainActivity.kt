package com.emon.appscheduler.ui

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.emon.appscheduler.R
import com.emon.appscheduler.base.BaseActivity
import com.emon.appscheduler.data.model.AppInfo
import com.emon.appscheduler.data.model.Schedule
import com.emon.appscheduler.databinding.ActivityMainBinding
import com.emon.appscheduler.databinding.SchedulerDialogBinding
import com.emon.appscheduler.utils.extensions.getInstalledAppsInfo
import com.emon.appscheduler.utils.extensions.getNextScheduledTime
import com.emon.appscheduler.utils.extensions.timeFormat
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private lateinit var navController: NavController
    private val viewModel: ScheduleViewModel by viewModels()

    override fun viewBindingLayout(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    override fun initializeView(savedInstanceState: Bundle?) {

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.mainNavHost) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)

        binding.addScheduleButton.setOnClickListener {
            openSchedulerDialog()
        }
    }

    private fun openSchedulerDialog() {

        val dialogBinding = SchedulerDialogBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val installedApps = getInstalledAppsInfo(this) as ArrayList
        installedApps.add(0, AppInfo("", getString(R.string.select_app), null))

        val adapter = AppInfoSpinnerAdapter(this, installedApps)
        dialogBinding.appSpinner.adapter = adapter

        var scheduledTime: Long? = null
        dialogBinding.selectTimeButton.setOnClickListener {

            val cal = Calendar.getInstance()
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            val minute = cal.get(Calendar.MINUTE)

            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minuteOfDay ->
                scheduledTime = getNextScheduledTime(hourOfDay, minuteOfDay)
                dialogBinding.pickedTime.text = timeFormat(scheduledTime!!)
            }
            TimePickerDialog(this, timeSetListener, hour, minute, false).show()
        }

        dialogBinding.addScheduleBtn.setOnClickListener {

            if (dialogBinding.appSpinner.selectedItemPosition != 0) {
                if (dialogBinding.pickedTime.text != getString(R.string.time)) {

                    val appInfo = installedApps[dialogBinding.appSpinner.selectedItemPosition]
                    val schedule = Schedule(
                        appPackageName = appInfo.packageName,
                        appName = appInfo.appName,
                        scheduleTime = scheduledTime!!
                    )

                    viewModel.checkTimeAvailability(schedule.scheduleTime) { isAvailable ->
                        if (isAvailable) {
                            viewModel.addSchedule(schedule)
                            Toast.makeText(this, getString(R.string.app_scheduled), Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        } else {
                            Toast.makeText(this, "This time slot is already taken!", Toast.LENGTH_SHORT).show()
                        }
                    }

                } else Toast.makeText(this, getString(R.string.select_time), Toast.LENGTH_SHORT).show()
            } else Toast.makeText(this, getString(R.string.select_app), Toast.LENGTH_SHORT).show()
        }
    }
}