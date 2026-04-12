package com.cardioo_sport.presentation.entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cardioo_sport.R
import com.cardioo_sport.domain.model.ExerciseIntensity
import com.cardioo_sport.domain.model.SportMeasurement
import com.cardioo_sport.domain.model.UserProfile
import com.cardioo_sport.domain.model.exerciseIntensity
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

    fun computedExerciseIntensity(): ExerciseIntensity {
        return exerciseIntensity(
            _state.value.morningSteps,
            _state.value.noonSteps,
            _state.value.runningDistance,
            _state.value.cyclingDistance,
            _state.value.stretching
        )
    }


    fun validate(): Int? {
        return null
        /*
        val s = _state.value.morningSteps ?: return R.string.error_enter_systolic
        val d = _state.value.noonSteps ?: return R.string.error_enter_diastolic

        if (s !in 50..250) return R.string.error_systolic_range
        if (d !in 30..150) return R.string.error_diastolic_range

        val pulseText = _state.value.runningDistanceText.trim()
        if (pulseText.isNotEmpty()) {
            _state.value.runningDistance?.let { if (it !in 40..200) return R.string.error_pulse_range }

        }

        val weightText = _state.value.cyclingDistanceText.trim()
        if (weightText.isNotEmpty()) {
            val w = _state.value.cyclingDistance ?: return R.string.error_weight_invalid
            if (w <= 0.0) return R.string.error_weight_positive
            val wKg = if (_state.value.weightUnit == WeightUnit.KG) w else poundsToKg(w)
            if (wKg !in 20.0..300.0) return R.string.error_weight_unrealistic
        }

        return null*/
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

