package com.cardioo_sport.presentation.chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cardioo_sport.domain.model.SportMeasurement
import com.cardioo_sport.domain.usecase.ObserveMeasurements
import com.cardioo_sport.presentation.util.Range
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ChartViewModel @Inject constructor(
    observeMeasurements: ObserveMeasurements,
) : ViewModel() {
    private val metric = MutableStateFlow(Metric.Steps)
    private val range = MutableStateFlow(Range.Month)

    val state: StateFlow<State> =
        combine(observeMeasurements(), metric, range) { measurements, m, r ->
            State(
                metric = m,
                range = r,
                measurements = measurements,
            )
        }.stateIn(
            viewModelScope,
            kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5_000),
            State()
        )

    data class State(
        val metric: Metric = Metric.Steps,
        val range: Range = Range.ThisYear,
        val measurements: List<SportMeasurement> = emptyList(),
    )

    enum class Metric { Steps, RunningDistance, CyclingDistance }

    fun setMetric(v: Metric) = metric.update { v }
    fun setRange(v: Range) = range.update { v }
}

