package com.cardioo_sport.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cardioo_sport.domain.model.SportMeasurement
import com.cardioo_sport.domain.model.UserProfile
import com.cardioo_sport.domain.repository.MeasurementRepository
import com.cardioo_sport.domain.usecase.ObserveProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    observeProfile: ObserveProfile,
    private val measurementRepository:
    MeasurementRepository,
) : ViewModel() {
    private val measurements =
        MutableStateFlow<Map<YearMonth, Map<LocalDate, SportMeasurement>>>(emptyMap())

    val currentMonth = MutableStateFlow<YearMonth>(YearMonth.now())
    private var activeAccountId: Long? = null

    data class State(
        val measurements: Map<YearMonth, Map<LocalDate, SportMeasurement>> = emptyMap(),
        val currentMonth: YearMonth = YearMonth.now(),
        val profile: UserProfile? = null,
    )

    val state: StateFlow<State> =
        combine(
            measurements,
            currentMonth,
            observeProfile(),
        ) { measurements, currentMonth, profile ->
            State(measurements = measurements, currentMonth = currentMonth, profile = profile)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), State())


    init {
        viewModelScope.launch {
            observeProfile()
                .map { it?.id }
                .distinctUntilChanged()
                .collect { accountId ->
                    // On account switch we reset list state before loading data for the new account.
                    activeAccountId = accountId
                    clear()
                    load()
                }
        }
    }

    fun clear() {
        measurements.value = emptyMap()
    }

    fun load() {
        if (measurements.value.containsKey(currentMonth.value)) {
            return
        }
        viewModelScope.launch {
            val startTimestamp =
                currentMonth.value.atDay(1).atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
            val endTimestamp =
                currentMonth.value.atEndOfMonth().plusDays(1).atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
            val list =
                measurementRepository.getInTimestampRangeForUser(startTimestamp, endTimestamp)
            if (list.isNotEmpty()) {
                measurements.update { it ->
                    val mutableMap = it.toMutableMap()
                    val monthMap =
                        list.associateBy({ toLocalDate(it.timestampEpochMillis) }, { it })
                    mutableMap[currentMonth.value] = monthMap
                    mutableMap
                }
            }
        }
    }

    fun toLocalDate(timestampEpochMillis: Long) =
        Instant.fromEpochMilliseconds(timestampEpochMillis)
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
}