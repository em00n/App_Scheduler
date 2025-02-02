package com.emon.appscheduler.utils

enum class ScheduleStatus {
    PENDING,    // Waiting to be executed
    EXECUTED,   // Successfully launched the app
    FAILED,     // Scheduled but failed to launch
    CANCEL      // Cancel Schedule
}