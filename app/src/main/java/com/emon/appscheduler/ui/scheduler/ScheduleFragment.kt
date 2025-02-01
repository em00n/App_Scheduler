package com.emon.appscheduler.ui.scheduler

import android.os.Bundle
import com.emon.appscheduler.base.BaseFragment
import com.emon.appscheduler.databinding.FragmentScheduleBinding

class ScheduleFragment : BaseFragment<FragmentScheduleBinding>() {

    override fun viewBindingLayout(): FragmentScheduleBinding = FragmentScheduleBinding.inflate(layoutInflater)

    override fun initializeView(savedInstanceState: Bundle?) {

    }
}