package com.example.wakeupmath.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wake_stats")
data class WakeStatEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val solveTimeSeconds: Long,
    val difficulty: String,
    val success: Boolean = true
)
