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
import android.app.TimePickerDialog
import android.widget.TimePicker
import android.widget.Toast
import com.example.kashio.ui.theme.MyApplicationTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kashio.data.local.database.Time
import com.example.kashio.ui.ItemUi.DataItemTypeUiState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@SuppressLint("SuspiciousIndentation")
@Composable
fun TimeScreen(modifier: Modifier = Modifier, viewModel: TimeViewModel = hiltViewModel(), selectedDay: String?) {
    val blocks by viewModel.uiState.collectAsStateWithLifecycle()
        TimeScreen(
            blocks = blocks.timeBlocks,
            onSave = viewModel::insertTimeBlock,
            modifier = modifier,
            selectedDay = selectedDay
        )
}

@Composable
internal fun TimeScreen(
    blocks: List<Time>,
    onSave: (time: String, title: String, text: String, tag: String) -> Unit,
    modifier: Modifier = Modifier,
    selectedDay: String?
) {
    var time by remember { mutableStateOf("1250-06-01-2024") }
    var title by remember { mutableStateOf("lunch") }
    var text by remember { mutableStateOf("ויטמינים וצהריים") }
    var tag by remember { mutableStateOf("eating") }

    val mContext = LocalContext.current

    // Declaring and initializing a calendar
    val mCalendar = Calendar.getInstance()
    val mYear = mCalendar[Calendar.YEAR]
    val mMonth = mCalendar[Calendar.MONTH]  + 1
    var mDay = mCalendar[Calendar.DAY_OF_MONTH]
    val mHour = mCalendar[Calendar.HOUR_OF_DAY]
    val mMinute = mCalendar[Calendar.MINUTE]

    // Value for storing time as a string
    //val mTime = remember { mutableStateOf("") }

    // Creating a TimePicker dialog
        val mTimePickerDialog = TimePickerDialog(
            mContext,
            {_, mHour : Int, mMinute: Int ->
                time = "$mMinute-$mHour-$mDay-$mMonth-$mYear" //mm-hh-dd-mm-yy
            }, mHour, mMinute, true
        )

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

           Button(onClick = { mTimePickerDialog.show()}) {
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

            Button(modifier = Modifier.width(96.dp), onClick = { onSave(time ,title, text, tag)}) {
                Text("Save")
            }
        }
        blocks.forEach {
            if (it.time.substring(6) == "$selectedDay-$mMonth-$mYear") {
                Text("Saved item: ${it.time}")
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    MyApplicationTheme {
        TimeScreen(
            blocks = listOf(
                Time(time = "time1" , title = "title1", text = "text1" , tag = "tag1"),
                Time(time = "time2" , title = "title2", text = "text2" , tag = "tag2"),
                Time(time = "time3" , title = "title3", text = "text3" , tag = "tag3")
            ),
            selectedDay = "6",
            onSave = { _, _, _, _ -> }
        )
    }
}

@Preview(showBackground = true, widthDp = 480)
@Composable
private fun PortraitPreview() {
    MyApplicationTheme {
        TimeScreen(
            blocks = listOf(
                Time(time = "time1", title = "title1", text = "text1", tag = "tag1"),
                Time(time = "time2", title = "title2", text = "text2", tag = "tag2"),
                Time(time = "time3", title = "title3", text = "text3", tag = "tag3")
            ),
            selectedDay = "6",
            onSave = { _, _, _, _ -> }
        )
    }
}



