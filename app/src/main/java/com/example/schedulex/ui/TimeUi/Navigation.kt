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

package com.example.schedulex.ui.TimeUi

///import com.example.kashio.ui.TimeUi.NavGraphs
//import com.example.kashio.ui.TimeUi.NavGraphs
import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schedulex.data.local.database.Time
import com.example.schedulex.ui.TimeUi.AppBars.AppBar
import com.example.schedulex.ui.TimeUi.AppBars.CalendarColumn
import com.example.schedulex.ui.TimeUi.AppBars.StatisticsAppBar
import com.example.schedulex.ui.TimeUi.SavedTitlesScreen.TitleListScreen
import com.example.schedulex.ui.TimeUi.ScheduleScreen.ScheduleScreen
import com.example.schedulex.ui.TimeUi.ScheduleScreen.SubScreens
import com.example.schedulex.ui.TimeUi.StatisticsScreen.Statistics
import com.example.schedulex.ui.ViewModels.TimeUiState
import com.example.schedulex.ui.ViewModels.TimeViewModel
import com.example.schedulex.R

enum class SCREENS{ // an enum class with 3 values one for every screen in the app.
    SCHEDULE, // Schedule Screen
    SAVED_TITLES, // Saved Titles Screen
    STATISTICS // Statistics Screen
}

enum class NavigationType { // The 3 different ways for the app to look based on the device type
    BOTTOM_NAVIGATION,
    NAVIGATION_RAIL,
    PERMANENT_NAVIGATION_DRAWER
}


enum class HSLA{  // an enum class with 4 values. Each value effects the way colors are sorted in the dialog in TitleListScreen.
    H, // Hue
    S, // Saturation
    L, // Lightness
    A  // Alphabetical order
}


@SuppressLint("SourceLockedOrientationActivity")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation(viewModel: TimeViewModel = hiltViewModel(), darkTheme: Boolean, uiState: TimeUiState, windowSize: WindowWidthSizeClass) { // receives the TimeViewModel singleton, uiState, windowSize

    var subScreen by rememberSaveable { mutableStateOf(SubScreens.MAIN) } // The current sub screen. (sub screens are only inside Schedule Screen)

    var currentBlock by rememberSaveable { mutableStateOf(false) } // A boolean that represent if the user clicked on the מפתח שוודי

    var blockSave by rememberSaveable { mutableStateOf(false) } // A boolean that represent if the user clicked on the save block button

    var blockDelete by rememberSaveable { mutableStateOf(false) } // A boolean that represent if the user clicked on the delete block button

    var deletableBlock by rememberSaveable { mutableStateOf(false) } // A boolean that represent if the block you clicked on or picked is deletable

    val longClick = remember { mutableStateMapOf<Time, Boolean>().also { it.putAll(uiState.timeBlocks.associateWith { false }) } } // a map that contains keys that are time blocks and values that are booleans. The booleans represent if the user long clicked on each block or not.

    var selectedScreen by rememberSaveable { mutableStateOf(SCREENS.SCHEDULE) } // A variable of type SCREENS that represent the selected screen

    var newTitle by rememberSaveable { mutableStateOf(false) } // A boolean that represent if the title creation dialog is active or not

    val statsBlockList = remember { mutableStateOf(emptyList<Time>()) } // A list of type Time that represent the list of blocks that are passed to the statistics screen for display

    var sortColorIndex by rememberSaveable { mutableStateOf(HSLA.H) } // A variable of type HSLA that represent the way colors in title creation dialog will be sorted.

    var settingsGoBack by rememberSaveable { mutableStateOf(false) } // A Boolean represent if the go back button in settings sub screen is pressed

    val navigationType: NavigationType = when (windowSize) { // navigation type is the type of navigation in the app according to the windowSize
        WindowWidthSizeClass.Compact -> {
            NavigationType.BOTTOM_NAVIGATION
        }
        WindowWidthSizeClass.Medium -> {
            NavigationType.NAVIGATION_RAIL
        }
        WindowWidthSizeClass.Expanded -> {
            NavigationType.PERMANENT_NAVIGATION_DRAWER
        }
        else -> {
            NavigationType.BOTTOM_NAVIGATION
        }
    }

    BackHandler(enabled = (subScreen == SubScreens.CREATION || (longClick.containsValue(true)) || (subScreen == SubScreens.SETTINGS && navigationType == NavigationType.BOTTOM_NAVIGATION))) { // handles back clicks when back click is available
        if(subScreen == SubScreens.CREATION) {
            subScreen = SubScreens.MAIN
        }
        if(longClick.containsValue(true)){
            longClick.keys.forEach { longClick[it] = false }
        }
        if(subScreen == SubScreens.SETTINGS && navigationType == NavigationType.BOTTOM_NAVIGATION) {
            subScreen = SubScreens.MAIN
            settingsGoBack = true
        }
    }

    when (navigationType) {
        NavigationType.BOTTOM_NAVIGATION -> { // adapts the app when the navigation type is bottom navigation
            Scaffold(
                topBar = {
                    when (selectedScreen) {
                        SCREENS.SCHEDULE -> {
                            AppBar(
                                selectedDate = uiState.date, // the date displayed in the app bar
                                saveUiDate = viewModel::setDate, // a function to change the current date
                                chunksDivider = uiState.chunksDivider, // chunksDivider
                                saveChunksDivider = viewModel::setChunksDivider, // a function to change chunksDivider
                                subScreens = subScreen, // the current sub screen
                                onSubScreens = { subScreen = it }, // a lambda to change sub screen
                                onCurrentBlock = { currentBlock = it }, // a lambda to change currentBlock
                                onBlockSave = { blockSave = true }, // a lambda to to change blockSave to true
                                onBlockDelete = { blockDelete = true },  // a lambda to to change blockDelete to true
                                deletableBlock = deletableBlock, // deletableBlock
                                onDeletableBlock = { deletableBlock = false }, // a lambda to change deletableBlock to false
                                longClick = longClick, // longClick
                                onLongClick = { // a lambda to change longClick to false if the map contain a true value
                                    if (longClick.containsValue(true)) {
                                        longClick.keys.forEach { longClick[it] = false }
                                    }
                                },
                                onGroupDelete = viewModel::deleteTimeBlock, // a function that delete a time block
                                onSettingsGoBack = { settingsGoBack = it }, // a lambda that change settingsGoBack
                                navigationType = navigationType // the type of navigation
                            )
                        }

                        SCREENS.SAVED_TITLES -> { // when the screen is saved titles screen
                            CenterAlignedTopAppBar(title = { // a top app bar with Saved titles as a title
                                Text(
                                    text = "Saved titles",
                                    style = MaterialTheme.typography.titleLarge
                                )
                            })
                        }

                        SCREENS.STATISTICS -> { // when the screen is the statistics screen
                            StatisticsAppBar( // a app bar for the statistics screen
                                selectedDate = uiState.date, // the date
                                saveUiDate = viewModel::setDate, // a function to save the date
                                onLongClick = {}, // long click isn't used in the statistics screen so the lambda is empty
                                navigationType = navigationType // navigation type for adaptive layout
                            )
                        }
                    }
                },
                bottomBar = { // the bottom app bar for bottom navigation type
                    NavigationBar { // using navigation bar for bottom navigation
                        NavigationBarItem( // item in the navigation bar
                            icon = { // icon
                                Icon(
                                    painter = painterResource(
                                        R.drawable.round_view_list_24
                                    ),
                                    contentDescription = "Saved titles screen",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            },
                            label = { Text("Saved titles") },
                            selected = selectedScreen == SCREENS.SAVED_TITLES, // icon is selected when selected screen is saved titles
                            onClick = { selectedScreen = SCREENS.SAVED_TITLES } // when icon is clicked the screen is saved titles
                        )
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_view_day_24),
                                    contentDescription = "schedule screen"
                                )
                            },
                            label = { Text("Schedule") },
                            selected = selectedScreen == SCREENS.SCHEDULE && subScreen != SubScreens.SETTINGS,  // icon is selected when selected screen is schedule and the sub screen isn't settings
                            onClick = { selectedScreen = SCREENS.SCHEDULE; subScreen = SubScreens.MAIN} // when icon is clicked the screen is schedule and the sub screen is main
                        )
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    painter = painterResource(R.drawable.round_show_chart_24),
                                    contentDescription = "statistics screen"
                                )
                            },
                            label = { Text("Statistics") },
                            selected = selectedScreen == SCREENS.STATISTICS, // icon is selected when selected screen is statistics
                            onClick = { selectedScreen = SCREENS.STATISTICS } // when icon is clicked the screen is statistics
                        )
                    }
                },
            )
            { paddingValues ->
                viewModel.loadTitles()
                when (selectedScreen) {
                    SCREENS.SCHEDULE -> {
                        ScheduleScreen(
                            modifier = Modifier.padding(paddingValues), // gives padding by padding values
                            timeBlocks = uiState.timeBlocks, // pass the list of timeBlocks from the UiState
                            onSave = viewModel::insertTimeBlock, // a function to insert a timeBlock
                            subScreen = subScreen, // subScreen
                            onSubScreen = { subScreen = it }, // a lambda to change the sub screen
                            currentBlock = currentBlock, // current block
                            onCurrentBlock = { currentBlock = it }, // a lambda to change the current block
                            onBlockSave = { blockSave = false }, // a lambda to change blockSave to false
                            blockSave = blockSave, // block save
                            onBlockDelete = { blockDelete = false }, // a lambda to change block delete to false
                            blockDelete = blockDelete, // block delete
                            onDelete = viewModel::deleteTimeBlock, // a function to delete a timeBlock
                            onDeletableBlock = { deletableBlock = it }, // a lambda to change deletableBlock
                            onLongClick = { longClick[it] = longClick[it] != true }, // a lambda that change the timeBlock's value to its opposite (from true to false and vice versa)
                            longClick = longClick, // longClick
                            savedTitles = uiState.savedTitles, // a list of savedTitles
                            insertTitle = viewModel::insertTitle, // a function to insert a title
                            onNewTitle = { newTitle = it }, // a lambda to change newTitle
                            newTitle = newTitle, // newTitle
                            sortIndex = sortColorIndex, // the way colors are sorted
                            onSortIndex = { // a lambda to change sortColorIndex so it goes in a loop over each way to sort
                                sortColorIndex = when (sortColorIndex) {
                                    HSLA.H -> {
                                        HSLA.S // hue to saturation
                                    }

                                    HSLA.S -> {
                                        HSLA.L // saturation to lightness
                                    }

                                    HSLA.L -> {
                                        HSLA.A // lightness to alphabetical
                                    }

                                    HSLA.A -> {
                                        HSLA.H // alphabetical to hue
                                    }
                                }
                            },
                            date = uiState.date, // date
                            darkTheme = darkTheme, // dark theme
                            fontType = uiState.fontType, // font Type
                            fontSize = uiState.fontSize, // font Size
                            onChange = viewModel::updateSettings, // a function to update settings
                            navigationType = navigationType, // the navigation type
                            chunksDivider = uiState.chunksDivider, // chunksDivider
                            saveChunksDivider = viewModel::setChunksDivider, // a function to change chunksDivider
                            saveUiDate = viewModel::setDate, // a function to change date
                            deletableBlock = deletableBlock, // deletable block
                            onLongClickAfterDelete = {} // a lambda to reset longClick to false
                        )
                    }

                    SCREENS.SAVED_TITLES -> { // the screen when is saved titles
                        subScreen = SubScreens.MAIN // change the subScreen to mai n
                        TitleListScreen( // a function for the saved titles screen
                            modifier = Modifier.padding(paddingValues), // passes the padding
                            list = uiState.savedTitles, // the list of saved titles
                            insertTitle = viewModel::insertTitle, // a function to insert a title
                            onNewTitle = { newTitle = it }, // a lambda to change new title
                            newTitle = newTitle, // newTitle
                            deleteTitle = viewModel::deleteTitle, // a function to delete a title
                            onPlay = viewModel::onPlay, // a function to start an active timeBlock
                            onStop = viewModel::onStop, // a function to stop an active timeBlock
                            getPlayId = viewModel::getPlay, // a function that retrieve the id of the active time time block
                            sortIndex = sortColorIndex, // the way colors are sorted
                            navigationType = navigationType, // navigation type
                            onSortIndex = { // a lambda to change sortColorIndex so it goes in a loop over each way to sort
                                sortColorIndex = when (sortColorIndex) {
                                    HSLA.H -> {
                                        HSLA.S // hue to saturation
                                    }

                                    HSLA.S -> {
                                        HSLA.L // saturation to lightness
                                    }

                                    HSLA.L -> {
                                        HSLA.A // lightness to alphabetical
                                    }

                                    HSLA.A -> {
                                        HSLA.H // alphabetical to hue
                                    }
                                }
                            })
                    }

                    SCREENS.STATISTICS -> {
                        subScreen = SubScreens.MAIN // change the sub screen to main
                        viewModel.getDatesInRange( // a function the gives all time blocks within a date range
                            startDate = uiState.date, // date
                            endDate = uiState.date, // date
                            callback = { statsBlockList.value = it }) // a lambda that change the statsBlockList
                        Statistics(  // a function for the statistics screen
                            modifier = Modifier.padding(paddingValues), // passes padding
                            blockList = statsBlockList.value, // a list of timeBlocks
                            rangeStart = uiState.date, // start of the range
                            rangeEnd = uiState.date, // end of the range
                            savedTitles = uiState.savedTitles, // a list of the saved titles
                            fontType = uiState.fontType, // font type
                            fontSize = uiState.fontSize, // font size
                            navigationType = navigationType // navigation type
                        )
                    }
                }
            }
        }
        NavigationType.PERMANENT_NAVIGATION_DRAWER -> { // adapts the app when the navigation type is permanent navigation drawer
            Scaffold()
            { paddingValues ->
                viewModel.loadTitles() // loads the current titles
                when (selectedScreen) {
                    SCREENS.SCHEDULE -> {

                        PermanentNavigationDrawer(drawerContent = {
                            PermanentDrawerSheet {
                                Column(
                                    modifier = Modifier.fillMaxHeight(), // fills max height
                                    verticalArrangement = Arrangement.SpaceEvenly // even space between it's children
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth() // fills max width
                                            .clickable { // when clicked the selected screen is schedule and sub screen is settings
                                                selectedScreen =
                                                    SCREENS.SCHEDULE; subScreen =
                                                SubScreens.SETTINGS
                                            },
                                        horizontalArrangement = Arrangement.Start, // arrange from start
                                        verticalAlignment = Alignment.CenterVertically // aligns from vertical center
                                    ) {
                                        Spacer(modifier = Modifier.width(30.dp)) // creates a empty space of 30.dp
                                        Icon(
                                            modifier = Modifier.size(48.dp), // icon in size of 48.dp
                                            imageVector = Icons.Rounded.Settings,
                                            contentDescription = "settings"
                                        )
                                        Spacer(modifier = Modifier.width(30.dp)) // creates a empty space of 30.dp
                                        Text(
                                            modifier = Modifier, // passes the modifier
                                            text = "Settings page",
                                            fontSize = 24.sp // text size is 24.dp
                                        )
                                        Spacer(modifier = Modifier.weight(2f)) // a empty space that fills the remaining space
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth() // fills max width
                                            .clickable { // when clicked the screen is saved titles and the sub screen is main
                                                selectedScreen =
                                                    SCREENS.SAVED_TITLES; subScreen =
                                                SubScreens.MAIN
                                            },
                                        horizontalArrangement = Arrangement.Start, // arrange from start
                                        verticalAlignment = Alignment.CenterVertically // align from vertical center
                                    ) {
                                        Spacer(modifier = Modifier.width(30.dp)) // fills empty width of 30.dp
                                        Icon(
                                            modifier = Modifier.size(48.dp), // icon in size of 48.dp
                                            painter = painterResource(R.drawable.round_view_list_24),
                                            contentDescription = "Saved titles screen",
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Spacer(modifier = Modifier.width(30.dp)) // fills empty width of 30.dp
                                        Text(
                                            modifier = Modifier, // passes modifier
                                            text = "Saved titles page", // text
                                            fontSize = 24.sp // font size is 24.dp
                                        )
                                        Spacer(modifier = Modifier.weight(2f)) //
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                selectedScreen =
                                                    SCREENS.SCHEDULE; subScreen =
                                                SubScreens.MAIN
                                            },
                                        horizontalArrangement = Arrangement.Start,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Spacer(modifier = Modifier.width(30.dp))
                                        Icon(
                                            modifier = Modifier.size(48.dp), // Adjust the icon size as needed
                                            painter = painterResource(R.drawable.baseline_view_day_24),
                                            contentDescription = "schedule screen",
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Spacer(modifier = Modifier.width(30.dp))
                                        Text(
                                            modifier = Modifier, // Expand the text to use available space
                                            text = "Schedule page",
                                            fontSize = 24.sp // Adjust the text size as needed
                                        )
                                        Spacer(modifier = Modifier.weight(2f)) // a empty space that fills the remaining space
                                    }
                                    Row (modifier = Modifier
                                        .fillMaxWidth() // fills empty max width
                                        .clickable { // when clicked the screen change to statistics and the sub screen changes to main
                                            selectedScreen =
                                                SCREENS.STATISTICS; subScreen =
                                            SubScreens.MAIN
                                        },
                                        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start){  // arrange from start // align from vertical center
                                        Spacer(modifier = Modifier.width(30.dp)) // empty width of 30.dp
                                        Icon(modifier = Modifier.size(48.dp), // icon in size of 48.dp
                                            painter = painterResource(R.drawable.round_show_chart_24),
                                            contentDescription = "statistics screen"
                                        )
                                        Spacer(modifier = Modifier.width(30.dp)) // empty eidth of 30.dp
                                        Text(modifier = Modifier, text = "Statistics page", fontSize = 24.sp) // font size of 24.sp
                                        Spacer(modifier = Modifier.weight(2f)) // empty space that fills the remaining space
                                    }
                                }
                            }}) {
                            Row(
                                horizontalArrangement = Arrangement.Start, // arrange from start
                                modifier = Modifier.padding(vertical = 5.dp) // vertical padding of 5.dp
                            ) {


                                ScheduleScreen(
                                    modifier = Modifier.padding(paddingValues), // gives padding by padding values
                                    timeBlocks = uiState.timeBlocks, // pass the list of timeBlocks from the UiState
                                    onSave = viewModel::insertTimeBlock, // a function to insert a timeBlock
                                    subScreen = subScreen, // subScreen
                                    onSubScreen = { subScreen = it }, // a lambda to change the sub screen
                                    currentBlock = currentBlock, // current block
                                    onCurrentBlock = { currentBlock = it }, // a lambda to change the current block
                                    onBlockSave = { blockSave = false }, // a lambda to change blockSave to false
                                    blockSave = blockSave, // block save
                                    onBlockDelete = { blockDelete = false }, // a lambda to change block delete to false
                                    blockDelete = blockDelete, // block delete
                                    onDelete = viewModel::deleteTimeBlock, // a function to delete a timeBlock
                                    onDeletableBlock = { deletableBlock = it }, // a lambda to change deletableBlock
                                    onLongClick = { longClick[it] = longClick[it] != true }, // a lambda that change the timeBlock's value to its opposite (from true to false and vice versa)
                                    longClick = longClick, // longClick
                                    savedTitles = uiState.savedTitles, // a list of savedTitles
                                    insertTitle = viewModel::insertTitle, // a function to insert a title
                                    onNewTitle = { newTitle = it }, // a lambda to change newTitle
                                    newTitle = newTitle, // newTitle
                                    sortIndex = sortColorIndex, // the way colors are sorted
                                    onSortIndex = { // a lambda to change sortColorIndex so it goes in a loop over each way to sort
                                        sortColorIndex = when (sortColorIndex) {
                                            HSLA.H -> {
                                                HSLA.S // hue to saturation
                                            }

                                            HSLA.S -> {
                                                HSLA.L // saturation to lightness
                                            }

                                            HSLA.L -> {
                                                HSLA.A // lightness to alphabetical
                                            }

                                            HSLA.A -> {
                                                HSLA.H // alphabetical to hue
                                            }
                                        }
                                    },
                                    date = uiState.date, // date
                                    darkTheme = darkTheme, // dark theme
                                    fontType = uiState.fontType, // font Type
                                    fontSize = uiState.fontSize, // font Size
                                    onChange = viewModel::updateSettings, // a function to update settings
                                    navigationType = navigationType, // the navigation type
                                    chunksDivider = uiState.chunksDivider, // chunksDivider
                                    saveChunksDivider = viewModel::setChunksDivider, // a function to change chunksDivider
                                    saveUiDate = viewModel::setDate, // a function to change date
                                    deletableBlock = deletableBlock, // deletable block
                                    onLongClickAfterDelete = {
                                        if (longClick.containsValue(true)) { // reset all longClick values to false
                                            longClick.keys.forEach { longClick[it] = false }
                                        }
                                    }
                                )

                                CalendarColumn( // a column to choose date from a column
                                    selectedDate = uiState.date, // date
                                    saveUiDate = viewModel::setDate, // a function to change date
                                    onLongClick = {}) // the lambda isn't needed so it does nothing
                            }
                        }
                    }

                    SCREENS.SAVED_TITLES -> { // when the screen is saved titles
                        subScreen = SubScreens.MAIN  // changes sub screen to main

                        PermanentNavigationDrawer(drawerContent = {
                            PermanentDrawerSheet {
                                Column(
                                    modifier = Modifier.fillMaxHeight(), // fills the max height
                                    verticalArrangement = Arrangement.SpaceEvenly // arrange with even spaces
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth() // fills max width
                                            .clickable { // when clicked the screen changes to schedule and sub screen changes to settings
                                                selectedScreen =
                                                    SCREENS.SCHEDULE; subScreen =
                                                SubScreens.SETTINGS
                                            },
                                        horizontalArrangement = Arrangement.Start, // arrange from start
                                        verticalAlignment = Alignment.CenterVertically // align at vertical center
                                    ) {
                                        Spacer(modifier = Modifier.width(30.dp)) // fills 30.dp of width
                                        Icon(
                                            modifier = Modifier.size(48.dp), // icon size is 48.dp
                                            imageVector = Icons.Rounded.Settings,
                                            contentDescription = "settings"
                                        )
                                        Spacer(modifier = Modifier.width(30.dp)) // fills 30.dp of width
                                        Text(
                                            modifier = Modifier, // modifier
                                            text = "Settings page", // text
                                            fontSize = 24.sp // font size os 24.sp
                                        )
                                        Spacer(modifier = Modifier.weight(2f)) // fills the remaining space
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth() // fills max width
                                            .clickable { // when clicked the screen is saved titles and sub screen is main
                                                selectedScreen =
                                                    SCREENS.SAVED_TITLES; subScreen =
                                                SubScreens.MAIN
                                            },
                                        horizontalArrangement = Arrangement.Start, // arrange from start
                                        verticalAlignment = Alignment.CenterVertically // align from vertical center
                                    ) {
                                        Spacer(modifier = Modifier.width(30.dp)) // fills 30.dp of width
                                        Icon(
                                            modifier = Modifier.size(48.dp), // Icon size is 48.dp
                                            painter = painterResource(R.drawable.round_view_list_24),
                                            contentDescription = "Saved titles screen",
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Spacer(modifier = Modifier.width(30.dp)) // fills 30.dp of width
                                        Text(
                                            modifier = Modifier,
                                            text = "Saved titles page",
                                            fontSize = 24.sp // text size 24.dp
                                        )
                                        Spacer(modifier = Modifier.weight(2f)) // fills the remaining space
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth() // fills max width
                                            .clickable { // when clicked screen is schedule and sub screen is main
                                                selectedScreen =
                                                    SCREENS.SCHEDULE; subScreen =
                                                SubScreens.MAIN
                                            },
                                        horizontalArrangement = Arrangement.Start, // arrange from start
                                        verticalAlignment = Alignment.CenterVertically // align from vertical center
                                    ) {
                                        Spacer(modifier = Modifier.width(30.dp)) // fills 30.dp width of space
                                        Icon(
                                            modifier = Modifier.size(48.dp), // icon size of 48,dp
                                            painter = painterResource(R.drawable.baseline_view_day_24),
                                            contentDescription = "schedule screen",
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Spacer(modifier = Modifier.width(30.dp)) // fills 30.dp of width
                                        Text(
                                            modifier = Modifier,
                                            text = "Schedule page", // text
                                            fontSize = 24.sp // font size of 24.sp
                                        )
                                        Spacer(modifier = Modifier.weight(2f)) // fills the remaining space
                                    }
                                    Row (modifier = Modifier
                                        .fillMaxWidth() // fills max width
                                        .clickable { // when clicked screen is statistics and sub screen is main
                                            selectedScreen =
                                                SCREENS.STATISTICS; subScreen =
                                            SubScreens.MAIN
                                        },
                                        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start){  // arrange from start // align from vertical center
                                        Spacer(modifier = Modifier.width(30.dp)) // empty 30.dp of width
                                        Icon(modifier = Modifier.size(48.dp), // icon size is 48.dp
                                            painter = painterResource(R.drawable.round_show_chart_24),
                                            contentDescription = "statistics screen"
                                        )
                                        Spacer(modifier = Modifier.width(30.dp)) // empty width of 30.dp
                                        Text(modifier = Modifier, text = "Statistics page", fontSize = 24.sp) // font size is 24.sp
                                        Spacer(modifier = Modifier.weight(2f)) // fills the remaining space
                                    }
                                }
                            }}) {
                            Row(
                                horizontalArrangement = Arrangement.Start,   // arrange from start
                                modifier = Modifier.padding(vertical = 5.dp) // 5.dp vertical padding
                            ) {

                                TitleListScreen( // function for the title list screen
                                    modifier = Modifier.padding(paddingValues), // passes padding
                                    list = uiState.savedTitles, // a list of saved titles
                                    insertTitle = viewModel::insertTitle, // a function to insert a title
                                    onNewTitle = { newTitle = it }, // a lambda to change newTitle
                                    newTitle = newTitle, // newTitle
                                    deleteTitle = viewModel::deleteTitle, // a function to delete title
                                    onPlay = viewModel::onPlay, // a function to start an active timeBlock
                                    onStop = viewModel::onStop, // a function to stop an active timeBlock
                                    getPlayId = viewModel::getPlay, // a function that retrieve the id of the active time time block
                                    sortIndex = sortColorIndex, // the way colors are sorted
                                    navigationType = navigationType, // navigation type
                                    onSortIndex = { // a lambda to change sortColorIndex so it goes in a loop over each way to sort
                                        sortColorIndex = when (sortColorIndex) {
                                            HSLA.H -> {
                                                HSLA.S // hue to saturation
                                            }

                                            HSLA.S -> {
                                                HSLA.L // saturation to lightness
                                            }

                                            HSLA.L -> {
                                                HSLA.A // lightness to alphabetical
                                            }

                                            HSLA.A -> {
                                                HSLA.H // alphabetical to hue
                                            }
                                        }
                                    })
                            }

                        }
                    }

                    SCREENS.STATISTICS -> {
                        subScreen = SubScreens.MAIN // change the sub screen to main
                        viewModel.getDatesInRange( // a function the gives all time blocks within a date range
                            startDate = uiState.date, // date
                            endDate = uiState.date, // date
                            callback = { statsBlockList.value = it }) // a lambda that change the statsBlockList

                                PermanentNavigationDrawer(drawerContent = {
                                        PermanentDrawerSheet {
                                            Column(
                                                modifier = Modifier.fillMaxHeight(),
                                                verticalArrangement = Arrangement.SpaceEvenly
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            selectedScreen =
                                                                SCREENS.SCHEDULE; subScreen =
                                                            SubScreens.SETTINGS
                                                        },
                                                    horizontalArrangement = Arrangement.Start,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Spacer(modifier = Modifier.width(30.dp))
                                                    Icon(
                                                        modifier = Modifier.size(48.dp),
                                                        imageVector = Icons.Rounded.Settings,
                                                        contentDescription = "settings"
                                                    )
                                                    Spacer(modifier = Modifier.width(30.dp))
                                                    Text(
                                                        modifier = Modifier,
                                                        text = "Settings page",
                                                        fontSize = 24.sp
                                                    )
                                                    Spacer(modifier = Modifier.weight(2f))
                                                }

                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            selectedScreen =
                                                                SCREENS.SAVED_TITLES; subScreen =
                                                            SubScreens.MAIN
                                                        },
                                                    horizontalArrangement = Arrangement.Start,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Spacer(modifier = Modifier.width(30.dp))
                                                    Icon(
                                                        modifier = Modifier.size(48.dp), // Adjust the icon size as needed
                                                        painter = painterResource(R.drawable.round_view_list_24),
                                                        contentDescription = "Saved titles screen",
                                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                                    )
                                                    Spacer(modifier = Modifier.width(30.dp))
                                                    Text(
                                                        modifier = Modifier, // Expand the text to use available space
                                                        text = "Saved titles page",
                                                        fontSize = 24.sp // Adjust the text size as needed
                                                    )
                                                    Spacer(modifier = Modifier.weight(2f))
                                                }

                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            selectedScreen =
                                                                SCREENS.SCHEDULE; subScreen =
                                                            SubScreens.MAIN
                                                        },
                                                    horizontalArrangement = Arrangement.Start,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Spacer(modifier = Modifier.width(30.dp))
                                                    Icon(
                                                        modifier = Modifier.size(48.dp), // Adjust the icon size as needed
                                                        painter = painterResource(R.drawable.baseline_view_day_24),
                                                        contentDescription = "schedule screen",
                                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                                    )
                                                    Spacer(modifier = Modifier.width(30.dp))
                                                    Text(
                                                        modifier = Modifier, // Expand the text to use available space
                                                        text = "Schedule page",
                                                        fontSize = 24.sp // Adjust the text size as needed
                                                    )
                                                    Spacer(modifier = Modifier.weight(2f))
                                                }
                                                Row (modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        selectedScreen =
                                                            SCREENS.STATISTICS; subScreen =
                                                        SubScreens.MAIN
                                                    },
                                                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start){
                                                    Spacer(modifier = Modifier.width(30.dp))
                                                    Icon(modifier = Modifier.size(48.dp), // Adjust the icon size as needed
                                                        painter = painterResource(R.drawable.round_show_chart_24),
                                                        contentDescription = "statistics screen"
                                                    )
                                                    Spacer(modifier = Modifier.width(30.dp))
                                                    Text(modifier = Modifier, text = "Statistics page", fontSize = 24.sp) // Adjust the text size as needed)
                                                    Spacer(modifier = Modifier.weight(2f))
                                                }
                                            }
                                        }}) {
                                    Row(
                                        horizontalArrangement = Arrangement.Start,
                                        modifier = Modifier.padding(vertical = 5.dp)
                                    ) {

                                        Statistics(  // a function for the statistics screen
                                            modifier = Modifier
                                                .padding(paddingValues)
                                                .weight(1f), // passes padding
                                            blockList = statsBlockList.value, // a list of timeBlocks
                                            rangeStart = uiState.date, // start of the range
                                            rangeEnd = uiState.date, // end of the range
                                            savedTitles = uiState.savedTitles, // a list of the saved titles
                                            fontType = uiState.fontType, // font type
                                            fontSize = uiState.fontSize, // font size
                                            navigationType = navigationType // navigation type
                                        )

                                        CalendarColumn( // a column to choose date from a column
                                            selectedDate = uiState.date, // date
                                            saveUiDate = viewModel::setDate, // a function to change date
                                            onLongClick = {}) // the lambda isn't needed so it does nothing
                                    }

                                }
                        }
                }
            }
        }
        else -> { // adapts the app when the navigation type is navigation rail
            Scaffold()
            { paddingValues ->
                viewModel.loadTitles()
                when (selectedScreen) {
                    SCREENS.SCHEDULE -> {
                        Row(horizontalArrangement = Arrangement.Start,  modifier = Modifier.padding(vertical = 2.dp)) {
                            NavigationRail (modifier = Modifier.fillMaxHeight()){
                                Spacer(modifier = Modifier.weight(0.5f))
                                NavigationRailItem(
                                    icon = {
                                        Icon(
                                            Icons.Rounded.Settings,
                                            contentDescription = "settings"
                                        )
                                    },
                                    label = { Text("Settings") },
                                    selected = subScreen == SubScreens.SETTINGS && selectedScreen == SCREENS.SCHEDULE,
                                    onClick = { subScreen = SubScreens.SETTINGS; selectedScreen =
                                        SCREENS.SCHEDULE
                                    }
                                )
                                Spacer(modifier = Modifier.weight(0.5f))
                                NavigationRailItem(
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
                                    onClick = { selectedScreen = SCREENS.SAVED_TITLES }
                                )
                                Spacer(modifier = Modifier.weight(0.5f))
                                NavigationRailItem(
                                    icon = {
                                        Icon(
                                            painter = painterResource(R.drawable.baseline_view_day_24),
                                            contentDescription = "schedule screen"
                                        )
                                    },
                                    label = { Text("Schedule") },
                                    selected = subScreen != SubScreens.SETTINGS && selectedScreen == SCREENS.SCHEDULE,
                                    onClick = { selectedScreen = SCREENS.SCHEDULE; subScreen = SubScreens.MAIN }
                                )
                                Spacer(modifier = Modifier.weight(0.5f))
                                NavigationRailItem(
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
                                Spacer(modifier = Modifier.weight(0.5f))
                            }

                            ScheduleScreen(
                                modifier = Modifier
                                    .weight(1f),
                                timeBlocks = uiState.timeBlocks,
                                onSave = viewModel::insertTimeBlock,
                                subScreen = subScreen,
                                onSubScreen = { subScreen = it },
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
                                onNewTitle = { newTitle = it },
                                newTitle = newTitle,
                                sortIndex = sortColorIndex,
                                onSortIndex = {
                                    sortColorIndex = when (sortColorIndex) {
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
                                    }
                                },
                                date = uiState.date,
                                darkTheme = darkTheme,
                                fontType = uiState.fontType,
                                fontSize = uiState.fontSize,
                                onChange = viewModel::updateSettings,
                                navigationType = navigationType,
                                chunksDivider = uiState.chunksDivider,
                                saveChunksDivider = viewModel::setChunksDivider,
                                saveUiDate = viewModel::setDate,
                                deletableBlock = deletableBlock,
                                onLongClickAfterDelete = {
                                    if (longClick.containsValue(true)) {
                                        longClick.keys.forEach { longClick[it] = false }
                                    }
                                }
                            )
                            CalendarColumn(
                                selectedDate = uiState.date,
                                saveUiDate = viewModel::setDate,
                                onLongClick = {
                                    if (longClick.containsValue(true)) {
                                        longClick.keys.forEach { longClick[it] = false }
                                    }
                                })
                        }
                    }

                    SCREENS.SAVED_TITLES -> {
                        subScreen = SubScreens.MAIN

                        Row(horizontalArrangement = Arrangement.Start,  modifier = Modifier.padding(vertical = 2.dp)) {
                            NavigationRail (modifier = Modifier.fillMaxHeight()){
                                Spacer(modifier = Modifier.weight(0.5f))
                                NavigationRailItem(
                                    icon = {
                                        Icon(
                                            Icons.Rounded.Settings,
                                            contentDescription = "settings"
                                        )
                                    },
                                    label = { Text("Settings") },
                                    selected = subScreen == SubScreens.SETTINGS && selectedScreen == SCREENS.SCHEDULE,
                                    onClick = { subScreen = SubScreens.SETTINGS; selectedScreen =
                                        SCREENS.SCHEDULE
                                    }
                                )
                                Spacer(modifier = Modifier.weight(0.5f))
                                NavigationRailItem(
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
                                    onClick = { selectedScreen = SCREENS.SAVED_TITLES }
                                )
                                Spacer(modifier = Modifier.weight(0.5f))
                                NavigationRailItem(
                                    icon = {
                                        Icon(
                                            painter = painterResource(R.drawable.baseline_view_day_24),
                                            contentDescription = "schedule screen"
                                        )
                                    },
                                    label = { Text("Schedule") },
                                    selected = subScreen != SubScreens.SETTINGS && selectedScreen == SCREENS.SCHEDULE,
                                    onClick = { selectedScreen = SCREENS.SCHEDULE; subScreen = SubScreens.MAIN }
                                )
                                Spacer(modifier = Modifier.weight(0.5f))
                                NavigationRailItem(
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
                                Spacer(modifier = Modifier.weight(0.5f))
                            }

                            TitleListScreen(
                                modifier = Modifier.padding(paddingValues),
                                list = uiState.savedTitles,
                                insertTitle = viewModel::insertTitle,
                                onNewTitle = { newTitle = it },
                                newTitle = newTitle,
                                deleteTitle = viewModel::deleteTitle,
                                onPlay = viewModel::onPlay,
                                onStop = viewModel::onStop,
                                getPlayId = viewModel::getPlay,
                                sortIndex = sortColorIndex,
                                onSortIndex = {
                                sortColorIndex = when (sortColorIndex) {
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
                                }
                            }, navigationType = navigationType)
                        }
                    }

                    SCREENS.STATISTICS -> {
                        subScreen = SubScreens.MAIN
                        viewModel.getDatesInRange(
                            startDate = uiState.date,
                            endDate = uiState.date,
                            callback = { statsBlockList.value = it })

                            Row(horizontalArrangement = Arrangement.Start,  modifier = Modifier.padding(vertical = 2.dp)) {
                                NavigationRail (modifier = Modifier.fillMaxHeight()){
                                    Spacer(modifier = Modifier.weight(0.5f))
                                    NavigationRailItem(
                                        icon = {
                                            Icon(
                                                Icons.Rounded.Settings,
                                                contentDescription = "settings"
                                            )
                                        },
                                        label = { Text("Settings") },
                                        selected = subScreen == SubScreens.SETTINGS && selectedScreen == SCREENS.SCHEDULE,
                                        onClick = { subScreen = SubScreens.SETTINGS; selectedScreen =
                                            SCREENS.SCHEDULE
                                        }
                                    )
                                    Spacer(modifier = Modifier.weight(0.5f))
                                    NavigationRailItem(
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
                                        onClick = { selectedScreen = SCREENS.SAVED_TITLES }
                                    )
                                    Spacer(modifier = Modifier.weight(0.5f))
                                    NavigationRailItem(
                                        icon = {
                                            Icon(
                                                painter = painterResource(R.drawable.baseline_view_day_24),
                                                contentDescription = "schedule screen"
                                            )
                                        },
                                        label = { Text("Schedule") },
                                        selected = subScreen != SubScreens.SETTINGS && selectedScreen == SCREENS.SCHEDULE,
                                        onClick = { selectedScreen = SCREENS.SCHEDULE; subScreen = SubScreens.MAIN }
                                    )
                                    Spacer(modifier = Modifier.weight(0.5f))
                                    NavigationRailItem(
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
                                    Spacer(modifier = Modifier.weight(0.5f))
                                }

                            Statistics(
                                modifier = Modifier
                                    .padding(paddingValues)
                                    .weight(1f),
                                blockList = statsBlockList.value,
                                rangeStart = uiState.date,
                                rangeEnd = uiState.date,
                                savedTitles = uiState.savedTitles,
                                fontType = uiState.fontType,
                                fontSize = uiState.fontSize,
                                navigationType = navigationType
                            )
                                CalendarColumn(
                                    selectedDate = uiState.date,
                                    saveUiDate = viewModel::setDate,
                                    onLongClick = {})

                        }
                    }
                }
            }
        }
    }
}


