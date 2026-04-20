package com.cardioo_sport.presentation.readings

import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cardioo_sport.R
import com.cardioo_sport.domain.model.SportMeasurement
import com.cardioo_sport.domain.model.exerciseScore
import com.cardioo_sport.presentation.util.formatLocalizedDateWithoutYear
import com.cardioo_sport.presentation.util.formatLocalizedDayOfWeek
import com.cardioo_sport.presentation.util.getYear
import com.cardioo_sport.presentation.util.scoreColor
import java.text.DecimalFormat
import java.time.ZonedDateTime

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ReadingsScreen(
    contentPadding: PaddingValues,
    onEdit: (Long) -> Unit,
    vm: ReadingsViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsState()
    val snack = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    val hasMore = state.measurements.size < state.totalCount
    val format = DecimalFormat("#.#")

    val pullState = rememberPullRefreshState(
        refreshing = state.isRefreshing,
        onRefresh = vm::refresh,
    )


    LaunchedEffect(vm.newAdded) {
        if (vm.newAdded.value) {
            listState.animateScrollToItem(0)
            vm.resetAddedNewFlag()
        }
    }

    // Infinite scroll: `snapshotFlow` turns LazyList scroll state into a cold Flow. We collect it
    // inside `LaunchedEffect` so work runs in a coroutine (collect is suspending) and restarts when
    // keys like list size change, so thresholds stay correct after loads/refreshes.
    LaunchedEffect(
        listState,
        state.totalCount,
        state.measurements.size,
        state.isLoadingMore,
        state.isRefreshing
    ) {
        snapshotFlow {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible
        }.collect { lastVisible ->
            val threshold = 5
            val shouldLoad =
                hasMore &&
                        !state.isLoadingMore &&
                        !state.isRefreshing &&
                        lastVisible >= (state.measurements.size - 1 - threshold).coerceAtLeast(0)
            if (shouldLoad) vm.loadNextPage()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .pullRefresh(pullState),
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(5.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            stickyHeader {
                Row(
                    modifier = Modifier
                        .padding(
                        ), verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .padding(
                                end = 10.dp,
                            )
                            .weight(18F),
                    )
                    Divider(
                        modifier = Modifier
                            .height(10.dp)
                            .width(3.5.dp)
                    )
                    Box(
                        modifier = Modifier
                            .weight(82F)
                    ) {
                            val shape = RoundedCornerShape(12.dp)
                            Card(
                                modifier = Modifier
                                    .clip(shape),
                                shape = shape,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(
                                            start = 10.dp,
                                            end = 15.dp,
                                            top = 1.dp,
                                            bottom = 1.dp
                                        ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier.weight(38F),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = ImageVector.vectorResource(id = R.drawable.c_sports_icons_walk),
                                            contentDescription = "Walk Icon",
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                    Box(
                                        modifier = Modifier.weight(25F),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = ImageVector.vectorResource(id = R.drawable.c_sports_icons_run),
                                            contentDescription = "Run Icon",
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }

                                    Box(
                                        modifier = Modifier.weight(25F),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = ImageVector.vectorResource(id = R.drawable.c_sports_icons_bike),
                                            contentDescription = "Run Icon",
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                    Box(
                                        modifier = Modifier.weight(12F),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = ImageVector.vectorResource(id = R.drawable.c_sports_icons_stretch),
                                            contentDescription = "Run Icon",
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                            }

                    }
                }

                //  Text(
                //  text = "Category A",
                //  Modifier
                //   )

            }
            val selectionActive = state.selectedIds.isNotEmpty()
            items(items = state.measurements, key = { it.id }) { m ->
                MeasurementCard(
                    measurement = m,
                    selected = m.id in state.selectedIds,
                    selectionActive = selectionActive,
                    onEdit = { onEdit(m.id) },
                    onToggleSelect = { vm.toggleSelection(m.id) },
                    onLongPressSelect = { vm.addToSelection(m.id) },
                    format = format
                )
            }

            if (state.isLoadingMore && !state.isRefreshing) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .size(20.dp),
                            strokeWidth = 2.dp,
                        )
                    }
                }
            }

            item { Spacer(Modifier.size(72.dp)) }
        }

        PullRefreshIndicator(
            refreshing = state.isRefreshing,
            state = pullState,
            modifier = Modifier.align(Alignment.TopCenter),
            contentColor = MaterialTheme.colorScheme.primary,
        )

        SnackbarHost(
            hostState = snack,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
        )
    }
}

@Composable
private fun MeasurementCard(
    measurement: SportMeasurement,
    selected: Boolean,
    selectionActive: Boolean,
    onEdit: () -> Unit,
    onToggleSelect: () -> Unit,
    onLongPressSelect: () -> Unit,
    format: DecimalFormat,
) {
    val exerciseIntensity = exerciseScore(measurement)
    val currentYear = ZonedDateTime.now().year
    val year = getYear(measurement.timestampEpochMillis)
    val shape = RoundedCornerShape(12.dp)

    Card(
        modifier = Modifier
            .clip(shape)
            .then(
                if (selected) {
                    Modifier.border(2.dp, MaterialTheme.colorScheme.primary, shape)
                } else {
                    Modifier
                },
            )
            .combinedClickable(
                onClick = {
                    if (selectionActive) onToggleSelect() else onEdit()
                },
                onLongClick = onLongPressSelect,
            ),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val spacing = if (year == currentYear) 4.dp else 1.dp
            val verticalPadding = if (year == currentYear) 5.dp else 3.dp
            Column(
                modifier = Modifier
                    .padding(
                        start = 5.dp,
                        top = verticalPadding,
                        end = 10.dp,
                        bottom = verticalPadding
                    )
                    .weight(18F),
                verticalArrangement = Arrangement.spacedBy(spacing),
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    formatLocalizedDayOfWeek(measurement.timestampEpochMillis),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    formatLocalizedDateWithoutYear(measurement.timestampEpochMillis),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (year != currentYear) {
                    Text(
                        year.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

            }
            Divider(
                color = scoreColor(exerciseIntensity),
                modifier = Modifier
                    .height(50.dp)
                    .width(3.5.dp)
            )
            Column(
                modifier = Modifier
                    .padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 5.dp)
                    .weight(82F),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row(
                    modifier = Modifier
                        .padding(top = 10.dp, end = 5.dp, bottom = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,

                    ) {
                    Column(
                        modifier = Modifier.weight(38F),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        val stepText =
                            formattedStepsText(measurement = measurement, format = format)
                        TextWithResizableFont(stepText)

                    }
                    Column(
                        modifier = Modifier.weight(25F),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        TextWithResizableFont(measurement.runningDistance?.let { format.format(it) }
                            ?: stringResource(R.string.value_empty))
                    }
                    Column(
                        modifier = Modifier.weight(25F),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        TextWithResizableFont(measurement.cyclingDistance?.let { format.format(it) }
                            ?: stringResource(R.string.value_empty))
                    }
                    Column(
                        modifier = Modifier.weight(12F),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        if (measurement.stretching) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.c_sports_icons_stretch),
                                contentDescription = "Run Icon",
                                modifier = Modifier.size(25.dp)
                            )
                            // Icon(Icons.Default.Check, "las")

                        }

                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 5.dp)
                ) {
                    measurement.notes?.takeIf { it.isNotBlank() }?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun formattedStepsText(measurement: SportMeasurement, format: DecimalFormat): String {
    @Composable
    fun formatSteps(steps: Int): String {
        if (steps > 1000) {
            val shortStepForm = format.format(steps.toDouble() / 1000)
            return stringResource(R.string.format_steps_short_form, shortStepForm)
        } else {
            return steps.toString()
        }
    }

    return when {
        measurement.morningSteps != null && measurement.noonSteps != null -> stringResource(
            R.string.format_steps,
            formatSteps(measurement.morningSteps), formatSteps(measurement.noonSteps)
        )

        measurement.morningSteps != null -> formatSteps(measurement.morningSteps)
        measurement.noonSteps != null -> formatSteps(measurement.noonSteps)
        else -> stringResource(R.string.value_empty)
    }
}


@Composable
private fun TextWithResizableFont(text: String) {
    val textStyle: TextStyle =
        when (text.length) {
            in 0..12 -> MaterialTheme.typography.titleLarge
            else -> MaterialTheme.typography.titleMedium
        }
    Text(
        text,
        style = textStyle,
    )
}
