package com.emon.appscheduler.ui

import android.app.Activity
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private lateinit var navController: NavController
    private val viewModel: ScheduleViewModel by viewModels()
    private var isDoubleBackPressToExit = false
    lateinit var installedApps:List<AppInfo>

    override fun viewBindingLayout(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    override fun initializeView(savedInstanceState: Bundle?) {

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.mainNavHost) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)
        setupOnBackPressed()

        lifecycleScope.launch {
            installedApps = loadInstalledApps(this@MainActivity).toMutableList().apply {
                add(0, AppInfo("", getString(R.string.select_app), null))
            }
        }

        binding.addScheduleButton.setOnClickListener {
            if (!checkFloatingPermission()) {
                showMessageForFloatingPermission()
            } else {
                if (::installedApps.isInitialized && installedApps.isNotEmpty()) {
                    openSchedulerDialog(installedApps)
                } else {
                    Toast.makeText(this, getString(R.string.loading_apps), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private suspend fun loadInstalledApps(context: Context): List<AppInfo> {
        return withContext(Dispatchers.IO) {
            getInstalledAppsInfo(context)
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
        if (result.resultCode != Activity.RESULT_OK && !checkFloatingPermission()) {
            showMessageForFloatingPermission()
        }
    }

    private fun openSchedulerDialog(installedApps:List<AppInfo>) {

        val dialogBinding = SchedulerDialogBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val adapter = AppInfoSpinnerAdapter(this, installedApps)
        dialogBinding.appSpinner.adapter = adapter

        var scheduledTime: Long? = null
        dialogBinding.selectTimeButton.setOnClickListener {
            val cal = Calendar.getInstance()
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            val minute = cal.get(Calendar.MINUTE)

            TimePickerDialog(this, { _, hourOfDay, minuteOfDay ->
                scheduledTime = getNextScheduledTime(hourOfDay, minuteOfDay)
                dialogBinding.pickedTime.text = timeFormat(scheduledTime!!)
            }, hour, minute, false).show()
        }

        dialogBinding.addScheduleBtn.setOnClickListener {
            val selectedPosition = dialogBinding.appSpinner.selectedItemPosition
            if (selectedPosition != 0) {
                if (dialogBinding.pickedTime.text != getString(R.string.time)) {

                    val appInfo = installedApps[selectedPosition]
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

                } else {
                    Toast.makeText(this, getString(R.string.select_time), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, getString(R.string.select_app), Toast.LENGTH_SHORT).show()
            }
        }
    }
}