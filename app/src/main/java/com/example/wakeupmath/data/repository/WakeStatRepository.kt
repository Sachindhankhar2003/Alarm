package com.example.wakeupmath.data.repository

import com.example.wakeupmath.data.local.WakeStatDao
import com.example.wakeupmath.data.local.WakeStatEntity
import kotlinx.coroutines.flow.Flow

class WakeStatRepository(private val wakeStatDao: WakeStatDao) {

    val allStats: Flow<List<WakeStatEntity>> = wakeStatDao.getAllStats()
    val totalSolvedCount: Flow<Int> = wakeStatDao.getTotalSolvedCount()
    val averageSolveTime: Flow<Double?> = wakeStatDao.getAverageSolveTime()

    suspend fun insertStat(stat: WakeStatEntity): Long = wakeStatDao.insertStat(stat)
}
