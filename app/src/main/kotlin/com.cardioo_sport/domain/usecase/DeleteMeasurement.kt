package com.cardioo_sport.domain.usecase

import com.cardioo_sport.domain.repository.MeasurementRepository
import javax.inject.Inject

class DeleteMeasurement @Inject constructor(
    private val repo: MeasurementRepository,
) {
    suspend operator fun invoke(id: Long) = repo.delete(id)
}

