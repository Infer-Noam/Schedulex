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

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.kashio.ui.theme.MyApplicationTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kashio.data.local.database.Time
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("SuspiciousIndentation")
@Composable
fun BlockCreationScreen(modifier: Modifier = Modifier, viewModel: TimeViewModel, selectedDate: String) {
    val blocks by viewModel.uiState.collectAsStateWithLifecycle()
        BlockCreationScreen(
            blocks = blocks.timeBlocks,
            onSave = viewModel::insertTimeBlock,
            modifier = modifier,
           selectedDate = selectedDate,
        )
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BlockCreationScreen(
    blocks: List<Time>,
    onSave: (time: String, title: String, text: String, tag: String) -> Unit,
    modifier: Modifier = Modifier,
    selectedDate: String
) {

    var startTime by remember { mutableStateOf("00-00") }
    var endTime by remember { mutableStateOf("00-00") }
    var date by remember { mutableStateOf(selectedDate) }
    var title by remember { mutableStateOf("lunch") }
    var text by remember { mutableStateOf(" וצהריים") }
    var tag by remember { mutableStateOf("eating") }

    val isDialogOpen = remember { mutableStateOf(false) }
    val currentTime = LocalTime.now()
    val timePickerState = rememberTimePickerState(currentTime.hour, currentTime.minute)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        if (isDialogOpen.value) {
            date = selectedDate

            AlertDialog(
                title = {
                        Text("Select Time", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary) },
                onDismissRequest = { isDialogOpen.value = false },
                confirmButton = {
                    TextButton(onClick = {
                        isDialogOpen.value = false

                       // time = "${timePickerState.minute.toString().padStart(2,'0')}-" +
                               // "${timePickerState.hour.toString().padStart(2,'0')}-" +
                             //  selectedDate
                        onSave(date, title, text, tag)
                    }) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        isDialogOpen.value = false
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
    }

    Column(modifier) {
       /* */
       Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            /*TextField(
                value = time,
                onValueChange = { time = it }
            )*/

           Button(onClick = { isDialogOpen.value = true })
           {
               Text(text = "Open Time Picker")
           }
           TextField(
                value = title,
                onValueChange = { title = it }
            )
            TextField(
                value = text,
                onValueChange = { text = it }
            )
            TextField(
                value = tag,
                onValueChange = { tag = it }
            )

            Button(modifier = Modifier.width(96.dp), onClick = { onSave(date ,title, text, tag)}) {
                Text("Save")
            }
        }
        //if...
          //  blocks.forEach {
          //          if (it.date == selectedDate) {
          //              Text("Saved item: ${it.date}")
          //          }
          // }
        }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    MyApplicationTheme {
        BlockCreationScreen(
            blocks = listOf(
                Time(date = "time1" , title = "title1", text = "text1" , tag = "tag1", startTime = "00-00", endTime = "00-00"),
                Time(date = "time2" , title = "title2", text = "text2" , tag = "tag2", startTime = "00-00", endTime = "00-00"),
                Time(date = "time3" , title = "title3", text = "text3" , tag = "tag3", startTime = "00-00", endTime = "00-00")
            ),
            selectedDate = "6",
            onSave = { _, _, _, _ -> }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, widthDp = 480)
@Composable
private fun PortraitPreview() {
    MyApplicationTheme {
        BlockCreationScreen(
            blocks = listOf(
                Time(date = "time1" , title = "title1", text = "text1" , tag = "tag1", startTime = "00-00", endTime = "00-00"),
                Time(date = "time2" , title = "title2", text = "text2" , tag = "tag2", startTime = "00-00", endTime = "00-00"),
                Time(date = "time3" , title = "title3", text = "text3" , tag = "tag3", startTime = "00-00", endTime = "00-00")
            ),
            selectedDate = "6",
            onSave = { _, _, _, _ -> }
        )
    }
}



