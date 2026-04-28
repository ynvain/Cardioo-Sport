package com.cardioo_sport.presentation.calendar

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.cardioo_sport.R
import com.cardioo_sport.domain.model.SportMeasurement
import com.cardioo_sport.domain.model.exerciseScore
import com.cardioo_sport.presentation.util.formatLocalizedDate
import com.cardioo_sport.presentation.util.scoreColor
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.ExperimentalCalendarApi
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.yearMonth
import kotlinx.datetime.toKotlinLocalDate
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale


@OptIn(ExperimentalCalendarApi::class)
@Composable
fun CalendarScreen(
    contentPadding: PaddingValues,
    vm: CalendarViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsState()
    val initialMonth = remember { YearMonth.now() }
    val startMonth = remember { state.currentMonth.minusMonths(200) } // Adjust as needed
    val endMonth = remember { state.currentMonth.plusMonths(1) } // Adjust as needed
    val daysOfWeek = remember { daysOfWeek() } // Available from the library

    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = initialMonth,
        firstDayOfWeek = daysOfWeek.first()
    )
    val clickedMeasurement = remember { mutableStateOf<SportMeasurement?>(null) }
    val visibleMonth by remember { derivedStateOf { calendarState.firstVisibleMonth.yearMonth } }
    val isLandscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE


    LaunchedEffect(Unit) {
        if (calendarState.firstVisibleMonth.yearMonth != vm.currentMonth.value) {
            calendarState.scrollToMonth(vm.currentMonth.value)
        }
    }

    LaunchedEffect(visibleMonth) {
        vm.currentMonth.value = visibleMonth

    }

    val currentMonth = state.currentMonth
    LaunchedEffect(currentMonth) {
        vm.load()
    }


    Column(
        modifier = Modifier
            .padding(contentPadding)
            .fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MonthAndWeekHeader(daysOfWeek, visibleMonth)
        HorizontalCalendar(
            state = calendarState,
            dayContent = { Day(it, state, isLandscape, clickedMeasurement) },
        )
    }

    val sportMeasurement: SportMeasurement? = clickedMeasurement.value
    sportMeasurement?.let {
        Dialog(
            onDismissRequest = { clickedMeasurement.value = null },
            content = {
                Card(
                    modifier = Modifier
                        .padding(5.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                ) {
                    DayCardContent(it)
                }
            }
        )
    }
}

@Composable
fun DayCardContent(measurement: SportMeasurement) {
    Column(
        modifier = Modifier.padding(all = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = formatLocalizedDate(measurement.timestampEpochMillis),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .background(scoreColor(exerciseScore(measurement)), shape = CircleShape),
            )
        }
        if (measurement.morningSteps != null || measurement.noonSteps != null) {
            val steps = (measurement.morningSteps ?: 0) + (measurement.noonSteps ?: 0)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.c_sports_icons_walk),
                    contentDescription = "Walk Icon",
                    modifier = Modifier.size(25.dp)
                )
                Text(
                    text = steps.toString(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        measurement.runningDistance?.let {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.c_sports_icons_run),
                    contentDescription = "Run Icon",
                    modifier = Modifier.size(25.dp)
                )
                Text(
                    text = stringResource(R.string.format_distance, it),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        measurement.cyclingDistance?.let {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.c_sports_icons_bike),
                    contentDescription = "Bike Icon",
                    modifier = Modifier.size(25.dp)
                )
                Text(
                    text = stringResource(R.string.format_distance, it),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }


        }

        if (measurement.stretching) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.c_sports_icons_stretch),
                contentDescription = "Stretch Icon",
                modifier = Modifier.size(25.dp)
            )
        }
    }
}


@Composable
fun Day(
    day: CalendarDay,
    state: CalendarViewModel.State,
    isLandscape: Boolean,
    clickedMeasurement: MutableState<SportMeasurement?>
) {
    val sportMeasurement: SportMeasurement? =
        state.measurements[day.date.yearMonth]?.get(day.date.toKotlinLocalDate())
    Box(
        modifier = Modifier
            .aspectRatio(if (isLandscape) 2.4f else 1f)
            .clickable(
                enabled = day.position == DayPosition.MonthDate && sportMeasurement != null,
                onClick = { clickedMeasurement.value = sportMeasurement }
            ),  // This is important for square sizing!
        contentAlignment = Alignment.Center

    ) {
        DayContainer(day, state)
    }
}


@Composable
private fun MonthAndWeekHeader(daysOfWeek: List<DayOfWeek>, yearMonth: YearMonth) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = yearMonth.month.getDisplayName(
                TextStyle.FULL,
                Locale.getDefault()
            ) + " " + yearMonth.year,
            modifier = Modifier.padding(start = 15.dp),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}


@Composable
private fun DayContainer(day: CalendarDay, state: CalendarViewModel.State) {
    val sportMeasurement: SportMeasurement? =
        state.measurements[day.date.yearMonth]?.get(day.date.toKotlinLocalDate())
    if (sportMeasurement == null || day.position != DayPosition.MonthDate) {
        Text(text = day.date.dayOfMonth.toString(), color = dayColor(day, sportMeasurement))
    } else {
        Box(
            modifier = Modifier
                .size(40.dp)
                .border(1.dp, getColor(sportMeasurement), CircleShape)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = day.date.dayOfMonth.toString(), color = dayColor(day, sportMeasurement))
        }
    }

}


@Composable
private fun dayColor(day: CalendarDay, sportMeasurement: SportMeasurement?): Color {
    if (day.position != DayPosition.MonthDate) return Color.Gray
    sportMeasurement ?: return MaterialTheme.colorScheme.onSurfaceVariant
    return getColor(sportMeasurement)
}


private fun getColor(sportMeasurement: SportMeasurement): Color {
    val exerciseScore = exerciseScore(sportMeasurement)
    return scoreColor(exerciseScore)
}
