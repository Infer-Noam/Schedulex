package com.example.kashio.ui.TimeUi

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun CalendarRow(selectedDate: String, onSelectDate: (String) -> Unit, saveUiDate: (date: String) -> Unit) {
    val calendar = Calendar.getInstance()
    val startDate = calendar.apply {
        add(Calendar.DAY_OF_MONTH, -60)
    }.time
    val endDate = calendar.apply {
        add(Calendar.DAY_OF_MONTH, 120) // Add 30 because we subtracted 15 earlier
    }.time

    val dates = generateSequence(startDate) { date ->
        Calendar.getInstance().apply {
            time = date
            add(Calendar.DAY_OF_MONTH, 1)
        }.time
    }.takeWhile { it.before(endDate) }
        .map { date ->
            val day = SimpleDateFormat("d", Locale.getDefault()).format(date)
            val dayOfWeek = SimpleDateFormat("E", Locale.getDefault()).format(date)
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(date)
            Triple(day, dayOfWeek, dateFormat)
        }
        .toList()

    val currentDateIndex = dates.indexOfFirst {
        it.third == selectedDate
    }

    LazyRow(
        state = rememberLazyListState(initialFirstVisibleItemIndex = currentDateIndex - 2)
    ) {
        items(dates) { (day, dayOfWeek, dateFormat) ->
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .size(55.dp)  // Set a fixed size for the Card
                    .clickable { onSelectDate(dateFormat)
                               saveUiDate(dateFormat)},
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                ),
                // backgroundColor = if (selectedDay.value == day) MaterialTheme.colors.primary else MaterialTheme.colors.surface
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                        .background(if (selectedDate == dateFormat) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.primaryContainer),

                    ) {
                    Text(
                        text = dayOfWeek,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = day.toString(),
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        }
    }
}

