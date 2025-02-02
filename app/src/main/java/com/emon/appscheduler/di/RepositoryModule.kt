package com.emon.appscheduler.di

import com.emon.appscheduler.data.repository.ScheduleRepoImpl
import com.emon.appscheduler.data.repository.ScheduleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    fun bindScheduleRepository(scheduleRepoImpl: ScheduleRepoImpl): ScheduleRepository
}