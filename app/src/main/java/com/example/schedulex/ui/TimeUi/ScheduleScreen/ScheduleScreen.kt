package com.example.schedulex.ui.TimeUi.ScheduleScreen

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.schedulex.data.local.database.Time
import com.example.schedulex.data.local.database.Title
import com.example.schedulex.ui.TimeUi.AppBars.CustomDialog
import com.example.schedulex.ui.TimeUi.AppBars.`Date-picker`
import com.example.schedulex.ui.TimeUi.HSLA
import com.example.schedulex.ui.TimeUi.NavigationType
import com.example.schedulex.ui.TimeUi.ScheduleScreen.CreationSubScreen.BlockCreationScreen
import com.example.schedulex.ui.TimeUi.ScheduleScreen.MainSubScreen.DisplayBlockList
import com.example.schedulex.ui.TimeUi.ScheduleScreen.SettingsSubScreen.SettingsPage
import com.example.schedulex.ui.ViewModels.CreationViewModel
import com.example.schedulex.R
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.reflect.KFunction2
import kotlin.reflect.KFunction3
import kotlin.reflect.KFunction8

enum class SubScreens{ // The 3 different sub screens
    CREATION, MAIN, SETTINGS
}


@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun ScheduleScreen(
    modifier: Modifier = Modifier, // modifier
    timeBlocks: List<Time>, // a list of time blocks
    onSave: KFunction8<String, String, String, String, String, Int, String, Context, Unit>, // a suspend function that saves a time block
    subScreen: SubScreens, // sub screen
    onSubScreen: (SubScreens) -> Unit, // a lambda that change the sub screen
    currentBlock: Boolean, // a boolean that represent if currentBlock was activated
    onCurrentBlock: (Boolean) -> Unit, // a lambda that change current block
    onBlockSave: () -> Unit, // a lambda that change block save
    blockSave: Boolean,  // block save
    onBlockDelete: () -> Unit, // a lambda that change block delete
    blockDelete: Boolean, // block delete
    onDelete: KFunction2<Int, Context, Unit>, // a suspend function that delete a time block
    onDeletableBlock: (Boolean) -> Unit, // a lambda that change deletable block
    longClick: Map<Time, Boolean>, // a map that contains keys that are time blocks and values that are booleans. The booleans represent if the user long clicked on each block or not.
    onLongClick: (Time) -> Unit, // a lambda that change the value of the key in the map
    savedTitles: List<Title>, // a list of Title
    onNewTitle: (Boolean) -> Unit, // a lambda that change newTitle
    newTitle: Boolean, // determine if a new title dialog is shown or not
    insertTitle: KFunction3<String, String, Int, Unit>, // a suspend function for inserting a title
    sortIndex: HSLA, // the method of sorting colors in new title dialog
    onSortIndex: (HSLA) -> Unit, // change the sorting method
    date: String, // date
    darkTheme: Boolean, // dark theme
    fontSize: String, // font size
    fontType: String, // font type
    onChange: (darkTheme: Boolean, fontSize: String, fontType: String) -> Unit, // a lambda for changing the settings
    navigationType: NavigationType, // navigation type
    saveChunksDivider: (chunksDivider: Int) -> Unit, // a lambda to change chunksDivider
    chunksDivider: Int, // chunksDivider
    saveUiDate: (String) -> Unit, // a lambda to change date
    deletableBlock: Boolean, // true if the block is deletable, otherwise false
    onLongClickAfterDelete: () -> Unit // a lambda to reset long click's values to false
) {
    val creationViewModel: CreationViewModel = hiltViewModel() // the creation viewModel
    val creationUiState = creationViewModel.uiState.collectAsStateWithLifecycle() // creation uiState

     var startTime = creationUiState.value.startTime // the start time of the timeBlock

    var endTime = creationUiState.value.endTime // the end time of the timeBlock

    var title = creationUiState.value.title // the title of the timeBlock

    var text = creationUiState.value.text // the text of the timeBlock

    var color = creationUiState.value.color // the color of the timeBlock

    var id = creationUiState.value.id // the id of the timeBlock

    var notification by rememberSaveable { mutableStateOf("") } // the notification time of the timeBlock. "" means no notification, other wise time in this format: hh-mm-dd-mm-yyyy

    val currentTime = LocalTime.now() // current time in LocalTime

    var expanded by rememberSaveable { mutableStateOf(false) } // expand button for rail navigation and permanent navigation bar types

    var expandedChunksDivider by rememberSaveable{ mutableStateOf(false) } // expand for chunks divider dialog

    var showSlider by rememberSaveable { mutableStateOf(false)} // a boolean for showing the custom dialog for chunksDivider

    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy") // format for date

    val datePicker = try { // if date is invalid gets the current date in the format
        LocalDate.parse(date, formatter)
    } catch (e: DateTimeParseException) {
        LocalDate.now() // default value
    }

    val isDatePickerDialogOpen = remember { mutableStateOf(false) } // boolean for the date picker dialog

    if (isDatePickerDialogOpen.value) {
        `Date-picker`(initialDate = datePicker, onDismiss = {isDatePickerDialogOpen.value = false}, onSaveDate = saveUiDate, modifier = Modifier.padding(15.dp), navigationType = navigationType)
        // It update `selectedDate` when a new date is selected
        // and set `isDatePickerDialogOpen.value` to false when dismissed
    }



    Box(modifier = modifier.fillMaxSize()) {
            if (subScreen == SubScreens.MAIN) { // when sub screen is main

                DisplayBlockList( // a function for the main screen
                    timeBlocks = timeBlocks, // list of time blocks to display
                    onBlockInput = { subScreenLam, startTimeLam, endTimeLam, chosenNotification, chosenText, chosenTitle, chosenId, chosenColor->
                        onSubScreen(subScreenLam) // the lambda updates the parameters

                        startTime = if (startTimeLam == "24:00") {
                            "00:00"
                        } else {
                            startTimeLam
                        }
                        endTime = if (endTimeLam == "24:00") {
                            "00:00"
                        } else {
                            endTimeLam
                        }

                        notification = chosenNotification
                        title = chosenTitle
                        text = chosenText
                        id = chosenId
                        color = chosenColor

                        if(id != -1) {onDeletableBlock(true)} else{ onDeletableBlock(false)}
                        expanded = false
                    },
                    currentBlock = currentBlock ,
                    onCurrentBlock = onCurrentBlock,
                    onLongClick = onLongClick,
                    longClick = longClick,
                    fontSize = fontSize,
                    fontType = fontType,
                    expendedIcons = {expanded = false}, // change expanded to false
                    saveCreationState = creationViewModel::saveUiState // save to creation uiState so the screen survive configuration change
                )

                if(longClick.containsValue(true)) { // chunks are blocks with id of -1 meaning the user didn't create them
                    var deletableGroup = true // delete the group of selected blocks in long click

                    for ((key, value) in longClick) {
                        if (value) {
                            if (key.uid == -1){ // -1 = chunk
                                deletableGroup = false
                            }
                        }
                    }

                    if(deletableGroup && navigationType != NavigationType.BOTTOM_NAVIGATION) { //true as long theres no blocks that arent deletable
                        val context = LocalContext.current
                        FloatingActionButton(onClick = { // deletes each block that was long clicked on by the user
                            val deletedGroup = mutableListOf<Int>()

                            longClick.forEach{
                                if(it.value){
                                    deletedGroup.add(it.key.uid)
                                }
                            }
                            deletedGroup.forEach{
                                onDelete(it, context) // same as onGroupDelete
                            }
                            onLongClickAfterDelete() // same as onLongClick in app bar
                        },  modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(15.dp), containerColor = MaterialTheme.colorScheme.errorContainer) {
                            Icon(
                                Icons.Filled.Delete, contentDescription = "delete a group of blocks"
                            )
                        }
                    }
                }
                else {
                    FloatingActionButton( // creates a new time block without clicking on an existing one
                        onClick = {

                            expanded = false
                            onSubScreen(SubScreens.CREATION)
                            startTime = "${currentTime.hour.toString().padStart(2, '0')}:${
                                currentTime.minute.toString().padStart(2, '0')
                            }"
                            endTime = "${currentTime.hour.toString().padStart(2, '0')}:${
                                currentTime.minute.toString().padStart(2, '0')
                            }"

                            // gives the new block default parameters
                            notification = ""
                            title = ""
                            text = ""
                            id = -1 // new block, in the viewModel when hte block is inserted the id will be unique to the user's new block
                            color = ""

                            onDeletableBlock(false) // disables deletable block

                            creationViewModel.saveUiState( title, text, startTime, endTime, color, id) // save to creation uiState so the screen survive configuration change
                        },

                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(15.dp)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Floating action button")
                    }
                }

                if(navigationType == NavigationType.NAVIGATION_RAIL || navigationType == NavigationType.PERMANENT_NAVIGATION_DRAWER) { // when dealing with navigation rail and permanent navigation
                    Column(modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(15.dp)
                        .animateContentSize( // animation when the column size suddenly changes
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )) {

                    FloatingActionButton(
                        modifier = Modifier,
                        onClick = { expanded = !expanded },
                    ) {

                        Icon(
                            imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = stringResource(R.string.time_block_expand_button_content_description),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                        if (expanded) { // if button is clicked, shows a column of icons
                            IconButton(
                                onClick = { expandedChunksDivider = true ; expanded = false },
                            ) {
                                Icon(
                                    Icons.Filled.Build,
                                    contentDescription = "change the size of the chunks"
                                )
                            }
                            IconButton(onClick = { onCurrentBlock(true); expanded = false }) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_beenhere_24),
                                    contentDescription = "slide to the current block"
                                )
                            }
                            IconButton(
                                onClick = {  isDatePickerDialogOpen.value = true ; expanded = false },
                            ) {
                                Icon(
                                    Icons.Rounded.DateRange,
                                    contentDescription = "open date picker dialog"
                                )
                            }
                        }
                        val options = listOf("15 Minutes", "30 Minutes", "1 Hour", "Dynamic", "Custom") // options for chunksDivider
                        var selectedOptionText by remember { mutableStateOf(options[0]) }

                        MaterialTheme(
                            shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(16.dp))
                        ) {
                            DropdownMenu(expanded = expandedChunksDivider, onDismissRequest = {  expandedChunksDivider = false }) {
                                options.forEach { selectionOption ->
                                    DropdownMenuItem(onClick = {
                                        selectedOptionText = selectionOption // on select closes the drop down menu
                                        expandedChunksDivider = false
                                        when (selectionOption) {
                                            "15 Minutes" -> {
                                                saveChunksDivider(15)
                                            }

                                            "30 Minutes" -> {
                                                saveChunksDivider(30)
                                            }

                                            "1 Hour" -> {
                                                saveChunksDivider(60)
                                            }

                                            "Dynamic" -> {
                                                saveChunksDivider(0) // 0 means having chunks that fill the gaps
                                            }

                                            "Custom" -> {
                                                showSlider = true // show the dialog to choose a custom value for chunksDivider
                                            }
                                        }
                                    }, text = { Text(text = selectionOption) })
                                }
                            }
                        }
                    }
                    if (showSlider) {
                        CustomDialog( // a function for the custom dialog
                            onDismissRequest = { showSlider = false }, // disable the slider on dismiss
                            onSave = saveChunksDivider, // lambda that save chunksDivider
                            initialChunkSize = chunksDivider // initial value
                        )
                    }

                }

                }
            else if(subScreen == SubScreens.CREATION){ // when the sub screen is creation
                expanded = false
                BlockCreationScreen( // calls block creation screen that mange block creation
                    startTime = startTime,
                    endTime = endTime,
                    onSave = onSave,
                    blockSave = blockSave,
                    onBlockSave = onBlockSave,
                    onSubScreen = onSubScreen,
                    onBlockDelete = onBlockDelete,
                    blockDelete = blockDelete, // true if block is deleted
                    onDelete = onDelete, // if block going to be deleted by the user
                    formerNotification = notification, // initial value for
                    formerText = text, // initial value for text
                    formerTitle = title, // initial value for title
                    formerId = id, // initial value for id
                    formerColor = color, // initial value for color
                    savedTitles = savedTitles, // list of saved titles
                    onNewTitle = onNewTitle, // change new title dialog with a lambda
                    newTitle = newTitle, // boolean that is true when the new Title dialog is shown
                    insertTitle = insertTitle, // a function that insert a new title
                    sortIndex = sortIndex, // method of sorting
                    onSortIndex = onSortIndex, // lambda to change method of sorting
                    date = date, // date
                    navigationType = navigationType, // navigation type
                    deletableBlock = deletableBlock, // true if block can be deleted
                    onDeletableBlock = onDeletableBlock, // lambda that change deletable block
                    saveTitle = creationViewModel::saveTitle, // a function to save a title in the ui state of creation viewModel
                    saveText = creationViewModel::saveText, // a function to save a text  in the ui state of creation viewModel
                    saveColor = creationViewModel::saveColor, // a function to save a color in the ui state of creation viewModel
                    saveEndTime = creationViewModel::saveEndTime, // a function to save end time in the ui state of creation viewModel
                    saveStartTime = creationViewModel::saveStartTime // a function to save start time in the ui state of creation viewModel
                )
            }
            else if(subScreen == SubScreens.SETTINGS){ // when the sub screen is settings
                expanded = false
                SettingsPage(darkTheme = darkTheme, fontSize = fontSize,  fontType = fontType, onChange = onChange, navigationType = navigationType) // settings function that display the settings page
            }
    }
}

