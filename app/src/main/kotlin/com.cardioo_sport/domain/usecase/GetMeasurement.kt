package com.cardioo_sport.domain.usecase

import com.cardioo_sport.domain.model.SportMeasurement
import com.cardioo_sport.domain.repository.MeasurementRepository
import javax.inject.Inject

class GetMeasurement @Inject constructor(
    private val repo: MeasurementRepository,
) {
    suspend operator fun invoke(id: Long): SportMeasurement? = repo.getById(id)
}

