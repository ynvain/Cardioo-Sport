package com.cardioo_sport.presentation.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cardioo_sport.domain.model.exerciseScore
import com.cardioo_sport.presentation.util.scoreColor
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.ExperimentalCalendarApi
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale


@OptIn(ExperimentalCalendarApi::class)
@Composable
fun CalendarScreen(contentPadding: PaddingValues) {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) } // Adjust as needed
    val endMonth = remember { currentMonth.plusMonths(100) } // Adjust as needed
    val daysOfWeek = remember { daysOfWeek() } // Available from the library

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first()
    )

    Column(
        modifier = Modifier
            .padding(contentPadding)
            .fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HorizontalCalendar(
            state = state,
            dayContent = { Day(it) },
            monthHeader = { month ->
                // You may want to use `remember {}` here so the mapping is not done
                // every time as the days of week order will never change unless
                // you set a new value for `firstDayOfWeek` in the state.
                val daysOfWeek = month.weekDays.first().map { it.date.dayOfWeek }
                MonthHeader(daysOfWeek = daysOfWeek, month.yearMonth)
            }
        )
    }
    /*
    Column(modifier = Modifier.padding(top = 50.dp).fillMaxSize(),) {
        HorizontalCalendar (
            state = calendarState,
            dayContent = { calendarDay: CalendarDay ->
                Box(
                    modifier = Modifier
                        .size(48.dp)
                    ,
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = calendarDay.date.dayOfMonth.toString(), color = Color.White)
                }},
            monthHeader = { month ->
                // You can also customize the month header here
                Text(text = month.yearMonth.month.name, style = MaterialTheme.typography.titleLarge,)
            }
        )
    }*/


}

@Composable
fun Day(day: CalendarDay) {
    Box(
        modifier = Modifier
            .aspectRatio(1f), // This is important for square sizing!
        contentAlignment = Alignment.Center
    ) {
        Text(text = day.date.dayOfMonth.toString(), color = getDayColor())
    }
}


@Composable
private fun MonthHeader(daysOfWeek: List<DayOfWeek>, yearMonth: YearMonth) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {

        Text(
            text = yearMonth.month.getDisplayName(
                TextStyle.FULL,
                Locale.getDefault()
            ) + " " + yearMonth.year,
            color = Color.White,
            modifier = Modifier.padding(start = 15.dp),
            style = MaterialTheme.typography.titleLarge
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("MonthHeader"),
        ) {
            for (dayOfWeek in daysOfWeek) {
                Text(
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    color = Color.White
                )
            }
        }
    }
}

private fun getDayColor(): Color {
    val exerciseScore = exerciseScore((0..4).random())
    return scoreColor(exerciseScore)
}
