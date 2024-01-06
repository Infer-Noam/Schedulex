package com.example.kashio.ui.TimeUi

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Calendar

@Composable
    fun TimeSubApp() {
        val mCalendar = Calendar.getInstance()
        var selectedDay by remember { mutableStateOf<String>(mCalendar[Calendar.DAY_OF_MONTH].toString()) }


        Scaffold(
            topBar = {
                CalendarRow(selectedDay = selectedDay, onSelectDay = { selectedDay = it },)
                     },
            content = { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    TimeScreen(modifier = Modifier.padding(16.dp), selectedDay = selectedDay)
                }
            }
        )
    }
