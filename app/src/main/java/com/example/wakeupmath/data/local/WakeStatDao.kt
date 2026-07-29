package com.example.wakeupmath.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WakeStatDao {

    @Query("SELECT * FROM wake_stats ORDER BY timestamp DESC")
    fun getAllStats(): Flow<List<WakeStatEntity>>

    @Query("SELECT COUNT(*) FROM wake_stats WHERE success = 1")
    fun getTotalSolvedCount(): Flow<Int>

    @Query("SELECT AVG(solveTimeSeconds) FROM wake_stats WHERE success = 1")
    fun getAverageSolveTime(): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStat(stat: WakeStatEntity): Long
}
