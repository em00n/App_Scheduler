package com.emon.appscheduler.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.emon.appscheduler.data.model.AppInfo
import com.emon.appscheduler.databinding.ItemAppListBinding

class AppInfoSpinnerAdapter(
    context: Context,
    private val appInfoList: List<AppInfo>
) : ArrayAdapter<AppInfo>(context, 0, appInfoList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    override fun isEnabled(position: Int): Boolean {
        return position != 0
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemAppListBinding
        if (convertView == null) {
            binding = ItemAppListBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
            binding.root.tag = binding
        } else {
            binding = convertView.tag as ItemAppListBinding
        }

        val appInfo = appInfoList[position]
        binding.iconIV.setImageDrawable(appInfo.appIcon)
        binding.nameTV.text = appInfo.appName
        return binding.root
    }
}