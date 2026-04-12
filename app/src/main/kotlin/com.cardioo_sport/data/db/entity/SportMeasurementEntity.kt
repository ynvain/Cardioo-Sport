package com.cardioo_sport.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.cardioo_sport.domain.model.WeightUnit

@Entity(
    tableName = "sport_measurement",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["timestampEpochMillis"]), Index(value = ["userId"])],
)
data class SportMeasurementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: Long,
    val timestampEpochMillis: Long,
    val morningSteps: Int?,
    val noonSteps: Int?,
    val runningDistance: Double?,
    val cyclingDistance: Double?,
    val stretching: Boolean,
    val notes: String?,
)
