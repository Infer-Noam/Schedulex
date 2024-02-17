/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.kashio.ui

///import com.example.kashio.ui.TimeUi.NavGraphs
//import com.example.kashio.ui.TimeUi.NavGraphs
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kashio.R
import com.example.kashio.data.local.database.Time
import com.example.kashio.ui.TimeUi.AppBar
import com.example.kashio.ui.TimeUi.Statistics
import com.example.kashio.ui.TimeUi.StatisticsAppBar
import com.example.kashio.ui.TimeUi.TimeSubApp
import com.example.kashio.ui.TimeUi.TimeViewModel
import com.example.kashio.ui.TimeUi.TitleListScreen
import com.example.kashio.ui.TimeUi.getCurrentDate

//import com.example.kashio.ui.TimeUi.navArgs

enum class SCREENS{
    SCHEDULE,
    SAVED_TITLES,
    STATISTICS
}

enum class HSLA{
    H,
    S,
    L,
    A
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation(viewModel: TimeViewModel = hiltViewModel()) {


    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var blockInput by rememberSaveable { mutableStateOf(false) }

    var currentBlock by remember { mutableStateOf(false) }

    var blockSave by remember { mutableStateOf(false) }

    var blockDelete by remember { mutableStateOf(false) }

    var deletableBlock by remember { mutableStateOf(false) }

    val longClick = remember { mutableStateMapOf<Time, Boolean>().also { it.putAll(uiState.timeBlocks.associateWith { false }) } }

    var selectedScreen by rememberSaveable { mutableStateOf(SCREENS.SCHEDULE) }

    var newTitle by rememberSaveable { mutableStateOf(false) }

    var rangeStart  by rememberSaveable { mutableStateOf(getCurrentDate()) }

    var rangeEnd  by rememberSaveable { mutableStateOf(getCurrentDate()) }

    var statsBlockList = remember { mutableStateOf(emptyList<Time>()) }

    var sortColorIndex by rememberSaveable { mutableStateOf(HSLA.H) }



    //var showSnackbar by rememberSaveable { mutableStateOf(false) }


    BackHandler(enabled = (blockInput || (longClick.containsValue(true)))) {
        if(blockInput) {
            blockInput = false
        }
        if(longClick.containsValue(true)){
            longClick.keys.forEach { longClick[it] = false }
        }
    }

        Scaffold(
            topBar = {
                when (selectedScreen) {
                    SCREENS.SCHEDULE -> {
                        AppBar(
                            selectedDate = uiState.date,
                            saveUiDate = viewModel::setDate,
                            chunksDivider = uiState.chunksDivider,
                            saveChunksDivider = viewModel::setChunksDivider,
                            blockInput = blockInput,
                            onBlockInput = { blockInput = it },
                            onCurrentBlock = { currentBlock = it },
                            onBlockSave = { blockSave = true },
                            onBlockDelete = { blockDelete = true },
                            deletableBlock = deletableBlock,
                            onDeletableBlock = { deletableBlock = false },
                            longClick = longClick,
                            onLongClick = {
                                if (longClick.containsValue(true)) {
                                    longClick.keys.forEach { longClick[it] = false }
                                }
                            },
                            onGroupDelete = viewModel::deleteTimeBlock,
                            onDateChange = {rangeStart = it; rangeEnd = it}
                        )
                    }
                    SCREENS.SAVED_TITLES -> {
                        CenterAlignedTopAppBar(title = { Text(text = "Saved titles", style = MaterialTheme.typography.titleLarge )})
                    }
                    SCREENS.STATISTICS -> {
                        StatisticsAppBar(
                            selectedDate = uiState.date,
                            saveUiDate = viewModel::setDate,
                            onLongClick = {},
                            onDateChange = {rangeStart = it; rangeEnd = it}
                        )
                    }
                }
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(
                                    R.drawable.round_view_list_24
                                ),
                                contentDescription = "Saved titles screen",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        },
                        label = { Text("Saved titles") },
                        selected = selectedScreen == SCREENS.SAVED_TITLES,
                        onClick = { selectedScreen = SCREENS.SAVED_TITLES}
                    )
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.baseline_view_day_24),
                                contentDescription = "schedule screen"
                            )
                        },
                        label = { Text("Schedule") },
                        selected = selectedScreen == SCREENS.SCHEDULE,
                        onClick = { selectedScreen = SCREENS.SCHEDULE }
                    )
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.round_show_chart_24),
                                contentDescription = "statistics screen"
                            )
                        },
                        label = { Text("Statistics") },
                        selected = selectedScreen == SCREENS.STATISTICS,
                        onClick = { selectedScreen = SCREENS.STATISTICS }
                    )
                }
            },
            /* snackbarHost = {if(showSnackbar){
                Snackbar(
                    action = {
                        TextButton(onClick = { showSnackbar = false }) {
                            Text("Dismiss")
                        }
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Title must not be empty")
                }
        }} */
        )
        { paddingValues ->
            viewModel.loadTitles()
            when (selectedScreen) {
                SCREENS.SCHEDULE -> {
                    TimeSubApp(
                        modifier = Modifier.padding(paddingValues),
                        timeBlocks = uiState.timeBlocks,
                        onSave = viewModel::insertTimeBlock,
                        blockInput = blockInput,
                        onBlockInput = { blockInput = it },
                        currentBlock = currentBlock,
                        onCurrentBlock = { currentBlock = it },
                        onBlockSave = { blockSave = false },
                        blockSave = blockSave,
                        onBlockDelete = { blockDelete = false },
                        blockDelete = blockDelete,
                        onDelete = viewModel::deleteTimeBlock,
                        onDeletableBlock = { deletableBlock = it },
                        onLongClick = { longClick[it] = longClick[it] != true },
                        longClick = longClick,
                        savedTitles = uiState.savedTitles,
                        insertTitle = viewModel::insertTitle,
                        onNewTitle = {newTitle = it},
                        newTitle = newTitle,
                        sortIndex = sortColorIndex,
                        onSortIndex = { sortColorIndex = when (sortColorIndex) {
                            HSLA.H -> {
                                HSLA.S
                            }
                            HSLA.S -> {
                                HSLA.L
                            }
                            HSLA.L -> {
                                HSLA.A
                            }
                            HSLA.A -> {
                                HSLA.H
                            }
                        }})
                    //onShowSnackbar = {showSnackbar = it})
                }
                SCREENS.SAVED_TITLES -> {
                    blockInput = false
                    TitleListScreen(
                        modifier = Modifier.padding(paddingValues),
                        list = uiState.savedTitles,
                        insertTitle = viewModel::insertTitle,
                        onNewTitle = {newTitle = it},
                        newTitle = newTitle, deleteTitle = viewModel::deleteTitle,
                        onPlay =  viewModel::onPlay, onStop = viewModel::onStop, getPlayId = viewModel::getPlay, sortIndex = sortColorIndex,
                        onSortIndex = { sortColorIndex = when (sortColorIndex) {
                            HSLA.H -> {
                                HSLA.S
                            }
                            HSLA.S -> {
                                HSLA.L
                            }
                            HSLA.L -> {
                                HSLA.A
                            }
                            HSLA.A -> {
                                HSLA.H
                            }
                        }})//, onShowSnackbar = {showSnackbar = it})
                }
                SCREENS.STATISTICS -> {
                    blockInput = false
                    viewModel.getDatesInRange(startDate = rangeStart, endDate = rangeEnd, callback = {statsBlockList.value = it})
                    Statistics(modifier = Modifier.padding(paddingValues),blockList = statsBlockList.value,rangeStart = rangeStart, rangeEnd = rangeEnd, savedTitles = uiState.savedTitles)
                }
            }
        }
}


