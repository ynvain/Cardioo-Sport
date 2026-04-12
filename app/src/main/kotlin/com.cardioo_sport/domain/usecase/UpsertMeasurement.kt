package com.cardioo_sport.domain.usecase

import com.cardioo_sport.domain.model.SportMeasurement
import com.cardioo_sport.domain.repository.MeasurementRepository
import javax.inject.Inject

class UpsertMeasurement @Inject constructor(
    private val repo: MeasurementRepository,
) {
    suspend operator fun invoke(measurement: SportMeasurement): Long = repo.upsert(measurement)
}

