package com.cardioo_sport.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.cardioo_sport.R
import java.text.DecimalFormat


val decimalFormat: DecimalFormat = DecimalFormat("#.#")

@Composable
fun formatSteps(steps: Int): String {
    if (steps > 1000) {
        val shortStepForm = decimalFormat.format(steps.toDouble() / 1000)
        return stringResource(R.string.format_steps_short_form, shortStepForm)
    } else {
        return steps.toString()
    }
}

@Composable
fun formatBigSteps(steps: Int): String {
    if (steps > 1000000) {
        val shortStepForm = decimalFormat.format(steps.toDouble() / 1000000)
        return stringResource(R.string.format_steps_short_form_mill, shortStepForm)
    } else {
        return formatSteps(steps)
    }
}


