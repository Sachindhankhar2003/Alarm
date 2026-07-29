package com.example.wakeupmath.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_ALARM_ID = "alarm_id"
        const val EXTRA_ALARM_LABEL = "alarm_label"
        const val EXTRA_ALARM_DIFFICULTY = "alarm_difficulty"
        const val EXTRA_ALARM_REPEAT_DAYS = "alarm_repeat_days"
        const val EXTRA_ALARM_SOUND = "alarm_sound"
        const val EXTRA_ALARM_HOUR = "alarm_hour"
        const val EXTRA_ALARM_MINUTE = "alarm_minute"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val serviceIntent = Intent(context, AlarmForegroundService::class.java).apply {
            putExtra(EXTRA_ALARM_ID, intent.getIntExtra(EXTRA_ALARM_ID, -1))
            putExtra(EXTRA_ALARM_LABEL, intent.getStringExtra(EXTRA_ALARM_LABEL) ?: "Alarm")
            putExtra(EXTRA_ALARM_DIFFICULTY, intent.getStringExtra(EXTRA_ALARM_DIFFICULTY) ?: "MIXED")
            putExtra(EXTRA_ALARM_REPEAT_DAYS, intent.getStringExtra(EXTRA_ALARM_REPEAT_DAYS) ?: "")
            putExtra(EXTRA_ALARM_SOUND, intent.getStringExtra(EXTRA_ALARM_SOUND) ?: "HARSH SAWTOOTH")
            putExtra(EXTRA_ALARM_HOUR, intent.getIntExtra(EXTRA_ALARM_HOUR, 0))
            putExtra(EXTRA_ALARM_MINUTE, intent.getIntExtra(EXTRA_ALARM_MINUTE, 0))
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }
}
