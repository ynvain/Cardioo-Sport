package com.cardioo_sport.domain.model

data class SportMeasurement(
    val id: Long = 0L,
    val userId: Long,
    val timestampEpochMillis: Long,
    val morningSteps: Int?,
    val noonSteps: Int?,
    val runningDistance: Double?,
    val cyclingDistance: Double?,
    val stretching: Boolean,
    val notes: String?,
)
