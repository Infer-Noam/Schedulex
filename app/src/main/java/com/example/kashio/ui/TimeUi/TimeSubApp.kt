package com.example.kashio.ui.TimeUi

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
    fun TimeSubApp() {
        val mCalendar = Calendar.getInstance()
        var selectedDay by remember { mutableStateOf<String>(
            ( mCalendar[Calendar.DAY_OF_MONTH].toString().padStart(2, '0')) + "-" +
                    ((mCalendar[Calendar.MONTH] + 1)).toString().padStart(2, '0') + "-" +
                    (mCalendar[Calendar.YEAR]).toString().padStart(4, '0'))}


        Scaffold(
            topBar = {
                    AppBar(selectedDay = selectedDay, onSelectDay = {selectedDay = it})
                },
            content = { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    TimeScreen(modifier = Modifier.padding(16.dp), selectedDay = selectedDay)
                }
            }
        )
    }

fun getDate(days: Int): String //0 means today, 1 tomorrow and -1 yesterday and so on...
{
    val mCalendar = Calendar.getInstance()

    return (mCalendar[Calendar.DAY_OF_MONTH] + days).toString().padStart(2, '0') + "-" +
    ((mCalendar[Calendar.MONTH] + 1)).toString().padStart(2, '0') + "-" +
            (mCalendar[Calendar.YEAR]).toString().padStart(4, '0')
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DisplayDate(selectedDay: String) {
    val formattedDate = when (selectedDay) {
       getDate(0) -> "Today"
        getDate(1) -> "Tomorrow"
        getDate(-1) -> "Yesterday"
        else ->  selectedDay
    }

    Text(text = formattedDate, style = MaterialTheme.typography.bodyMedium)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppBar(selectedDay: String, onSelectDay: (String) -> Unit){
    Column {
        DisplayDate(selectedDay)

        Spacer(Modifier.height(20.dp))

        CalendarRow(selectedDay = selectedDay, onSelectDay = onSelectDay,)

    }
}