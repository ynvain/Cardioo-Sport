package com.cardioo_sport.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cardioo_sport.domain.model.Gender
import com.cardioo_sport.domain.model.HeightUnit
import com.cardioo_sport.domain.model.UserProfile
import com.cardioo_sport.domain.model.WeightUnit
import com.cardioo_sport.domain.usecase.ObserveProfile
import com.cardioo_sport.domain.usecase.UpsertProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeProfile: ObserveProfile,
    private val upsertProfile: UpsertProfile,
) : ViewModel() {
    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    data class State(
        val accountId: Long? = null,
        val name: String = "",
        val loaded: Boolean = false,
        val heightText: String = "",
        val heightUnit: HeightUnit = HeightUnit.CM,
        val weightUnit: WeightUnit = WeightUnit.KG,
        val dob: LocalDate? = null,
        val gender: Gender? = null,
        val error: String? = null,
        val saving: Boolean = false,
        val stepLengthText: String = "",
    )

    init {
        viewModelScope.launch {
            observeProfile().collectLatest { profile ->
                if (profile == null) return@collectLatest
                _state.update {
                    it.copy(
                        accountId = profile.id,
                        name = profile.name,
                        loaded = true,
                        heightText = profile.height.toString(),
                        heightUnit = profile.heightUnit,
                        weightUnit = profile.weightUnit,
                        dob = profile.dateOfBirth,
                        gender = profile.gender,
                        stepLengthText = profile.stepLength.toString()
                    )
                }
            }
        }
    }

    fun setName(v: String) = _state.update { it.copy(name = v) }
    fun setHeightText(v: String) =
        _state.update { it.copy(heightText = v.filter { c -> c.isDigit() || c == '.' }) }

    fun setDob(v: LocalDate?) = _state.update { it.copy(dob = v) }
    fun setGender(v: Gender?) = _state.update { it.copy(gender = v) }

    fun setStepLengthText(v: String) =
        _state.update { it.copy(stepLengthText = v.filter { c -> c.isDigit() || c == '.' }) }

    fun save(onDone: () -> Unit) {
        val name = _state.value.name.trim()
        val height = _state.value.heightText.toDoubleOrNull()
        val stepLength = _state.value.stepLengthText.toDoubleOrNull()
        if (name.isBlank()) {
            _state.update { it.copy(error = "Account name is required.") }
            return
        }
        if (height == null || height <= 0.0) {
            _state.update { it.copy(error = "Height is required.") }
            return
        }
        if (stepLength == null || stepLength <= 0.0) {
            _state.update { it.copy(error = "Step Length is required.") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(saving = true, error = null) }
            upsertProfile(
                UserProfile(
                    id = _state.value.accountId ?: 0L,
                    name = name,
                    height = height,
                    heightUnit = _state.value.heightUnit,
                    weightUnit = _state.value.weightUnit,
                    dateOfBirth = _state.value.dob,
                    gender = _state.value.gender,
                    stepLength = stepLength,
                ),
            )
            _state.update { it.copy(saving = false) }
            onDone()
        }
    }
}

