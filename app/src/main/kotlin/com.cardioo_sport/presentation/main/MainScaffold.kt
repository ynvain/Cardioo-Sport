package com.cardioo_sport.presentation.main

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cardioo_sport.R
import com.cardioo_sport.presentation.calendar.CalendarScreen
import com.cardioo_sport.presentation.calendar.CalendarViewModel
import com.cardioo_sport.presentation.chart.ChartScreen
import com.cardioo_sport.presentation.readings.ReadingsScreen
import com.cardioo_sport.presentation.readings.ReadingsViewModel
import com.cardioo_sport.presentation.statistics.StatisticsScreen
import com.cardioo_sport.presentation.util.AccountAvatar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    onOpenEntry: (measurementIdOrNull: Long?) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenManageAccounts: () -> Unit,
    vm: MainViewModel = hiltViewModel(),
) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var menuExpanded by remember { mutableStateOf(false) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var createName by remember { mutableStateOf("") }
    val accountState by vm.state.collectAsState()
    val readingsVm: ReadingsViewModel = hiltViewModel()
    val readingsState by readingsVm.state.collectAsState()
    val calendarVm: CalendarViewModel = hiltViewModel()
    val currentAccount =
        accountState.accounts.firstOrNull { it.id == accountState.currentAccountId }
    val defaultAccountName = stringResource(R.string.account_default_name)
    val currentName = currentAccount?.name ?: defaultAccountName
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    LaunchedEffect(tab) {
        if (tab != 0) readingsVm.clearSelection()
        if (tab != 3) calendarVm.clear()
    }

    Scaffold(
        topBar = {
            if (!isLandscape) {
                TopAppBar(
                    title = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(R.drawable.c_sports),
                                contentDescription = stringResource(R.string.cd_app_logo),
                                modifier = Modifier.size(36.dp),
                            )
                            Text(
                                stringResource(
                                    when (tab) {
                                        0 -> R.string.title_readings
                                        1 -> R.string.title_statistics
                                        2 -> R.string.title_chart
                                        else -> R.string.title_calendar
                                    },
                                ),
                            )
                        }
                    },
                    actions = {
                        if (tab == 0 && readingsState.selectedIds.isNotEmpty()) {
                            IconButton(onClick = readingsVm::clearSelection) {
                                Icon(
                                    Icons.Filled.Close,
                                    contentDescription = stringResource(R.string.cd_readings_cancel_selection),
                                )
                            }
                            IconButton(onClick = readingsVm::deleteSelected) {
                                Icon(
                                    Icons.Filled.Delete,
                                    contentDescription = stringResource(R.string.cd_readings_delete_selected),
                                )
                            }
                        }
                        IconButton(onClick = { menuExpanded = true }) {
                            AccountAvatar(
                                name = currentName,
                                size = 38.dp,
                            )
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        stringResource(
                                            R.string.account_current_format,
                                            currentName
                                        )
                                    )
                                },
                                onClick = { menuExpanded = false },
                                leadingIcon = {
                                    AccountAvatar(
                                        name = currentName,
                                        size = 36.dp,
                                    )
                                },
                            )
                            accountState.accounts
                                .filter { it.id != accountState.currentAccountId }
                                .forEach { account ->
                                    DropdownMenuItem(
                                        text = { Text(account.name) },
                                        onClick = {
                                            vm.switchAccount(account.id)
                                            menuExpanded = false
                                        },
                                        leadingIcon = {
                                            AccountAvatar(
                                                name = account.name,
                                                size = 30.dp,
                                            )
                                        },
                                        modifier = Modifier.padding(start = 2.dp)
                                    )
                                }
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.menu_create_account)) },
                                onClick = {
                                    menuExpanded = false
                                    showCreateDialog = true
                                },
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.menu_manage_accounts)) },
                                onClick = {
                                    menuExpanded = false
                                    onOpenManageAccounts()
                                },
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.menu_edit_current_profile)) },
                                onClick = {
                                    menuExpanded = false
                                    onOpenSettings()
                                },
                            )
                        }
                    },
                )
            }
        },
        floatingActionButton = {
            if (tab == 0) {
                FloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    onClick = { onOpenEntry(null) },
                ) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.action_add))
                }
            }
        },
        bottomBar = {
            if (!isLandscape) {
                NavigationBar {
                    NavigationBarItem(
                        selected = tab == 0,
                        onClick = { tab = 0 },
                        icon = {
                            Icon(
                                Icons.AutoMirrored.Filled.ListAlt,
                                contentDescription = null
                            )
                        },
                        label = { Text(stringResource(R.string.cd_nav_readings)) },
                    )
                    NavigationBarItem(
                        selected = tab == 1,
                        onClick = { tab = 1 },
                        icon = { Icon(Icons.Filled.Assessment, contentDescription = null) },
                        label = { Text(stringResource(R.string.cd_nav_statistics)) },
                    )
                    NavigationBarItem(
                        selected = tab == 2,
                        onClick = { tab = 2 },
                        icon = { Icon(Icons.Filled.Insights, contentDescription = null) },
                        label = { Text(stringResource(R.string.cd_nav_chart)) },
                    )
                    NavigationBarItem(
                        selected = tab == 3,
                        onClick = { tab = 3 },
                        icon = { Icon(Icons.Filled.CalendarMonth, contentDescription = null) },
                        label = { Text(stringResource(R.string.cd_nav_calendar)) },
                    )
                }
            }
        },
    ) { padding ->
        if (isLandscape) {
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail {
                    Image(
                        painter = painterResource(R.drawable.c_sports),
                        contentDescription = stringResource(R.string.cd_app_logo),
                        modifier = Modifier.size(36.dp),
                    )
                    NavigationRailItem(
                        selected = tab == 0,
                        onClick = { tab = 0 },
                        icon = {
                            Icon(
                                Icons.AutoMirrored.Filled.ListAlt,
                                contentDescription = null
                            )
                        },
                        label = { Text(stringResource(R.string.cd_nav_readings)) },
                    )
                    NavigationRailItem(
                        selected = tab == 1,
                        onClick = { tab = 1 },
                        icon = { Icon(Icons.Filled.Assessment, contentDescription = null) },
                        label = { Text(stringResource(R.string.cd_nav_statistics)) },
                    )
                    NavigationRailItem(
                        selected = tab == 2,
                        onClick = { tab = 2 },
                        icon = { Icon(Icons.Filled.Insights, contentDescription = null) },
                        label = { Text(stringResource(R.string.cd_nav_chart)) },
                    )
                    NavigationRailItem(
                        selected = tab == 3,
                        onClick = { tab = 3 },
                        icon = { Icon(Icons.Filled.CalendarMonth, contentDescription = null) },
                        label = { Text(stringResource(R.string.cd_nav_calendar)) })
                }
                when (tab) {
                    0 -> ReadingsScreen(
                        contentPadding = PaddingValues(0.dp),
                        onEdit = { onOpenEntry(it) },
                        vm = readingsVm,
                    )

                    1 -> StatisticsScreen(contentPadding = PaddingValues(0.dp))
                    2 -> ChartScreen(contentPadding = PaddingValues(0.dp))
                    else -> CalendarScreen(contentPadding = PaddingValues(0.dp))
                }
            }
        } else {
            when (tab) {
                0 -> ReadingsScreen(
                    contentPadding = padding,
                    onEdit = { onOpenEntry(it) },
                    vm = readingsVm,
                )

                1 -> StatisticsScreen(contentPadding = padding)
                2 -> ChartScreen(contentPadding = padding)
                else -> CalendarScreen(contentPadding = padding)
            }
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text(stringResource(R.string.title_create_account)) },
            text = {
                OutlinedTextField(
                    value = createName,
                    onValueChange = { createName = it },
                    label = { Text(stringResource(R.string.label_account_name)) },
                    singleLine = true,
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        vm.createAccount(createName)
                        createName = ""
                        showCreateDialog = false
                    },
                ) { Text(stringResource(R.string.action_create)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showCreateDialog = false
                }) { Text(stringResource(R.string.action_cancel)) }
            },
        )
    }
}
