package com.cardioo_sport.presentation.statistics

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cardioo_sport.R
import com.cardioo_sport.domain.model.SportMeasurement
import com.cardioo_sport.presentation.util.decimalFormat
import com.cardioo_sport.presentation.util.formatSteps
import com.cardioo_sport.presentation.util.scoreColor
import kotlinx.coroutines.launch

@Composable
fun StatisticsScreen(
    contentPadding: PaddingValues,
    vm: StatisticsViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsState()
    val snack = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var rangeExpanded by remember { mutableStateOf(false) }

    val exportLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
            if (uri == null) return@rememberLauncherForActivityResult
            scope.launch {
                val ok = exportCsv(context, uri, state.measurements)
                snack.showSnackbar(
                    if (ok) context.getString(R.string.export_csv_success)
                    else context.getString(R.string.export_csv_failed),
                )
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(5.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            val latest = state.summary.latest
            if (latest == null) {
                Text(
                    stringResource(R.string.statistics_no_readings),
                    style = MaterialTheme.typography.bodyMedium
                )
                return@Card;
            }
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box {
                        OutlinedButton(onClick = { rangeExpanded = true }) {
                            Text(
                                stringResource(
                                    R.string.stats_range_button,
                                    stringResource(state.periodLabelRes),
                                ),
                            )
                        }
                        DropdownMenu(
                            expanded = rangeExpanded,
                            onDismissRequest = { rangeExpanded = false }) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.range_week)) },
                                onClick = {
                                    vm.setRange(StatisticsViewModel.Range.Week); rangeExpanded =
                                    false
                                },
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.range_month)) },
                                onClick = {
                                    vm.setRange(StatisticsViewModel.Range.Month); rangeExpanded =
                                    false
                                },
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.range_six_months)) },
                                onClick = {
                                    vm.setRange(StatisticsViewModel.Range.SixMonths); rangeExpanded =
                                    false
                                },
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.range_year)) },
                                onClick = {
                                    vm.setRange(StatisticsViewModel.Range.Year); rangeExpanded =
                                    false
                                },
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.range_all_time)) },
                                onClick = {
                                    vm.setRange(StatisticsViewModel.Range.AllTime); rangeExpanded =
                                    false
                                },
                            )
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = { exportLauncher.launch(context.getString(R.string.csv_default_filename)) }) {
                        Icon(
                            Icons.Filled.UploadFile,
                            contentDescription = stringResource(R.string.cd_export_csv)
                        )
                    }
                }

                state.summary.averageExerciseScore?.let { score ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            stringResource(
                                R.string.statistics_avg_score,
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        state.summary.averageExerciseScoreCount?.let { count ->
                            Text(
                                text = count.toString(),
                                style = MaterialTheme.typography.titleLarge,
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .background(scoreColor(score), shape = CircleShape),
                        )
                    }
                }
                state.summary.prevAverageExerciseScore?.let { score ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            stringResource(
                                R.string.statistics_prev_avg_score,
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        state.summary.prevAverageExerciseScoreCount?.let { count ->
                            Text(
                                text = count.toString(),
                                style = MaterialTheme.typography.titleLarge,
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .background(scoreColor(score), shape = CircleShape),
                        )
                    }
                }
                Box(modifier = Modifier.padding(top = 10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            pluralStringResource(
                                R.plurals.entries_count,
                                state.summary.count,
                                state.summary.count,
                            ),
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Text(
                            pluralStringResource(
                                R.plurals.stretching_count,
                                state.summary.stretchingCount,
                                state.summary.stretchingCount,
                            ),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }

                StatsTable(
                    table = state.table,
                )
            }
        }
    }

    SnackbarHost(hostState = snack)
}


@Composable
private fun StatsTable(
    table: StatisticsViewModel.TableStats,
) {
    val borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    val headBg = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(10.dp)),
    ) {
        StatsRow(
            label = stringResource(R.string.stats_row_metric),
            min = stringResource(R.string.stats_col_min),
            max = stringResource(R.string.stats_col_max),
            avg = stringResource(R.string.stats_col_avg),
            total = stringResource(R.string.stats_col_total),
            isHeader = true,
            headBg = headBg,
        )
        StatsRow(
            label = stringResource(R.string.stats_row_morning_walk),
            min = formatTableSteps(table.minMorningSteps),
            max = formatTableSteps(table.maxMorningSteps),
            avg = formatTableSteps(table.avgMorningSteps),
            total = formatTableSteps(table.totalMorningSteps),
        )
        StatsRow(
            label = stringResource(R.string.stats_row_noon_walk),
            min = formatTableSteps(table.minNoonSteps),
            max = formatTableSteps(table.maxNoonSteps),
            avg = formatTableSteps(table.avgNoonSteps),
            total = formatTableSteps(table.totalNoonSteps),
        )
        StatsRow(
            label = stringResource(R.string.stats_row_running_distance),
            min = formatTableDistance(table.minRunningDistance),
            max = formatTableDistance(table.maxRunningDistance),
            avg = formatTableDistance(table.avgRunningDistance),
            total = formatTableDistance(table.totalRunningDistance),
        )
        StatsRow(
            label = stringResource(R.string.stats_row_cycling_distance),
            min = formatTableDistance(table.minCyclingDistance),
            max = formatTableDistance(table.maxCyclingDistance),
            avg = formatTableDistance(table.avgCyclingDistance),
            total = formatTableDistance(table.totalCyclingDistance),
        )
    }
}

@Composable
private fun formatTableSteps(steps: Int?): String {
    return steps?.let { formatSteps(it) } ?: "—"
}

private fun formatTableDistance(distance: Double?): String {
    return distance?.let { decimalFormat.format(it) } ?: "—"
}

@Composable
private fun StatsRow(
    label: String,
    min: String,
    max: String,
    avg: String,
    total: String,
    isHeader: Boolean = false,
    headBg: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Unspecified,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isHeader) Modifier.background(headBg) else Modifier)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1.4f),
            style = if (isHeader) MaterialTheme.typography.labelLarge else MaterialTheme.typography.bodyMedium,
        )
        Text(min, modifier = Modifier.weight(1f))
        Text(max, modifier = Modifier.weight(1f))
        Text(avg, modifier = Modifier.weight(1f))
        Text(total, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.primary)
    }
}

private suspend fun exportCsv(
    context: Context,
    uri: android.net.Uri,
    measurements: List<SportMeasurement>,
): Boolean {
    return try {
        context.contentResolver.openOutputStream(uri)?.use { os ->
            val header = context.getString(R.string.csv_header)
            os.write(header.toByteArray())
            measurements.sortedBy { it.timestampEpochMillis }.forEach { m ->
                val notesEscaped = (m.notes ?: "").replace("\"", "\"\"")
                val row =
                    "${m.timestampEpochMillis},${m.morningSteps ?: ""},${m.noonSteps ?: ""},${m.runningDistance ?: ""},${m.cyclingDistance ?: ""},${m.stretching},\"$notesEscaped\"\n"
                os.write(row.toByteArray())
            }
        }
        true
    } catch (_: Throwable) {
        false
    }
}
