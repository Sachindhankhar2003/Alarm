package com.example.wakeupmath.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.wakeupmath.data.local.AlarmDatabase
import com.example.wakeupmath.data.repository.AlarmRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Re-schedule all enabled alarms after boot
            val database = AlarmDatabase.getInstance(context)
            val repository = AlarmRepository(database.alarmDao())

            CoroutineScope(Dispatchers.IO).launch {
                val alarms = repository.getEnabledAlarms()
                for (alarm in alarms) {
                    AlarmScheduler.schedule(context, alarm)
                }
            }
        }
    }
}
