package com.cardioo_sport.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cardioo_sport.domain.model.Gender
import com.cardioo_sport.domain.model.HeightUnit
import com.cardioo_sport.domain.model.WeightUnit

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val height: Double,
    val heightUnit: HeightUnit,
    val weightUnit: WeightUnit,
    val dateOfBirthIso: String?,
    val gender: Gender?,
    val stepLength: Double
)

