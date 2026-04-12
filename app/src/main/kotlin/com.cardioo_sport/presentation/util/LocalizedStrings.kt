package com.cardioo_sport.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.cardioo_sport.R
import com.cardioo_sport.domain.model.ExerciseIntensity
import com.cardioo_sport.domain.model.Gender
import com.cardioo_sport.domain.model.HeightUnit
import com.cardioo_sport.domain.model.WeightUnit

@Composable
fun localizeGender(gender: Gender): String =
    when (gender) {
        Gender.Male -> stringResource(R.string.gender_male)
        Gender.Female -> stringResource(R.string.gender_female)
    }

@Composable
fun weightUnitString(unit: WeightUnit): String =
    when (unit) {
        WeightUnit.KG -> stringResource(R.string.unit_kg)
        WeightUnit.LB -> stringResource(R.string.unit_lb)
    }

@Composable
fun heightUnitString(unit: HeightUnit): String =
    when (unit) {
        HeightUnit.CM -> stringResource(R.string.unit_cm)
        HeightUnit.IN -> stringResource(R.string.unit_in)
    }
