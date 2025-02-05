
# App Scheduler 📅🚀
An Android app that allows users to schedule installed apps to launch automatically at a specific time, manage schedules, and track execution history.

## 📌 Features
✅ Schedule Apps – Select an installed app and set a time for it to launch automatically.
✅ Modify Schedule – Edit an existing schedule before execution.
✅ Cancel Schedule – Remove a scheduled app if it hasn't been launched yet.
✅ Multiple Schedules – Supports multiple scheduled apps with no time conflicts.
✅ Execution Records – Tracks whether scheduled apps were launched successfully.

## 🛠 Tech Stack
- Language: Kotlin
- Architecture: MVVM
- Database: Room Database (for storing schedules & execution logs)
- Background Processing: WorkManager / AlarmManager
- Dependency Injection: Hilt
- Concurrency: Kotlin Coroutines (for background tasks)
- Reactive Streams: Flow (for observing schedule updates)
- UI Components: XML

## 🛠 Implementation Details
1. Schedule Management
   - Uses Room Database to store scheduled apps.
   - Prevents time conflicts when adding new schedules.
2. App Launching
   - Uses PendingIntent + AlarmManager to start apps at the scheduled time.
3. Execution Tracking
   - Logs execution status and stores it in Room Database.
