package com.emon.appscheduler.ui.schedule

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.emon.appscheduler.R
import com.emon.appscheduler.base.BaseFragment
import com.emon.appscheduler.data.model.Schedule
import com.emon.appscheduler.databinding.FragmentScheduleBinding
import com.emon.appscheduler.databinding.UpdateScheduleTimeDialogBinding
import com.emon.appscheduler.ui.ScheduleViewModel
import com.emon.appscheduler.utils.ScheduleStatus
import com.emon.appscheduler.utils.autoCleared
import com.emon.appscheduler.utils.extensions.getNextScheduledTime
import com.emon.appscheduler.utils.extensions.setUpVerticalRecyclerView
import com.emon.appscheduler.utils.extensions.timeFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class ScheduleFragment : BaseFragment<FragmentScheduleBinding>() {

    override fun viewBindingLayout(): FragmentScheduleBinding =
        FragmentScheduleBinding.inflate(layoutInflater)

    private val viewModel by viewModels<ScheduleViewModel>()
    private var adapter by autoCleared<ScheduleListAdapter>()

    override fun initializeView(savedInstanceState: Bundle?) {

        adapter = ScheduleListAdapter(
            onScheduleSwitchClick = {
                viewModel.updateScheduleStatus(it)
            }, onUpdateClick = {
                updateScheduleTimeDialog(it)
            })
        requireContext().setUpVerticalRecyclerView(binding.scheduleListRV, adapter)

        viewModel.getScheduleList(ScheduleStatus.PENDING)
        observeSchedules()
    }

    private fun observeSchedules() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.scheduleList.collect { schedules ->
                    binding.noDataIV.isVisible = schedules.isEmpty()
                    adapter.submitList(schedules)
                }
            }
        }
    }

    private fun updateScheduleTimeDialog(schedule: Schedule) {

        val dialogBinding = UpdateScheduleTimeDialogBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogBinding.pickedTime.text = timeFormat(schedule.scheduleTime)
        var newScheduleTime: Long? = schedule.scheduleTime
        dialogBinding.selectTimeButton.setOnClickListener {

            val cal = Calendar.getInstance()
            cal.timeInMillis = schedule.scheduleTime
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            val minute = cal.get(Calendar.MINUTE)

            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minuteOfDay ->
                newScheduleTime = getNextScheduledTime(hourOfDay, minuteOfDay)
                dialogBinding.pickedTime.text = timeFormat(newScheduleTime!!)
            }
            TimePickerDialog(requireContext(), timeSetListener, hour, minute, false).show()
        }

        dialogBinding.addScheduleBtn.setOnClickListener {

            val newSchedule = schedule.copy(scheduleTime = newScheduleTime!!)
            viewModel.checkTimeAvailability(newScheduleTime!!) { isAvailable ->
                if (isAvailable) {
                    viewModel.updateScheduleTime(newSchedule)
                    Toast.makeText(requireContext(), getString(R.string.time_updated), Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } else {
                    Toast.makeText(requireContext(), getString(R.string.this_time_slot_is_already_taken), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}