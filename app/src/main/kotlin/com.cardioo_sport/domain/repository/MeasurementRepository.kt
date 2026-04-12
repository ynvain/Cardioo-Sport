package com.cardioo_sport.domain.repository

import com.cardioo_sport.domain.model.SportMeasurement
import kotlinx.coroutines.flow.Flow

interface MeasurementRepository {
    fun observeAll(): Flow<List<SportMeasurement>>
    fun observeCount(): Flow<Int>
    /**
     * Cursor-based paging for the readings list.
     *
     * - Pass null cursor to load the newest page.
     * - Pass (beforeTimestampEpochMillis, beforeId) from the last loaded item to load the next page.
     */
    suspend fun getPage(
        limit: Int,
        beforeTimestampEpochMillis: Long? = null,
        beforeId: Long? = null,
    ): List<SportMeasurement>
    suspend fun getById(id: Long): SportMeasurement?
    suspend fun upsert(measurement: SportMeasurement): Long
    suspend fun delete(id: Long)
}

