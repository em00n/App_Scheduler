package com.emon.appscheduler.ui

import android.app.Activity
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.emon.appscheduler.R
import com.emon.appscheduler.base.BaseActivity
import com.emon.appscheduler.data.model.AppInfo
import com.emon.appscheduler.data.model.Schedule
import com.emon.appscheduler.databinding.ActivityMainBinding
import com.emon.appscheduler.databinding.SchedulerDialogBinding
import com.emon.appscheduler.utils.AppConstants
import com.emon.appscheduler.utils.extensions.getInstalledAppsInfo
import com.emon.appscheduler.utils.extensions.getNextScheduledTime
import com.emon.appscheduler.utils.extensions.timeFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private lateinit var navController: NavController
    private val viewModel: ScheduleViewModel by viewModels()
    private var isDoubleBackPressToExit = false

    override fun viewBindingLayout(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    override fun initializeView(savedInstanceState: Bundle?) {

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.mainNavHost) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)
        setupOnBackPressed()

        binding.addScheduleButton.setOnClickListener {
            if (!checkFloatingPermission()) {
                showMessageForFloatingPermission()
            }else{
                openSchedulerDialog()
            }
        }
    }

    private fun setupOnBackPressed() {
        val callback = this.onBackPressedDispatcher.addCallback(this) {
            handleBackPressed()
        }
        callback.isEnabled = true
    }

    private fun handleBackPressed() {
        if (isDoubleBackPressToExit) {
            finish()
        } else {
            isDoubleBackPressToExit = true
            lifecycleScope.launch {
                delay(AppConstants.doublePressAppExitDelayTime)
                isDoubleBackPressToExit = false
            }
        }
    }

    // Helper method for checking overlay (floating) permission
    private fun checkFloatingPermission(): Boolean {
        return Settings.canDrawOverlays(this)
    }

    // Request overlay permission
    private fun requestFloatingPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        startActivityFloatingPermission.launch(intent)
    }

    // dialog explaining why overlay permission is needed
    private fun showMessageForFloatingPermission() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.enable_overlay_permission))
            .setMessage(getString(R.string.message_for_floating_permission))
            .setPositiveButton(getString(R.string.grant_permission)) { dialog, _ ->
                requestFloatingPermission()
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    // Initialize ActivityResultLauncher
    private val startActivityFloatingPermission = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Permission granted
        } else {
            // If permission is not granted
            if (!checkFloatingPermission()) {
                showMessageForFloatingPermission()
            }
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
                            Toast.makeText(this, getString(R.string.this_time_slot_is_already_taken), Toast.LENGTH_SHORT).show()
                        }
                    }

                } else Toast.makeText(this, getString(R.string.select_time), Toast.LENGTH_SHORT).show()
            } else Toast.makeText(this, getString(R.string.select_app), Toast.LENGTH_SHORT).show()
        }
    }
}