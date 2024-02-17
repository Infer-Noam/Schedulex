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

package com.example.schedulex.ui.TimeUi.ScheduleScreen.CreationSubScreen

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.schedulex.data.local.database.Title
import com.example.schedulex.ui.TimeUi.HSLA
import com.example.schedulex.ui.TimeUi.NavigationType
import com.example.schedulex.ui.TimeUi.SavedTitlesScreen.NewTitleDialog
import com.example.schedulex.ui.TimeUi.ScheduleScreen.SubScreens
import com.example.schedulex.ui.TimeUi.adjustDateTime
import com.example.schedulex.ui.TimeUi.calculateDifference
import com.example.schedulex.ui.TimeUi.dateTimeToLocalDateTime
import com.example.schedulex.ui.TimeUi.getTime
import com.example.schedulex.R
import com.github.skydoves.colorpicker.compose.AlphaTile
import java.time.LocalDateTime
import kotlin.math.absoluteValue
import kotlin.reflect.KFunction2
import kotlin.reflect.KFunction3
import kotlin.reflect.KFunction8

enum class NotificationPlacement{ // placement of the notification in relation to the start time
    NONE,
    BEFORE, // minutes before the start time
    AT, // at start time
    AFTER // minutes after start time
}


@SuppressLint("SourceLockedOrientationActivity")
@OptIn(ExperimentalAnimationApi::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
internal fun BlockCreationScreen( // a function for block creation screen, in this screen the user can create, edit and delete time blocks
    onSave: KFunction8<String, String, String, String, String, Int, String, Context, Unit>, //  a function to save a time block
    modifier: Modifier = Modifier, // a modifier
    startTime: String, // start time initial value
    endTime: String, // end time initial value
    onBlockSave: () -> Unit, // lambda that change block save
    blockSave: Boolean, // block save
    onSubScreen: (SubScreens) -> Unit, // a lambda that change sub screen
    onBlockDelete: () -> Unit, // a lambda that change block delete
    blockDelete: Boolean, // block delete
    onDelete: KFunction2<Int, Context, Unit>, // a lambda that delete a time block
    formerTitle: String, // initial title value
    formerNotification: String, // initial notification value
    formerText: String, // initial text value
    formerId: Int, // initial id value
    formerColor: String, // initial color value
    savedTitles: List<Title>, // a list of saved titles
    onNewTitle: (Boolean) -> Unit, // a lambda to to change newTitle
    newTitle: Boolean, // a boolean that represent thee new title dialog
    insertTitle: KFunction3<String, String, Int, Unit>, // a function to insert a new title
    sortIndex: HSLA, // method of sorting the colors in new title dialog
    onSortIndex: (HSLA) -> Unit, // change the method of sorting colors
    date: String, // date
    navigationType: NavigationType, // the navigation type
    deletableBlock: Boolean, // boolean that is true if the block is deletable
    onDeletableBlock: (Boolean) -> Unit, // change deletableBlock
    saveTitle: (String) -> Unit, // a lambda to save the title in the creation viewModel
    saveText: (String) -> Unit, // a lambda to save the text in the creation viewModel
    saveStartTime: (String) -> Unit, // a lambda to save the start time in the creation viewModel
    saveEndTime: (String) -> Unit, // a lambda to save the end time in the creation viewModel
    saveColor: (String) -> Unit // a lambda to save the color in the creation viewModel
) {

    var startTimepicker by remember { mutableStateOf(startTime) } // initial value of the start time picker for the start time picker dialog
    var endTimepicker by remember { // initial value of the end time picker for the end time picker dialog
        mutableStateOf(
            if (endTime != "00:00") {
                endTime
            } else {
                "24:00"
            }
        )
    }



    var title by remember { mutableStateOf(formerTitle) } // initial title value
    var text by remember{ mutableStateOf(formerText) } // initial text value
    var notification by remember { mutableStateOf(formerNotification) } // initial notification value
    val id by remember { mutableIntStateOf(formerId) } // initial id value
    var color by remember { mutableStateOf(formerColor) } // initial color value
    var hasNotification by remember { mutableStateOf(notification != "") } //  true if the block has notification, otherwise false
    var sliderPosition by remember { mutableLongStateOf(1L) } // initial value of the slider
    var count by remember { mutableLongStateOf(sliderPosition.absoluteValue) } // initial value that will be added to the start date of the block to create a notification
    var sliderEnabled by remember { mutableStateOf( // true if the slider is enabled
        when
        {
            notification.isEmpty() -> NotificationPlacement.NONE
            calculateDifference(date1 = startTimepicker.substring(0,2) + "-" + startTimepicker.substring(3,5) + "-" + date, date2 = notification) < 0 -> NotificationPlacement.BEFORE
            calculateDifference(date1 = startTimepicker.substring(0,2) + "-" + startTimepicker.substring(3,5) + "-" + date, date2 = notification).toInt() == 0 -> NotificationPlacement.AT
            else -> NotificationPlacement.AFTER
        }) }

    if(hasNotification){
        sliderPosition = calculateDifference(date1 = startTimepicker.substring(0,2) + "-" + startTimepicker.substring(3,5) + "-" + date, date2 = notification)
        hasNotification = false // making sure it dosen't reset the value after changing it
        count = sliderPosition.absoluteValue
    }

    val isStartTimeDialogOpen = remember { mutableStateOf(false) } // true if startTime dialog is open or not, otherwise false
    val isEndTimeDialogOpen = remember { mutableStateOf(false) } // true if endTime dialog is open or not, otherwise false

    Column(
        modifier = modifier
            .fillMaxSize(), // fills max size
        horizontalAlignment = Alignment.CenterHorizontally // arrange the child elements from center
    ) {
        if (isStartTimeDialogOpen.value) { // shows the start time picker dialog
            Timepicker( // function for the time picker
                initialTime = startTimepicker, // initial value
                onSaveTime = { startTimepicker = it ; saveStartTime(startTimepicker)}, // saves the value to the viewModel and startTime picker
                onBlockInput = { isStartTimeDialogOpen.value = it }, isStartTime = true, navigationType)
        }
        if (isEndTimeDialogOpen.value) { // shows the end time picker dialog
            Timepicker( // function for the time picker
                initialTime = if (endTime != "24:00") { // initial value
                    endTime
                } else {
                    "00:00"
                },
                onSaveTime = { // saves the value to the viewModel and endTime picker
                    endTimepicker = if (it != "00:00") {
                        it
                    } else {
                        "24:00"
                    }
                    saveEndTime(endTimepicker)
                },
                onBlockInput = { isEndTimeDialogOpen.value = it }, isStartTime = false, navigationType)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        if(navigationType == NavigationType.BOTTOM_NAVIGATION) { // when the navigation type is bottom navigation

            Row(modifier.fillMaxWidth(0.9f)) { // fills 9/10 of the available space

                Spacer(modifier = Modifier.weight(1f)) // fills 1f of remaining space

                OutlinedButton(
                    onClick = { isStartTimeDialogOpen.value = true }, // button to open start time picker dialog
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .height(50.dp)
                ) {
                    Icon(
                        painter = painterResource(
                            R.drawable.clock
                        ),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = startTimepicker, color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                OutlinedButton(
                    onClick = { isEndTimeDialogOpen.value = true },  // button to open end time picker dialog
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .height(50.dp)
                ) {
                    Icon(
                        painter = painterResource(
                            R.drawable.clock
                        ),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = if (endTimepicker != "24:00") {
                            endTimepicker
                        } else {
                            "00:00"
                        },
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
            }

            var expanded by remember { mutableStateOf(false) }

            Box {
                Box {
                    OutlinedTextField( // title text field
                        value = title,
                        onValueChange = { title = it ;  saveTitle(title)  // saves the new value of title to the creation viewModel and to title
                        },
                        label = { Text("Title") },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        leadingIcon = {
                            Icon(
                                painter = painterResource(
                                    R.drawable.title_input_icon
                                ), contentDescription = "title field icon"
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { expanded = !expanded }, content = {
                                Icon(
                                    painter = if (expanded) {
                                        painterResource(
                                            R.drawable.baseline_arrow_drop_down_24
                                        )
                                    } else {
                                        painterResource(
                                            R.drawable.baseline_arrow_drop_up_24
                                        )
                                    }, contentDescription = "expand"
                                )
                            })
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(60.dp)
                    )
                }
                DropdownMenu( // drop down menu for the saved titles so the user can pick out of the saved titles
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .heightIn(max = 200.dp) // Set a max height
                ) {
                    if (savedTitles.isEmpty()) { // when saved titles list is empty
                        DropdownMenuItem(onClick = { onNewTitle(true) }, text = {
                            Text("No titles saved")
                        })
                    } else {
                        savedTitles.forEach { option -> // column of all the titles
                            DropdownMenuItem(onClick = {
                                title = option.title; saveTitle(title) // when picked the title is saved to title and to the creation viewModel
                                color = option.color; saveColor(color) // when picked the color is saved to color and to the creation viewModel
                                expanded = false
                            }, text = { // a row that display each title in saved titles column

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        option.title,
                                        fontSize = 16.sp,
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1
                                    )

                                    Spacer(modifier = Modifier.width(5.dp))

                                    AlphaTile(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(RoundedCornerShape(6.dp)),
                                        selectedColor = Color(
                                            android.graphics.Color.parseColor(
                                                option.color
                                            )
                                        )
                                    )
                                }
                            })
                        }
                    }
                }
            }
        }
        else if(navigationType == NavigationType.NAVIGATION_RAIL || navigationType == NavigationType.PERMANENT_NAVIGATION_DRAWER){ // when the navigation type is navigation rail or permanent navigation drawer

            var expanded by remember { mutableStateOf(false) } // true if the saved titles drop down dialog is open, otherwise false

            Box {
                Box {
                    Row(modifier = Modifier.fillMaxWidth(0.9f)) { // fills 9/10 of the available width
                        OutlinedTextField( // text filled for titles
                            value = title,
                            onValueChange = { title = it ; saveTitle(title) },
                            label = { Text("Title") },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            singleLine = true, // stay in a single line
                            shape = RoundedCornerShape(8.dp), // round corner shape
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(
                                        R.drawable.title_input_icon
                                    ), contentDescription = "title field icon"
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { expanded = !expanded }, // change the value of expanded, shows or hide the drop down menu
                                    content = {
                                    Icon(
                                        painter = if (expanded) {
                                            painterResource(
                                                R.drawable.baseline_arrow_drop_down_24
                                            )
                                        } else {
                                            painterResource(
                                                R.drawable.baseline_arrow_drop_up_24
                                            )
                                        }, contentDescription = "expand"
                                    )
                                })
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.5f) // fills half the available width
                                .height(60.dp) // height of 60.dp
                        )

                        Spacer(modifier = Modifier.weight(1f)) // fills 1f of the remaining space

                        Column {
                            Spacer(modifier = Modifier.height(6.dp)) // empty space at 6.dp of height
                            OutlinedButton(
                                onClick = { isStartTimeDialogOpen.value = true }, // opens the start time dialog
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .height(55.dp)
                            ) {
                                Icon(
                                    painter = painterResource(
                                        R.drawable.clock
                                    ),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = startTimepicker,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(0.6f)) // fills 6/10 of the remaining space

                        Column {
                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedButton(
                                onClick = { isEndTimeDialogOpen.value = true }, // opens the end time dialog
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .height(55.dp)
                            ) {
                                Icon(
                                    painter = painterResource(
                                        R.drawable.clock
                                    ),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.width(5.dp))

                                Text(
                                    text = if (endTimepicker != "24:00") {
                                        endTimepicker
                                    } else {
                                        "00:00"
                                    },
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
                DropdownMenu(
                    expanded = expanded, // expanded if true, otherwise false
                    onDismissRequest = { expanded = false }, // close the dialog on dismiss
                    modifier = Modifier
                        .fillMaxWidth(0.5f * 0.7f) // fills 0.5f * 0.7f of the available space
                        .heightIn(max = 200.dp) // Set a max height
                ) {
                    if (savedTitles.isEmpty()) { // when the saved titles list is empty
                        DropdownMenuItem(onClick = { onNewTitle(true) }, // opens the new title dialog
                            text = {
                            Text("No titles saved")
                        })
                    } else {
                        savedTitles.forEach { option ->
                            DropdownMenuItem(onClick = { // column of titles
                                title = option.title; saveTitle(title) // when picked the title is saved to title and to the creation viewModel
                                color = option.color; saveColor(color) // when picked the color is saved to color and to the creation viewModel
                                expanded = false // when a title is picked the drop down menu closes
                            }, text = {

                                Row( // a row that display each title
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        option.title,
                                        fontSize = 16.sp,
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1
                                    )

                                    Spacer(modifier = Modifier.width(5.dp))

                                    AlphaTile(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(RoundedCornerShape(6.dp)),
                                        selectedColor = Color(
                                            android.graphics.Color.parseColor(
                                                option.color
                                            )
                                        )
                                    )
                                }
                            })
                        }
                    }
                }
            }
        }
        OutlinedTextField( // text field for the text
            value = text,
            onValueChange = { text = it; saveText(text)},
            label = { Text("Text") },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .defaultMinSize(minHeight = 60.dp),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done), singleLine = false,
            leadingIcon = {
                Icon(
                    painter = painterResource(
                        R.drawable.text_input_icon
                    ),
                    contentDescription = "text field icon"
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = { text = "" },
                    content = {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "clear text field"
                        )
                    })
            },
        )
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth(0.9f)) { // column for the notification
                val hours = (sliderPosition / 60).toInt()
                val minutes = (sliderPosition % 60).toInt()

                val hoursText = when(hours){0 -> ""; 1,-1 -> "1 hour"; else -> "${hours.absoluteValue} hours"}
                val minutesText = when(minutes){0 -> ""; 1,-1 -> "1 minute"; else -> "${minutes.absoluteValue} minutes"}
                val combinedTime = if(hoursText == "" && minutesText != ""){minutesText} else if(hoursText != "" && minutesText == ""){hoursText}
                else if(hoursText != ""){"$hoursText and $minutesText" } else{""}
                val sliderText = if(hours * 60 + minutes > 0) { "$combinedTime after start" } else if(hours * 60 + minutes < 0) { "$combinedTime before start" } else { "At start"}

                val context = LocalContext.current

                SegmentedButtons {
                    SegmentedButtonItem(
                        selected = sliderEnabled == NotificationPlacement.NONE,
                        onClick = { sliderEnabled = NotificationPlacement.NONE; sliderPosition = 0 },
                        label = { Text(text = "None") },
                    )
                    SegmentedButtonItem(
                        selected = sliderEnabled == NotificationPlacement.BEFORE,
                        onClick = { sliderEnabled = NotificationPlacement.BEFORE ; sliderPosition = -count },
                        label = { Text(text = "Before") },
                    )
                    SegmentedButtonItem(
                        selected = sliderEnabled == NotificationPlacement.AT,
                        onClick = { sliderEnabled = NotificationPlacement.AT ; sliderPosition = 0},
                        label = { Text(text = "At") },
                    )
                    SegmentedButtonItem(
                        selected = sliderEnabled == NotificationPlacement.AFTER,
                        onClick = { sliderEnabled = NotificationPlacement.AFTER; sliderPosition = count},
                        label = { Text(text = "After") },
                    )
                }


                if(sliderEnabled != NotificationPlacement.NONE) {
                    Text(text = sliderText, modifier = Modifier.padding(10.dp))
                }
                else{
                    Text(text = "No notification set", modifier = Modifier.padding(10.dp))
                }



                    Row( // has a buttons for: saving, exiting and deleting
                       modifier = Modifier.height(65.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if(navigationType == NavigationType.NAVIGATION_RAIL || navigationType == NavigationType.PERMANENT_NAVIGATION_DRAWER) {
                            if(deletableBlock){

                                DiagonalSplitButton(onLeftClick = {onSubScreen(SubScreens.MAIN)}, onRightClick = {onSubScreen(
                                    SubScreens.MAIN) ;   onDelete(id, context); onDeletableBlock(false) }, width = LocalConfiguration.current.screenWidthDp.dp * 0.15f)
                            } else {
                                Button(
                                    modifier = Modifier.size(width = LocalConfiguration.current.screenWidthDp.dp * 0.15f, height = LocalConfiguration.current.screenWidthDp.dp * 0.15f / 2.5f),
                                    onClick = { onSubScreen(SubScreens.MAIN) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonColors(containerColor =  MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                        disabledContainerColor = ButtonDefaults.buttonColors().disabledContainerColor, disabledContentColor =  ButtonDefaults.buttonColors().disabledContentColor)
                                ) {
                                    Spacer(modifier = Modifier.weight(2f))

                                    Icon(Icons.Rounded.Close, contentDescription = "exit screen")

                                    Spacer(modifier = Modifier.weight(4f))

                                    Text(text = "Exit")

                                    Spacer(modifier = Modifier.weight(2f))
                                }
                            }
                            Spacer(modifier = Modifier.weight(1f))
                        }

                        if(sliderEnabled != NotificationPlacement.NONE && sliderEnabled != NotificationPlacement.AT) {
                            Button(colors = ButtonColors(containerColor =  MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                disabledContainerColor = ButtonDefaults.buttonColors().disabledContainerColor, disabledContentColor =  ButtonDefaults.buttonColors().disabledContentColor)
                                 ,onClick = { if(count > 1){count--; sliderPosition = when (sliderEnabled) {
                                NotificationPlacement.BEFORE -> -count
                                NotificationPlacement.AFTER -> count
                                NotificationPlacement.AT -> 0
                                NotificationPlacement.NONE -> 0
                            }} }) { Text(text = "-") }

                            Row(
                                modifier = Modifier
                                    .animateContentSize()
                                    .padding(horizontal = 22.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                count.toString()
                                    .mapIndexed { index, c -> Digit(c, count.toInt(), index) }
                                    .forEach { digit ->
                                        AnimatedContent(
                                            targetState = digit,
                                            transitionSpec = {
                                                if (targetState > initialState) {
                                                    slideInVertically { -it } togetherWith slideOutVertically { it }
                                                } else {
                                                    slideInVertically { it } togetherWith slideOutVertically { -it }
                                                }
                                            }, label = ""
                                        ) { digit2 ->
                                            Text(
                                                "${digit2.digitChar}",
                                                style = MaterialTheme.typography.displayLarge,
                                                textAlign = TextAlign.Center,
                                            )
                                        }
                                    }
                            }

                            Button(colors = ButtonColors(containerColor =  MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                disabledContainerColor = ButtonDefaults.buttonColors().disabledContainerColor, disabledContentColor =  ButtonDefaults.buttonColors().disabledContentColor)
                                 ,onClick = {if(count < 999) {count++; sliderPosition = when (sliderEnabled) {
                                NotificationPlacement.BEFORE -> -count
                                NotificationPlacement.AFTER -> count
                                NotificationPlacement.AT -> 0
                                NotificationPlacement.NONE -> 0
                            }}
                            }) { Text(text = "+") }
                    }
                        if(navigationType == NavigationType.NAVIGATION_RAIL || navigationType == NavigationType.PERMANENT_NAVIGATION_DRAWER) {
                            Spacer(modifier = Modifier.weight(1f))

                                Button(modifier = Modifier.size(width = LocalConfiguration.current.screenWidthDp.dp * 0.15f, height = LocalConfiguration.current.screenWidthDp.dp * 0.15f / 2.5f), onClick = { if(sliderEnabled != NotificationPlacement.NONE) {
                                    val currentTime = LocalDateTime.now()
                                    notification =
                                        startTimepicker.substring(0, 2) + "-" + startTimepicker.substring(
                                            3,
                                            5
                                        ) + "-" + date
                                    notification =
                                        adjustDateTime(dateTime = notification, minutes = sliderPosition.toInt())
                                    val alarmTime = dateTimeToLocalDateTime(notification)

                                    if (!(alarmTime.isAfter(currentTime))) {
                                        println("The alarm time has already passed.")
                                        notification = "" // no notification
                                    }
                                }
                                else{
                                    notification = "" // no notification
                                }

                                    if (getTime(startTimepicker) < getTime(endTimepicker)) {
                                        onBlockSave()
                                        onSave(title, text, notification, startTimepicker, endTimepicker, id, color, context)
                                        onSubScreen(SubScreens.MAIN)
                                    }
                                    else{
                                        Toast.makeText(context, "End time should be after start time", Toast.LENGTH_LONG).show()
                                    }
                                    onDeletableBlock(false) }, shape = RoundedCornerShape(10.dp),
                                    colors = ButtonColors(containerColor =  MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                        disabledContainerColor = ButtonDefaults.buttonColors().disabledContainerColor, disabledContentColor =  ButtonDefaults.buttonColors().disabledContentColor)
                                ) {
                                    Spacer(modifier = Modifier.weight(2f))

                                    Icon(Icons.Rounded.Check, contentDescription = "save block")

                                    Spacer(modifier = Modifier.weight(4f))

                                    Text(text = "Save")

                                    Spacer(modifier = Modifier.weight(2f))
                            }
                        }
                }

            }
    }


            if(blockSave) { // if block is saved
                if(sliderEnabled != NotificationPlacement.NONE) { // saves notification
                    val currentTime = LocalDateTime.now()
                    notification =
                        startTimepicker.substring(0, 2) + "-" + startTimepicker.substring(
                            3,
                            5
                        ) + "-" + date
                    notification =
                        adjustDateTime(dateTime = notification, minutes = sliderPosition.toInt())
                    val alarmTime = dateTimeToLocalDateTime(notification)

                    if (!(alarmTime.isAfter(currentTime))) {
                        println("The alarm time has already passed.")
                        notification = ""
                    }
                }
                else{
                    notification = "" // no notification
                }

                if (getTime(startTimepicker) < getTime(endTimepicker)) { // if valid it saved the time block
                    onBlockSave()
                    onSave(title, text, notification, startTimepicker, endTimepicker, id, color, LocalContext.current)
                    onSubScreen(SubScreens.MAIN)
                }
                else{
                    Toast.makeText(LocalContext.current, "End time should be after start time", Toast.LENGTH_LONG).show() // shows a toast messeage for the user
                }
            }

            if (blockDelete){ // when deleting a time block

                notification = ""
                onBlockDelete()
                onDelete(id, LocalContext.current)
                onSubScreen(SubScreens.MAIN)
            }

            if(newTitle) // when the new title dialog is activated
            {
                NewTitleDialog(onNewTitle = onNewTitle, onSave = insertTitle, oldTitle = "", oldColor = "", oldId = -1, sortIndex = sortIndex, onSortIndex = onSortIndex)// onShowSnackbar = onShowSnackbar)
            }
        }




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Timepicker(initialTime: String, onSaveTime: (String) -> Unit, onBlockInput: (blockInput: Boolean) -> Unit, isStartTime: Boolean, navigationType: NavigationType){

    val timePickerState = rememberTimePickerState(initialTime.substring(0,2).toInt(), initialTime.substring(3,5).toInt())

    var picker by remember { mutableStateOf(true) }

    AlertDialog(icon = {   if (navigationType == NavigationType.BOTTOM_NAVIGATION) {
        IconButton(onClick = { picker = !picker }) {
            if (picker) {
                Icon(
                    painterResource(R.drawable.rounded_keyboard_24),
                    contentDescription = "time input"
                )
            } else {
                Icon(
                    painterResource(R.drawable.clock),
                    contentDescription = "time picker"
                )
            }
        }
    }},
        title = {
            Text(if(isStartTime){"Select start time"}else{"Select end time"}, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary) },

        onDismissRequest = {
            onBlockInput(false)},

        confirmButton = {
            TextButton(onClick = {
                onBlockInput(false)

              onSaveTime("${timePickerState.hour.toString().padStart(2,'0')}:${timePickerState.minute.toString().padStart(2,'0')}")
            }) {
                Text("Confirm")
            }
        },
        dismissButton = {

            TextButton(onClick = {
                onBlockInput(false)
            }) {
                Text("Dismiss")

        }

                        } ,
        text = {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                if(navigationType == NavigationType.BOTTOM_NAVIGATION) {
                    if (picker) {
                        TimePicker(
                            state = timePickerState,
                            layoutType = TimePickerLayoutType.Vertical
                        )
                    }
                    else{
                        TimeInput(state = timePickerState)
                    }
                }
                else{
                    TimeInput(state = timePickerState)
                }
            }
        }
    )
}

@Suppress("EqualsOrHashCode")
data class Digit(val digitChar: Char, val fullNumber: Int, val place: Int) {
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Digit -> digitChar == other.digitChar
            else -> super.equals(other)
        }
    }
}

operator fun Digit.compareTo(other: Digit): Int {
    return fullNumber.compareTo(other.fullNumber)
}


@Composable
fun DiagonalSplitButton(
    onLeftClick: () -> Unit = {},
    onRightClick: () -> Unit = {},
    color1: Color = MaterialTheme.colorScheme.primaryContainer,
    color2: Color = MaterialTheme.colorScheme.errorContainer,
    width: Dp
) {
    val shape = RoundedCornerShape(10.dp)

    Box(modifier = Modifier
        .size(width = width, height = (width.value / 2.5).dp)
        .clip(shape)) {
        Canvas(modifier = Modifier
            .matchParentSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    if (offset.x < offset.y) {
                        onLeftClick()
                    } else {
                        onRightClick()
                    }
                }
            }) {
            drawPath(
                color = color1,
                path = Path().apply {
                    moveTo(size.width, 0f)
                    lineTo(0f, 0f)
                    lineTo(0f, size.height)
                    close()
                }
            )
            drawPath(
                color = color2 ,
                path = Path().apply {
                    moveTo(size.width, 0f)
                    lineTo(0f, size.height)
                    lineTo(size.width, size.height)
                    close()
                }
            )
        }
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape),
            contentAlignment = Alignment.Center
        ) {
            Row {

                IconButton(onClick = { onLeftClick() }) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Exit",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Row {
                    IconButton(onClick = { onRightClick() }) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "delete",
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }

            }
        }
    }
}
