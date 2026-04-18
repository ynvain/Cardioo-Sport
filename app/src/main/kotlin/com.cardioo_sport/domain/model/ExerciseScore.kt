package com.cardioo_sport.domain.model


enum class ExerciseScore {
    None,
    Low,
    Medium,
    High,
    VeryHigh
}

fun exerciseScore(measurement: SportMeasurement): ExerciseScore {
    return exerciseScore(
        measurement.morningSteps,
        measurement.noonSteps,
        measurement.runningDistance,
        measurement.cyclingDistance,
        measurement.stretching
    )
}

fun exerciseScore(
    morningSteps: Int?,
    noonSteps: Int?,
    runningDistance: Double?,
    cyclingDistance: Double?,
    stretching: Boolean
): ExerciseScore {
    val exerciseCount = exerciseScoreCount(
        morningSteps, noonSteps, runningDistance, cyclingDistance, stretching
    )
    return exerciseScore(exerciseCount)
}

fun exerciseScore(exerciseCount: Int): ExerciseScore {
    return when {
        exerciseCount > 3 -> ExerciseScore.VeryHigh
        exerciseCount == 3 -> ExerciseScore.High
        exerciseCount == 2 -> ExerciseScore.Medium
        exerciseCount == 1 -> ExerciseScore.Low
        else -> ExerciseScore.None
    }
}

fun exerciseScoreCount(
    measurement: SportMeasurement
) = exerciseScoreCount(
    measurement.morningSteps,
    measurement.noonSteps,
    measurement.runningDistance,
    measurement.cyclingDistance,
    measurement.stretching
)


fun exerciseScoreCount(
    morningSteps: Int?,
    noonSteps: Int?,
    runningDistance: Double?,
    cyclingDistance: Double?,
    stretching: Boolean
): Int {
    var exerciseCount = 0
    if (morningSteps != null || noonSteps != null) exerciseCount++
    if (runningDistance != null) exerciseCount++
    if (cyclingDistance != null) exerciseCount++
    if (stretching) exerciseCount++
    return exerciseCount

}






