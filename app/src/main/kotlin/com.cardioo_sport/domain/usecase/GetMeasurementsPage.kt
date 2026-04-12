package com.cardioo_sport.domain.usecase

import com.cardioo_sport.domain.model.SportMeasurement
import com.cardioo_sport.domain.repository.MeasurementRepository
import javax.inject.Inject

class GetMeasurementsPage @Inject constructor(
    private val repo: MeasurementRepository,
) {
    /**
     * Load a page for the readings list using cursor pagination.
     *
     * Cursor should be taken from the last item currently loaded.
     */
    suspend operator fun invoke(
        limit: Int,
        beforeTimestampEpochMillis: Long? = null,
        beforeId: Long? = null,
    ): List<SportMeasurement> =
        repo.getPage(
            limit = limit,
            beforeTimestampEpochMillis = beforeTimestampEpochMillis,
            beforeId = beforeId,
        )
}

