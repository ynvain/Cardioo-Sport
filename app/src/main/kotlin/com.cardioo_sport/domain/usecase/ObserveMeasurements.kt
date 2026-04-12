package com.cardioo_sport.domain.usecase

import com.cardioo_sport.domain.model.SportMeasurement
import com.cardioo_sport.domain.repository.MeasurementRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveMeasurements @Inject constructor(
    private val repo: MeasurementRepository,
) {
    operator fun invoke(): Flow<List<SportMeasurement>> = repo.observeAll()
}

