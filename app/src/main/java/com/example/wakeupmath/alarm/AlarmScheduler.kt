package com.example.wakeupmath.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.wakeupmath.data.local.AlarmEntity
import java.util.Calendar

object AlarmScheduler {

    fun schedule(context: Context, alarm: AlarmEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Check permission for exact alarms on Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                return // Permission not granted — caller should request it
            }
        }

        if (alarm.repeatDays.isBlank()) {
            // One-shot alarm
            val triggerTime = getNextTriggerTime(alarm.hour, alarm.minute)
            val pendingIntent = createPendingIntent(context, alarm)
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent,
            )
        } else {
            // Repeating alarm — schedule for the next applicable day
            val days = alarm.repeatDays.split(",").mapNotNull { it.trim().toIntOrNull() }
            val nextTrigger = getNextRepeatTriggerTime(alarm.hour, alarm.minute, days)
            if (nextTrigger != null) {
                val pendingIntent = createPendingIntent(context, alarm)
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    nextTrigger,
                    pendingIntent,
                )
            }
        }
    }

    fun cancel(context: Context, alarm: AlarmEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = createPendingIntent(context, alarm)
        alarmManager.cancel(pendingIntent)
    }

    private fun createPendingIntent(context: Context, alarm: AlarmEntity): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_ALARM_ID, alarm.id)
            putExtra(AlarmReceiver.EXTRA_ALARM_LABEL, alarm.label)
            putExtra(AlarmReceiver.EXTRA_ALARM_DIFFICULTY, alarm.difficulty)
            putExtra(AlarmReceiver.EXTRA_ALARM_REPEAT_DAYS, alarm.repeatDays)
            putExtra(AlarmReceiver.EXTRA_ALARM_SOUND, alarm.sound)
            putExtra(AlarmReceiver.EXTRA_ALARM_HOUR, alarm.hour)
            putExtra(AlarmReceiver.EXTRA_ALARM_MINUTE, alarm.minute)
        }
        return PendingIntent.getBroadcast(
            context,
            alarm.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun getNextTriggerTime(hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return calendar.timeInMillis
    }

    private fun getNextRepeatTriggerTime(hour: Int, minute: Int, days: List<Int>): Long? {
        if (days.isEmpty()) return null
        val now = Calendar.getInstance()
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        // Calendar.DAY_OF_WEEK: Sunday=1..Saturday=7
        // Our days: Mon=0..Sun=6
        // Convert our format to Calendar format
        fun ourDayToCalendarDay(ourDay: Int): Int {
            return when (ourDay) {
                0 -> Calendar.MONDAY
                1 -> Calendar.TUESDAY
                2 -> Calendar.WEDNESDAY
                3 -> Calendar.THURSDAY
                4 -> Calendar.FRIDAY
                5 -> Calendar.SATURDAY
                6 -> Calendar.SUNDAY
                else -> Calendar.MONDAY
            }
        }

        // Try up to 7 days ahead
        for (i in 0..7) {
            val testCal = calendar.clone() as Calendar
            testCal.add(Calendar.DAY_OF_YEAR, i)
            val calDayOfWeek = testCal.get(Calendar.DAY_OF_WEEK)
            val matchesDay = days.any { ourDayToCalendarDay(it) == calDayOfWeek }
            if (matchesDay && testCal.timeInMillis > now.timeInMillis) {
                return testCal.timeInMillis
            }
        }
        return null
    }
}
