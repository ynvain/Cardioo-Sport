package com.cardioo_sport.presentation.entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cardioo_sport.R
import com.cardioo_sport.domain.model.ExerciseScore
import com.cardioo_sport.domain.model.SportMeasurement
import com.cardioo_sport.domain.model.UserProfile
import com.cardioo_sport.domain.model.exerciseScore
import com.cardioo_sport.domain.usecase.GetMeasurement
import com.cardioo_sport.domain.usecase.ObserveProfile
import com.cardioo_sport.domain.usecase.UpsertMeasurement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MeasurementEntryViewModel @Inject constructor(
    private val getMeasurement: GetMeasurement,
    private val upsertMeasurement: UpsertMeasurement,
    private val observeProfile: ObserveProfile,
) : ViewModel() {
    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    data class State(
        val loading: Boolean = true,
        val measurementId: Long? = null,
        val timestampEpochMillis: Long = System.currentTimeMillis(),
        val morningStepsText: String = "",
        val noonStepsText: String = "",
        val runningDistanceText: String = "",
        val cyclingDistanceText: String = "",
        val stretching: Boolean = false,
        val notes: String = "",
        val profile: UserProfile? = null,
        val error: Int? = null,
        val saving: Boolean = false,
    ) {
        val morningSteps: Int? get() = morningStepsText.toIntOrNull()
        val noonSteps: Int? get() = noonStepsText.toIntOrNull()
        val runningDistance: Double? get() = runningDistanceText.toDoubleOrNull()
        val cyclingDistance: Double? get() = cyclingDistanceText.toDoubleOrNull()
    }

    fun load(measurementId: Long?) {
        viewModelScope.launch {
            val profile = observeProfile().first()
            _state.update { it.copy(profile = profile) }

            if (measurementId == null) {
                _state.update {
                    it.copy(
                        loading = false,
                        measurementId = null,
                    )
                }
                return@launch
            }

            val m = getMeasurement(measurementId)
            if (m == null) {
                _state.update {
                    it.copy(
                        loading = false,
                        measurementId = null,
                        error = R.string.error_reading_not_found
                    )
                }
                return@launch
            }
            _state.update {
                it.copy(
                    loading = false,
                    measurementId = m.id,
                    timestampEpochMillis = m.timestampEpochMillis,
                    morningStepsText = m.morningSteps?.toString().orEmpty(),
                    noonStepsText = m.noonSteps?.toString().orEmpty(),
                    runningDistanceText = m.runningDistance?.toString().orEmpty(),
                    cyclingDistanceText = m.cyclingDistance?.toString().orEmpty(),
                    stretching = m.stretching,
                    notes = m.notes.orEmpty(),
                )
            }
        }
    }

    fun setTimestamp(epochMillis: Long) =
        _state.update { it.copy(timestampEpochMillis = epochMillis) }

    fun setMorningStepsText(v: String) =
        _state.update { it.copy(morningStepsText = v.filter(Char::isDigit)) }

    fun setNoonStepsText(v: String) =
        _state.update { it.copy(noonStepsText = v.filter(Char::isDigit)) }

    fun setRunningDistanceText(v: String) =
        _state.update { it.copy(runningDistanceText = v.filter(Char::isDigit)) }

    fun setCyclingDistanceText(v: String) =
        _state.update { it.copy(cyclingDistanceText = v.filter { c -> c.isDigit() || c == '.' }) }

    fun setStretching(v: Boolean) =
        _state.update { it.copy(stretching = v) }

    fun setNotes(v: String) = _state.update { it.copy(notes = v) }

    fun computedExerciseScore(): ExerciseScore {
        return exerciseScore(
            _state.value.morningSteps,
            _state.value.noonSteps,
            _state.value.runningDistance,
            _state.value.cyclingDistance,
            _state.value.stretching
        )
    }


    fun validate(): Int? {
        if (_state.value.morningSteps == null && _state.value.noonSteps == null && _state.value.runningDistance == null &&
            _state.value.cyclingDistance == null && !_state.value.stretching) return R.string.error_enter_any_params
        return null
    }

    fun save(onDone: () -> Unit) {
        val err = validate()
        if (err != null) {
            _state.update { it.copy(error = err) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(saving = true, error = null) }
            upsertMeasurement(
                SportMeasurement(
                    id = _state.value.measurementId ?: 0L,
                    userId = _state.value.profile?.id ?: 0L,
                    timestampEpochMillis = _state.value.timestampEpochMillis,
                    morningSteps = _state.value.morningSteps,
                    noonSteps = _state.value.noonSteps,
                    runningDistance = _state.value.runningDistance,
                    cyclingDistance = _state.value.cyclingDistance,
                    stretching = _state.value.stretching,
                    notes = _state.value.notes.trim().ifBlank { null },
                ),
            )
            _state.update { it.copy(saving = false) }
            onDone()
        }
    }
}

