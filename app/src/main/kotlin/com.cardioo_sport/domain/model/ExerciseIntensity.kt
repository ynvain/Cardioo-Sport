package com.cardioo_sport.domain.model


enum class ExerciseIntensity {
    None,
    Low,
    Medium,
    High,
}


fun exerciseIntensity(measurement: SportMeasurement): ExerciseIntensity {
    return exerciseIntensity(
        morningSteps = measurement.morningSteps,
        noonSteps = measurement.morningSteps,
        runningDistance = measurement.runningDistance,
        cyclingDistance = measurement.cyclingDistance,
        stretching = measurement.stretching
    )
}

fun exerciseIntensity(
    morningSteps: Int?,
    noonSteps: Int?,
    runningDistance: Double?,
    cyclingDistance: Double?,
    stretching: Boolean
): ExerciseIntensity {
    var exerciseCount = 0
    if (morningSteps != null || noonSteps != null) exerciseCount++
    if (runningDistance != null) exerciseCount++
    if (cyclingDistance != null) exerciseCount++
    if (stretching) exerciseCount++
    return when {
        exerciseCount >= 3 -> ExerciseIntensity.High
        exerciseCount == 2 -> ExerciseIntensity.Medium
        exerciseCount == 1 -> ExerciseIntensity.Low
        else -> ExerciseIntensity.None
    }
}


