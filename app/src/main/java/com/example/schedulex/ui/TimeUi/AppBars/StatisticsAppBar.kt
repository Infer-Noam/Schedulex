package com.example.schedulex.ui.TimeUi.AppBars

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.schedulex.ui.TimeUi.NavigationType
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsAppBar(selectedDate: String, saveUiDate: (String) -> Unit, onLongClick: () -> Unit,  navigationType: NavigationType){

    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    val date = try {
        LocalDate.parse(selectedDate, formatter)
    } catch (e: DateTimeParseException) {
        LocalDate.now() // default value
    }

    val isDatePickerDialogOpen = remember { mutableStateOf(false) }


    if (isDatePickerDialogOpen.value) {
        `Date-picker`(initialDate = date, onDismiss = {isDatePickerDialogOpen.value = false}, onSaveDate = saveUiDate, modifier = Modifier.padding(15.dp), navigationType = navigationType)
        // It should update `selectedDate` when a new date is selected
        // and set `isDatePickerDialogOpen.value` to false when dismissed
    }
    Column {

        CenterAlignedTopAppBar(title = {
            Text(
                text = displayDate(selectedDate), style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.clickable{ isDatePickerDialogOpen.value = true }
            )

        })
        CalendarRow(selectedDate = selectedDate, saveUiDate = saveUiDate, onLongClick = onLongClick)

        Spacer(Modifier.height(10.dp))
    }
}