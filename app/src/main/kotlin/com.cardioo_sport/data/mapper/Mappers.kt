package com.cardioo_sport.data.mapper

import com.cardioo_sport.data.db.entity.SportMeasurementEntity
import com.cardioo_sport.data.db.entity.UserEntity
import com.cardioo_sport.domain.model.SportMeasurement
import com.cardioo_sport.domain.model.UserProfile
import kotlinx.datetime.LocalDate
import kotlin.Double

fun UserEntity.toDomain(): UserProfile =
    UserProfile(
        id = id,
        name = name,
        height = height,
        heightUnit = heightUnit,
        weightUnit = weightUnit,
        dateOfBirth = dateOfBirthIso?.let { LocalDate.parse(it) },
        gender = gender,
        stepLength = stepLength
    )

fun UserProfile.toEntity(): UserEntity =
    UserEntity(
        id = id,
        name = name,
        height = height,
        heightUnit = heightUnit,
        weightUnit = weightUnit,
        dateOfBirthIso = dateOfBirth?.toString(),
        gender = gender,
        stepLength = stepLength
    )

fun SportMeasurementEntity.toDomain(): SportMeasurement =
    SportMeasurement(
        id = id,
        userId = userId,
        timestampEpochMillis = timestampEpochMillis,
        morningSteps = morningSteps,
        noonSteps = noonSteps,
        runningDistance = runningDistance,
        cyclingDistance = cyclingDistance,
        stretching = stretching,
        notes = notes,
    )

fun SportMeasurement.toEntity(): SportMeasurementEntity =
    SportMeasurementEntity(
        id = id,
        userId = userId,
        timestampEpochMillis = timestampEpochMillis,
        morningSteps = morningSteps,
        noonSteps = noonSteps,
        runningDistance = runningDistance,
        cyclingDistance = cyclingDistance,
        stretching = stretching,
        notes = notes,
    )

