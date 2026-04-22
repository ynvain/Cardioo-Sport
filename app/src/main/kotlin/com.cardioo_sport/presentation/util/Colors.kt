package com.cardioo_sport.presentation.util

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cardioo_sport.domain.model.ExerciseScore


val Orange = Color(0xFFFFA726);
val Violet = Color(0xFF5B3AB7);

@Composable
fun toggleButtonBorder(toggle: Boolean): BorderStroke {
    return if (toggle) BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    else ButtonDefaults.outlinedButtonBorder(true)
}


fun scoreColor(category: ExerciseScore): Color {
    return when (category) {
        ExerciseScore.Low -> Orange
        ExerciseScore.Medium -> Color(0xFFBEDC39)
        ExerciseScore.High -> Color(0xFF009650)
        ExerciseScore.VeryHigh -> Color(0xFF1E78BD)
        ExerciseScore.None -> Color.White
    }
}