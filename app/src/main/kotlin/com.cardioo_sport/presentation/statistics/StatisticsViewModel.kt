package com.cardioo_sport.presentation.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cardioo_sport.domain.model.ExerciseScore
import com.cardioo_sport.domain.model.SportMeasurement
import com.cardioo_sport.domain.model.exerciseScore
import com.cardioo_sport.domain.model.exerciseScoreCount
import com.cardioo_sport.domain.usecase.ObserveMeasurements
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    observeMeasurements: ObserveMeasurements,
) : ViewModel() {
    private val range = MutableStateFlow(Range.Month)

    val state: StateFlow<State> =
        combine(observeMeasurements(), range) { measurements, r ->
            val filtered = filterByRange(measurements, r)
            val filteredPrev =
                if (r != Range.AllTime) filterPrevByRange(measurements, r) else emptyList()
            val summary = Summary.from(filtered, filteredPrev)
            val table = TableStats.from(filtered)

            val periodLabelRes = when (r) {
                Range.Week -> com.cardioo_sport.R.string.range_week
                Range.Month -> com.cardioo_sport.R.string.range_month
                Range.SixMonths -> com.cardioo_sport.R.string.range_six_months
                Range.Year -> com.cardioo_sport.R.string.range_year
                Range.AllTime -> com.cardioo_sport.R.string.range_all_time
            }
            State(
                range = r,
                measurements = filtered,
                summary = summary,
                table = table,
                periodLabelRes = periodLabelRes,
            )
        }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), State())

    data class State(
        val range: Range = Range.Month,
        val measurements: List<SportMeasurement> = emptyList(),
        val summary: Summary = Summary(),
        val table: TableStats = TableStats(),
        val periodLabelRes: Int = com.cardioo_sport.R.string.range_month,
    )

    data class Summary(
        val latest: SportMeasurement? = null,
        val averageExerciseScoreCount: Int? = null,
        val averageExerciseScore: ExerciseScore? = null,
        val prevAverageExerciseScoreCount: Int? = null,
        val prevAverageExerciseScore: ExerciseScore? = null,
        val stretchingCount: Int = 0,
        val count: Int = 0,
    ) {
        companion object {
            fun from(list: List<SportMeasurement>, prevList: List<SportMeasurement>): Summary {
                val latest = list.firstOrNull()
                if (list.isEmpty()) return Summary(latest = null, count = 0)
                val averageExerciseScoreCount =
                    list.map { exerciseScoreCount(it) }.average().toInt()
                val averageExerciseScore = exerciseScore(averageExerciseScoreCount)
                var prevAverageExerciseScoreCount: Int? = null
                var prevAverageExerciseScore: ExerciseScore? = null
                if (prevList.isNotEmpty()) {
                    prevAverageExerciseScoreCount =
                        prevList.map { exerciseScoreCount(it) }.average().toInt()
                    prevAverageExerciseScore = exerciseScore(prevAverageExerciseScoreCount)
                }
                val stretchingCount = list.count { it.stretching }

                return Summary(
                    latest = latest,
                    averageExerciseScoreCount = averageExerciseScoreCount,
                    averageExerciseScore = averageExerciseScore,
                    prevAverageExerciseScoreCount = prevAverageExerciseScoreCount,
                    prevAverageExerciseScore = prevAverageExerciseScore,
                    stretchingCount = stretchingCount,
                    count = list.size,
                )
            }
        }
    }

    data class TableStats(
        val minMorningSteps: Int? = null,
        val maxMorningSteps: Int? = null,
        val avgMorningSteps: Int? = null,
        val minNoonSteps: Int? = null,
        val maxNoonSteps: Int? = null,
        val avgNoonSteps: Int? = null,
        val minRunningDistance: Double? = null,
        val maxRunningDistance: Double? = null,
        val avgRunningDistance: Double? = null,
        val minCyclingDistance: Double? = null,
        val maxCyclingDistance: Double? = null,
        val avgCyclingDistance: Double? = null,
        val totalMorningSteps: Int? = null,
        val totalNoonSteps: Int? = null,
        val totalRunningDistance: Double? = null,
        val totalCyclingDistance: Double? = null,
    ) {
        companion object {
            fun from(list: List<SportMeasurement>): TableStats {
                if (list.isEmpty()) return TableStats()
                val morningStepsList = list.mapNotNull { it.morningSteps }
                val noonStepsList = list.mapNotNull { it.noonSteps }
                val runningDistanceList = list.mapNotNull { it.runningDistance }
                val cyclingDistanceList = list.mapNotNull { it.cyclingDistance }
                return TableStats(
                    minMorningSteps = morningStepsList.minOrNull(),
                    maxMorningSteps = morningStepsList.maxOrNull(),
                    avgMorningSteps = morningStepsList.takeIf { it.isNotEmpty() }?.average()
                        ?.toInt(),
                    minNoonSteps = noonStepsList.minOrNull(),
                    maxNoonSteps = noonStepsList.maxOrNull(),
                    avgNoonSteps = noonStepsList.takeIf { it.isNotEmpty() }?.average()?.toInt(),
                    minRunningDistance = runningDistanceList.minOrNull(),
                    maxRunningDistance = runningDistanceList.maxOrNull(),
                    avgRunningDistance = runningDistanceList.takeIf { it.isNotEmpty() }?.average(),
                    minCyclingDistance = cyclingDistanceList.minOrNull(),
                    maxCyclingDistance = cyclingDistanceList.maxOrNull(),
                    avgCyclingDistance = cyclingDistanceList.takeIf { it.isNotEmpty() }?.average(),
                    totalMorningSteps =
                        morningStepsList.takeIf { it.isNotEmpty() }?.sum(),
                    totalNoonSteps =
                        noonStepsList.takeIf { it.isNotEmpty() }?.sum(),
                    totalRunningDistance = runningDistanceList.takeIf { it.isNotEmpty() }?.sum(),
                    totalCyclingDistance = cyclingDistanceList.takeIf { it.isNotEmpty() }?.sum()
                )
            }
        }
    }

    enum class Range {
        Week, Month, SixMonths, Year, AllTime
    }

    fun setRange(v: Range) = range.update { v }

    private fun filterByRange(
        measurements: List<SportMeasurement>,
        range: Range,
    ): List<SportMeasurement> {
        if (range == Range.AllTime) return measurements
        val days = when (range) {
            Range.Week -> 7
            Range.Month -> 30
            Range.SixMonths -> 180
            Range.Year -> 365
            else -> 0
        }
        val cutoff = ZonedDateTime.now().minusDays(days.toLong()).toInstant().toEpochMilli()
        return measurements.filter { it.timestampEpochMillis >= cutoff }
    }

    // Filter measurements from prev period
    private fun filterPrevByRange(
        measurements: List<SportMeasurement>,
        range: Range,
    ): List<SportMeasurement> {
        if (range == Range.AllTime) return measurements
        val days = when (range) {
            Range.Week -> 7
            Range.Month -> 30
            Range.SixMonths -> 180
            Range.Year -> 365
            else -> 0
        }
        val start = ZonedDateTime.now().minusDays(days.toLong()).toInstant().toEpochMilli()
        val finish = ZonedDateTime.now().minusDays(2 * days.toLong()).toInstant().toEpochMilli()
        return measurements.filter { it.timestampEpochMillis in finish..start }
    }
}

