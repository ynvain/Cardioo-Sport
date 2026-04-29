package com.cardioo_sport.presentation.util

import com.cardioo_sport.domain.model.SportMeasurement
import java.time.LocalDate
import java.time.ZonedDateTime


enum class Range {
    Week, Month, SixMonths, ThisYear, PreviousYear, Year, AllTime
}


fun filterByRange(
    measurements: List<SportMeasurement>,
    range: Range,
): List<SportMeasurement> {
    // Return everything for AllTime
    if (range == Range.AllTime) return measurements

    val now = ZonedDateTime.now()
    val nowInclusive = now.plusDays(1).toInstant().toEpochMilli()
    fun nowMinusDays(days: Long) = now.minusDays(days).toInstant().toEpochMilli()
    val (startEpoch, endEpoch) = when (range) {
        Range.Week -> {
            nowMinusDays(7) to nowInclusive
        }

        Range.Month -> {
            nowMinusDays(30) to nowInclusive
        }

        Range.SixMonths -> {
            nowMinusDays(180) to nowInclusive
        }

        Range.Year -> {
            nowMinusDays(365) to nowInclusive
        }

        Range.ThisYear -> {
            val start = LocalDate.of(now.year, 1, 1).atStartOfDay(now.zone)
            start.toInstant().toEpochMilli() to nowInclusive
        }

        Range.PreviousYear -> {
            val previousYear = now.year - 1
            val start = LocalDate.of(previousYear, 1, 1).atStartOfDay(now.zone)
            val end = LocalDate.of(now.year, 1, 1).atStartOfDay(now.zone)
            start.toInstant().toEpochMilli() to end.toInstant().toEpochMilli()
        }
        // If any other range is added, handle it here. The original had an `else` branch,
        // but we'll keep it exhaustive and safe.
        else -> error("Unsupported range: $range")
    }

    return measurements.filter {
        it.timestampEpochMillis in startEpoch until endEpoch
    }
}


fun filterPrevByRange(
    measurements: List<SportMeasurement>,
    range: Range,
): List<SportMeasurement> {
    if (range != Range.Week && range != Range.Month) return emptyList()
    val days = when (range) {
        Range.Week -> 7
        Range.Month -> 30
        else -> 0
    }
    val start = ZonedDateTime.now().minusDays(days.toLong()).toInstant().toEpochMilli()
    val finish = ZonedDateTime.now().minusDays(2 * days.toLong()).toInstant().toEpochMilli()
    return measurements.filter { it.timestampEpochMillis in finish..start }
}