package com.cardioo_sport.presentation.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cardioo_sport.R
import com.cardioo_sport.domain.model.ExerciseScore
import com.cardioo_sport.domain.model.SportMeasurement
import com.cardioo_sport.domain.model.exerciseScore
import com.cardioo_sport.domain.model.exerciseScoreCount
import com.cardioo_sport.domain.usecase.ObserveMeasurements
import com.cardioo_sport.domain.usecase.ObserveProfile
import com.cardioo_sport.presentation.util.Range
import com.cardioo_sport.presentation.util.filterByRange
import com.cardioo_sport.presentation.util.filterPrevByRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class StatisticsViewModel @Inject constructor(
    observeMeasurements: ObserveMeasurements,
    observeProfile: ObserveProfile,
) : ViewModel() {
    private val range = MutableStateFlow(Range.ThisYear)

    val state: StateFlow<State> =
        combine(observeMeasurements(), observeProfile(), range) { measurements, profile, r ->
            val filtered = filterByRange(measurements, r)
            val filteredPrev =
                filterPrevByRange(
                    measurements,
                    r
                )
            val summary = Summary.from(filtered, filteredPrev, profile?.stepLength ?: 1.0)
            val table = TableStats.from(filtered)

            val periodLabelRes = when (r) {
                Range.Week -> R.string.range_week
                Range.Month -> R.string.range_month
                Range.SixMonths -> R.string.range_six_months
                Range.ThisYear -> R.string.range_this_year
                Range.PreviousYear -> R.string.range_previous_year
                Range.Year -> R.string.range_year
                Range.AllTime -> R.string.range_all_time
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
        val periodLabelRes: Int = R.string.range_month,
    )

    data class Summary(
        val latest: SportMeasurement? = null,
        val averageExerciseScoreCount: Int? = null,
        val averageExerciseScore: ExerciseScore? = null,
        val prevAverageExerciseScoreCount: Int? = null,
        val prevAverageExerciseScore: ExerciseScore? = null,
        val walkingCount: Int? = null,
        val runningCount: Int? = null,
        val cyclingCount: Int? = null,
        val stretchingCount: Int? = null,
        val count: Int = 0,
        val totalWalkingDistance: Double = 0.0
    ) {
        companion object {
            fun from(
                list: List<SportMeasurement>,
                prevList: List<SportMeasurement>,
                stepLength: Double
            ): Summary {
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
                val walkingCount =
                    (list.count { it.morningSteps != null } + list.count { it.noonSteps != null }).takeIf { it != 0 }
                val totalSteps =
                    list.mapNotNull { it.morningSteps }.sum() + list.mapNotNull { it.noonSteps }
                        .sum()
                val totalWalkingDistance = (totalSteps * stepLength) / 1000
                val runningCount = list.count { it.runningDistance != null }.takeIf { it != 0 }
                val cyclingCount = list.count { it.cyclingDistance != null }.takeIf { it != 0 }
                val stretchingCount = list.count { it.stretching }.takeIf { it != 0 }

                return Summary(
                    latest = latest,
                    averageExerciseScoreCount = averageExerciseScoreCount,
                    averageExerciseScore = averageExerciseScore,
                    prevAverageExerciseScoreCount = prevAverageExerciseScoreCount,
                    prevAverageExerciseScore = prevAverageExerciseScore,
                    walkingCount = walkingCount,
                    runningCount = runningCount,
                    cyclingCount = cyclingCount,
                    stretchingCount = stretchingCount,
                    count = list.size,
                    totalWalkingDistance = totalWalkingDistance
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


    fun setRange(v: Range) = range.update { v }

}

