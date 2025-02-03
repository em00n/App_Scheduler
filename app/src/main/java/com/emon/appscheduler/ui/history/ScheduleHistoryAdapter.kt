package com.emon.appscheduler.ui.history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.emon.appscheduler.R
import com.emon.appscheduler.base.BaseListAdapter
import com.emon.appscheduler.data.model.Schedule
import com.emon.appscheduler.databinding.ItemScheduleHistoryListBinding
import com.emon.appscheduler.utils.extensions.timeFormat

class ScheduleHistoryAdapter(): BaseListAdapter<Schedule, ItemScheduleHistoryListBinding>(
    diffCallback = object : DiffUtil.ItemCallback<Schedule>() {
        override fun areItemsTheSame(
            oldItem: Schedule,
            newItem: Schedule
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Schedule,
            newItem: Schedule
        ): Boolean {
            return oldItem == newItem
        }
    }
) {
    override fun createBinding(parent: ViewGroup): ItemScheduleHistoryListBinding =
        ItemScheduleHistoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    @SuppressLint("SetTextI18n")
    override fun bind(binding: ItemScheduleHistoryListBinding, item: Schedule, position: Int) {
        with(binding) {
            val packageManager = root.context.packageManager
            val appInfo = packageManager.getApplicationInfo(item.appPackageName, 0)
            val appIcon = appInfo.loadIcon(packageManager)
            iconIV.setImageDrawable(appIcon)

            nameTV.text = item.appName
            timeTV.text = "${root.context.getString(R.string.schedule_at)} ${timeFormat(item.scheduleTime)}"
            statusTV.text = item.status.name
        }
    }
}