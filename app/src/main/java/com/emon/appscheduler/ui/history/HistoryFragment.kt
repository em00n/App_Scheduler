package com.emon.appscheduler.ui.history

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.emon.appscheduler.base.BaseFragment
import com.emon.appscheduler.databinding.FragmentHistoryBinding
import com.emon.appscheduler.ui.ScheduleViewModel
import com.emon.appscheduler.utils.ScheduleStatus
import com.emon.appscheduler.utils.autoCleared
import com.emon.appscheduler.utils.extensions.setUpVerticalRecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryFragment : BaseFragment<FragmentHistoryBinding>() {

    override fun viewBindingLayout(): FragmentHistoryBinding =
        FragmentHistoryBinding.inflate(layoutInflater)

    private val viewModel by viewModels<ScheduleViewModel>()
    private var adapter by autoCleared<ScheduleHistoryAdapter>()

    override fun initializeView(savedInstanceState: Bundle?) {

        adapter = ScheduleHistoryAdapter()
        requireContext().setUpVerticalRecyclerView(binding.scheduleHistoryRV, adapter)

        viewModel.getScheduleList(ScheduleStatus.EXECUTED)
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
}