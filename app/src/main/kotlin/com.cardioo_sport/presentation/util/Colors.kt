package com.cardioo_sport.presentation.util

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cardioo_sport.domain.model.ExerciseIntensity


val Orange = Color(0xFFFFA726);

@Composable
fun toggleButtonBorder(toggle: Boolean): BorderStroke {
    val toggledButtonBorder = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    val untoggledButtonBorder = ButtonDefaults.outlinedButtonBorder(true)
    return if (toggle) toggledButtonBorder
    else untoggledButtonBorder
}


fun intensityColor(category: ExerciseIntensity): Color {
    return when (category) {
        ExerciseIntensity.Low -> Orange
        ExerciseIntensity.Medium -> Color(0xFFBEDC39)
        ExerciseIntensity.High -> Color(0xFF009650)
        ExerciseIntensity.None -> Color.White
    }
}