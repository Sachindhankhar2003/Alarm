package com.example.wakeupmath.ui.ringing

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.wakeupmath.alarm.AlarmForegroundService
import com.example.wakeupmath.alarm.AlarmReceiver
import com.example.wakeupmath.theme.Charcoal
import com.example.wakeupmath.theme.WakeUpMathTheme

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class RingingActivity : ComponentActivity(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    private var lastShakeTime: Long = 0
    private var onShakeCallback: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register accelerometer sensor for shake mission
        sensorManager = getSystemService(SENSOR_SERVICE) as? SensorManager
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Show over lock screen and turn screen on
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        )

        val label = intent.getStringExtra(AlarmReceiver.EXTRA_ALARM_LABEL) ?: "Alarm"
        val difficulty = intent.getStringExtra(AlarmReceiver.EXTRA_ALARM_DIFFICULTY) ?: "MIXED"

        setContent {
            WakeUpMathTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Charcoal,
                ) {
                    RingingScreen(
                        label = label,
                        difficulty = difficulty,
                        onShakeDetected = { onShakeCallback?.invoke() },
                        onDismissed = {
                            AlarmForegroundService.instance?.stopAlarm()
                            finish()
                        },
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || event.sensor.type != Sensor.TYPE_ACCELEROMETER) return
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        val gForce = sqrt(x * x + y * y + z * z) / SensorManager.GRAVITY_EARTH
        if (gForce > 2.2f) { // Shake detected
            val now = System.currentTimeMillis()
            if (now - lastShakeTime > 300) { // debounce 300ms
                lastShakeTime = now
                onShakeCallback?.invoke()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    @Deprecated("Block back button during alarm")
    override fun onBackPressed() {
        // Do nothing — user must solve the math problem
    }

    override fun dispatchKeyEvent(event: android.view.KeyEvent): Boolean {
        when (event.keyCode) {
            android.view.KeyEvent.KEYCODE_VOLUME_DOWN,
            android.view.KeyEvent.KEYCODE_VOLUME_UP,
            android.view.KeyEvent.KEYCODE_VOLUME_MUTE -> {
                val audioManager = getSystemService(AUDIO_SERVICE) as? android.media.AudioManager
                audioManager?.let {
                    val maxVol = it.getStreamMaxVolume(android.media.AudioManager.STREAM_ALARM)
                    it.setStreamVolume(android.media.AudioManager.STREAM_ALARM, maxVol, 0)
                }
                return true // Block volume down key press!
            }
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        // If user tries to leave (home button), bring activity back
        val intent = intent
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}
