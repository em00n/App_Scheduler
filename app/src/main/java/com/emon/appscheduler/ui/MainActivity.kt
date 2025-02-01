package com.emon.appscheduler.ui

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.emon.appscheduler.R
import com.emon.appscheduler.base.BaseActivity
import com.emon.appscheduler.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    private lateinit var navController: NavController

    override fun viewBindingLayout(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    override fun initializeView(savedInstanceState: Bundle?) {

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.mainNavHost) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)
    }
}