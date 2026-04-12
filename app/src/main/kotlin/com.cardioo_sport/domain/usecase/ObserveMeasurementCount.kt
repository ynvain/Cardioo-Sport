package com.cardioo_sport.domain.usecase

import com.cardioo_sport.domain.repository.MeasurementRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveMeasurementCount @Inject constructor(
    private val repo: MeasurementRepository,
) {
    operator fun invoke(): Flow<Int> = repo.observeCount()
}

