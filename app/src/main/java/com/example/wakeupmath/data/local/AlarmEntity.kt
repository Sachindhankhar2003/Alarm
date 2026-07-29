package com.example.wakeupmath.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val hour: Int,
    val minute: Int,
    val label: String = "",
    val difficulty: String = "MIXED", // TRIGONOMETRY, ALGEBRA, CALCULUS, LOGARITHMS, MIXED
    val repeatDays: String = "",       // Comma-separated: "0,1,2,3,4,5,6" (Mon=0..Sun=6)
    val sound: String = "HARSH SAWTOOTH", // HARSH SAWTOOTH, SIREN PULSE, DIGITAL BEEP, SINE CHIME, RADAR SWEEP
    val enabled: Boolean = true,
)
