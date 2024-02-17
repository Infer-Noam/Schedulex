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

package com.example.kashio.ui.TimeUi

import android.widget.Toast
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kashio.R
import com.example.kashio.data.local.database.Title
import com.example.kashio.ui.HSLA
import com.github.skydoves.colorpicker.compose.AlphaTile

//import com.example.kashio.ui.destinations.TimeSubAppDestination
//import com.example.kashio.ui.TimeUi.destinations.TimeSubAppDestination


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BlockCreationScreen(
    onSave: (title: String, text: String, tag: String, startTime: String, endTime: String, id: Int, color: String) -> Unit,
    modifier: Modifier = Modifier,
    startTime: String,
    endTime: String,
    onBlockSave: () -> Unit,
    blockSave: Boolean,
    onBlockInput: (Boolean) -> Unit,
    onBlockDelete: () -> Unit,
    blockDelete: Boolean,
    onDelete: (id: Int) -> Unit,
    formerTitle: String,
    formerTag: String,
    formerText: String,
    formerId: Int,
    formerColor: String,
    savedTitles: List<Title>,
    onNewTitle: (Boolean) -> Unit,
    newTitle: Boolean,
    insertTitle: (String, String, Int) -> Unit,
    sortIndex: HSLA,
    onSortIndex: (HSLA) -> Unit
    //onShowSnackbar: (Boolean) -> Unit
) {

    var startTimepicker by remember { mutableStateOf(startTime) }
    var endTimepicker by remember {
        mutableStateOf(
            if (endTime != "00:00") {
                endTime
            } else {
                "24:00"
            }
        )
    }

    var title by remember { mutableStateOf(formerTitle) }
    var text by remember { mutableStateOf(formerText) }
    var tag by remember { mutableStateOf(formerTag) }
    val id by remember { mutableIntStateOf(formerId) }
    var color by remember { mutableStateOf(formerColor) }


    val isStartTimeDialogOpen = remember { mutableStateOf(false) }
    val isEndTimeDialogOpen = remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isStartTimeDialogOpen.value) {
            //  date = selectedDate

            Timepicker(
                initialTime = startTimepicker,
                onSaveTime = { startTimepicker = it },
                onBlockInput = { isStartTimeDialogOpen.value = it }, isStartTime = true)
        }
        if (isEndTimeDialogOpen.value) {
            Timepicker(
                initialTime = if (endTime != "24:00") {
                    endTime
                } else {
                    "00:00"
                },
                onSaveTime = {
                    endTimepicker = if (it != "00:00") {
                        it
                    } else {
                        "24:00"
                    }
                },
                onBlockInput = { isEndTimeDialogOpen.value = it }, isStartTime = false)
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Row(modifier.fillMaxWidth(0.9f)) {

            Spacer(modifier = Modifier.weight(1f))

            OutlinedButton(onClick = {isStartTimeDialogOpen.value = true}, shape =  RoundedCornerShape(8.dp), modifier = Modifier
                .height(50.dp)
            ) {
                    Icon(painter = painterResource(
                        R.drawable.clock), contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = startTimepicker, color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
            }

            Spacer(modifier = Modifier.weight(1f))

            OutlinedButton(onClick = {isEndTimeDialogOpen.value = true}, shape =  RoundedCornerShape(8.dp), modifier = Modifier
                .height(50.dp)
            ) {
                Icon(painter = painterResource(
                    R.drawable.clock), contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
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
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    leadingIcon = { Icon(painter = painterResource(
                        R.drawable.title_input_icon), contentDescription = "title field icon")},
                    trailingIcon = { IconButton(onClick = {expanded = !expanded }, content = {Icon(painter = if(expanded){painterResource(
                        R.drawable.baseline_arrow_drop_down_24)}else{painterResource(
                        R.drawable.baseline_arrow_drop_up_24)}, contentDescription = "expand")}) },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(60.dp)
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .heightIn(max = 200.dp) // Set a max height
            ) {
                if (savedTitles.isEmpty()) {
                    DropdownMenuItem(onClick = { onNewTitle(true) }, text =  {
                        Text("No titles saved")
                    })
                } else {
                    savedTitles.forEach { option ->
                        DropdownMenuItem(onClick = {
                            title = option.title
                            color = option.color
                            expanded = false
                        }, text =  {

                            Row (modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween){
                                Text(option.title, fontSize = 16.sp, modifier = Modifier.weight(1f), maxLines = 1)

                                Spacer(modifier = Modifier.width(5.dp))

                                AlphaTile(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(RoundedCornerShape(6.dp)),
                                    selectedColor = Color(android.graphics.Color.parseColor(option.color))
                                )
                            }
                            })
                    }
                }
            }
        }
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Text") },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .defaultMinSize(minHeight = 60.dp),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done), singleLine = false,
            leadingIcon = {
                Icon(
                    painter = painterResource(
                        R.drawable.text_input_icon),
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
    }


            if(blockSave) {
                if (getTime(startTimepicker) < getTime(endTimepicker)) {
                    onBlockSave()
                    onSave(title, text, tag, startTimepicker, endTimepicker, id, color)
                    onBlockInput(false)
                }
                else{
                    Toast.makeText(LocalContext.current, "Ain't you a funny little fella", Toast.LENGTH_LONG).show()
                }
            }

            if (blockDelete){
                onBlockDelete()
                onDelete(id)
                onBlockInput(false)
            }

            if(newTitle)
            {
                NewTitleDialog(onNewTitle = onNewTitle, onSave = insertTitle, oldTitle = "", oldColor = "", oldId = -1, sortIndex = sortIndex, onSortIndex = onSortIndex)// onShowSnackbar = onShowSnackbar)
            }
        }




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Timepicker(initialTime: String, onSaveTime: (String) -> Unit, onBlockInput: (blockInput: Boolean) -> Unit, isStartTime: Boolean){

    val timePickerState = rememberTimePickerState(initialTime.substring(0,2).toInt(), initialTime.substring(3,5).toInt())

    AlertDialog(
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
                TimePicker(state = timePickerState)
            }
        }
    )
}


/* @RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    MyApplicationTheme {
        BlockCreationScreen(
            selectedDate = "6",
            onSave = { _, _, _, _, _,_ -> },
            endTime = "00-00",
            startTime = "00-00",
            navigator = {},
            id = 1,
            groupName = ""
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, widthDp = 480)
@Composable
private fun PortraitPreview() {
    MyApplicationTheme {
        BlockCreationScreen(
            selectedDate = "6",
            onSave = { _, _, _, _, _, _ -> },
            endTime = "00-00",
            startTime = "00-00"
        )
    }
} */



