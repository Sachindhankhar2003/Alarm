package com.example.wakeupmath.alarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.wakeupmath.R
import com.example.wakeupmath.ui.ringing.RingingActivity
import kotlin.math.sin

class AlarmForegroundService : Service() {

    companion object {
        const val CHANNEL_ID = "wakeupmath_alarm_channel"
        const val NOTIFICATION_ID = 9001
        const val ACTION_STOP = "com.example.wakeupmath.STOP_ALARM"

        @Volatile
        var instance: AlarmForegroundService? = null
            private set
    }

    private var audioTrack: AudioTrack? = null
    private var mediaPlayer: MediaPlayer? = null
    @Volatile
    private var isPlaying = false

    override fun onCreate() {
        super.onCreate()
        instance = this
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopAlarm()
            return START_NOT_STICKY
        }

        val alarmId = intent?.getIntExtra(AlarmReceiver.EXTRA_ALARM_ID, -1) ?: -1
        val label = intent?.getStringExtra(AlarmReceiver.EXTRA_ALARM_LABEL) ?: "Alarm"
        val difficulty = intent?.getStringExtra(AlarmReceiver.EXTRA_ALARM_DIFFICULTY) ?: "MIXED"
        val repeatDays = intent?.getStringExtra(AlarmReceiver.EXTRA_ALARM_REPEAT_DAYS) ?: ""
        val sound = intent?.getStringExtra(AlarmReceiver.EXTRA_ALARM_SOUND) ?: "HARSH SAWTOOTH"
        val hour = intent?.getIntExtra(AlarmReceiver.EXTRA_ALARM_HOUR, 0) ?: 0
        val minute = intent?.getIntExtra(AlarmReceiver.EXTRA_ALARM_MINUTE, 0) ?: 0

        // Build full-screen intent for the ringing activity
        val ringingIntent = Intent(this, RingingActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION)
            putExtra(AlarmReceiver.EXTRA_ALARM_ID, alarmId)
            putExtra(AlarmReceiver.EXTRA_ALARM_LABEL, label)
            putExtra(AlarmReceiver.EXTRA_ALARM_DIFFICULTY, difficulty)
            putExtra(AlarmReceiver.EXTRA_ALARM_REPEAT_DAYS, repeatDays)
            putExtra(AlarmReceiver.EXTRA_ALARM_SOUND, sound)
            putExtra(AlarmReceiver.EXTRA_ALARM_HOUR, hour)
            putExtra(AlarmReceiver.EXTRA_ALARM_MINUTE, minute)
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            this,
            alarmId,
            ringingIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("WakeUp Math")
            .setContentText("Alarm ringing — solve the math problem!")
            .setSmallIcon(R.drawable.ic_alarm)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setOngoing(true)
            .setAutoCancel(false)
            .build()

        startForeground(NOTIFICATION_ID, notification)

        // Start continuous max volume enforcement and alarm sound
        startAlarmSound(sound)

        // Also launch the ringing activity directly
        startActivity(ringingIntent)

        // Re-schedule if repeating
        if (repeatDays.isNotBlank() && alarmId != -1) {
            rescheduleRepeatingAlarm(alarmId, hour, minute, label, difficulty, repeatDays, sound)
        }

        return START_STICKY
    }

    private fun rescheduleRepeatingAlarm(
        id: Int, hour: Int, minute: Int, label: String, difficulty: String, repeatDays: String, sound: String
    ) {
        val alarm = com.example.wakeupmath.data.local.AlarmEntity(
            id = id, hour = hour, minute = minute,
            label = label, difficulty = difficulty, repeatDays = repeatDays, sound = sound, enabled = true,
        )
        AlarmScheduler.schedule(this, alarm)
    }

    private fun enforceMaxVolume() {
        Thread {
            val audioManager = getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return@Thread
            while (isPlaying) {
                try {
                    val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
                    audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVol, 0)
                    val maxMusicVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxMusicVol, 0)
                    Thread.sleep(500)
                } catch (e: Exception) {
                    break
                }
            }
        }.start()
    }

    private fun startAlarmSound(soundType: String) {
        if (isPlaying) return
        isPlaying = true

        enforceMaxVolume()

        if (soundType.startsWith("CUSTOM:") || soundType.startsWith("content://")) {
            val uriString = if (soundType.startsWith("CUSTOM:")) {
                soundType.substringAfter("CUSTOM:").substringBefore("|")
            } else {
                soundType
            }
            try {
                val uri = Uri.parse(uriString)
                val mp = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    setDataSource(applicationContext, uri)
                    isLooping = true
                    prepare()
                    setVolume(1.0f, 1.0f)
                    start()
                }
                mediaPlayer = mp
                return
            } catch (e: Exception) {
                e.printStackTrace()
                // Fallback to synth audio if custom media fails
            }
        }

        playSynthSound(soundType)
    }

    private fun playSynthSound(soundType: String) {
        Thread {
            try {
                val sampleRate = 44100
                val durationSeconds = 2
                val numSamples = sampleRate * durationSeconds
                val samples = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val sample = when (soundType.uppercase()) {
                        "SIREN PULSE" -> {
                            val freq = if ((t * 2).toInt() % 2 == 0) 800.0 else 1200.0
                            sin(2 * Math.PI * freq * t) * 0.9
                        }
                        "DIGITAL BEEP" -> {
                            val beepPulse = if ((t * 8).toInt() % 2 == 0) 1.0 else 0.0
                            sin(2 * Math.PI * 3000.0 * t) * beepPulse * 0.8
                        }
                        "SINE CHIME" -> {
                            val fundamental = sin(2 * Math.PI * 440.0 * t)
                            val harmonic = sin(2 * Math.PI * 880.0 * t) * 0.5
                            (fundamental + harmonic) * 0.7
                        }
                        "RADAR SWEEP" -> {
                            val sweepFreq = 500.0 + (t % 0.5) * 3000.0
                            sin(2 * Math.PI * sweepFreq * t) * 0.85
                        }
                        else -> { // HARSH SAWTOOTH (default)
                            val frequency = 2500.0
                            val phase = (t * frequency) % 1.0
                            val sawtooth = 2.0 * phase - 1.0
                            val harmonic = sin(2 * Math.PI * frequency * 1.5 * t) * 0.3
                            val pulse = if (sin(2 * Math.PI * 4.0 * t) > -0.2) 1.0 else 0.0
                            (sawtooth + harmonic) * pulse * 0.9
                        }
                    }

                    samples[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(
                        Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()
                    ).toShort()
                }

                val bufferSize = samples.size * 2

                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()

                val format = AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()

                audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setAudioFormat(format)
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack?.let { track ->
                    track.write(samples, 0, samples.size)
                    track.setLoopPoints(0, samples.size, -1) // Loop indefinitely
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        track.setVolume(1.0f)
                    }
                    track.play()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    fun stopAlarm() {
        isPlaying = false
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            audioTrack?.stop()
            audioTrack?.release()
            audioTrack = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAlarm()
        instance = null
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Alarm Notifications",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Channel for alarm notifications"
                setBypassDnd(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}
