package com.emon.appscheduler.scheduling

import com.emon.appscheduler.domain.usecase.ScheduleUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AppLaunchReceiverEntryPoint {
    fun scheduleUseCase(): ScheduleUseCase
}