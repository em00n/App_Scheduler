package com.emon.appscheduler.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.emon.appscheduler.data.repository.ScheduleRepository
import com.emon.appscheduler.domain.usecase.ScheduleUseCase
import com.emon.appscheduler.scheduling.AppScheduler
import com.emon.appscheduler.data.local.AppDatabase
import com.emon.appscheduler.data.local.ScheduleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideAppContext(application: Application): Context = application

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_schedule_database"
        ).build()
    }

    @Provides
    fun provideScheduleDao(database: AppDatabase): ScheduleDao {
        return database.scheduleDao()
    }


    @Provides
    @Singleton
    fun provideScheduleUseCase(scheduleRepository: ScheduleRepository): ScheduleUseCase {
        return ScheduleUseCase(scheduleRepository)
    }

    @Provides
    @Singleton
    fun provideAppScheduler(
        context: Context
    ): AppScheduler {
        return AppScheduler(context)
    }
}
