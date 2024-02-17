package com.example.kashio.ui.TimeUi

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.kashio.R
import com.example.kashio.data.local.database.Time
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppBar(
    selectedDate: String,
    saveUiDate: (String) -> Unit,
    chunksDivider: Int,
    saveChunksDivider: (chunksDivider: Int) -> Unit,
    blockInput: Boolean,
    onBlockInput: (Boolean) -> Unit,
    onCurrentBlock: (Boolean) -> Unit,
    onBlockSave: () -> Unit,
    onBlockDelete: () -> Unit,
    deletableBlock: Boolean,
    onDeletableBlock: () -> Unit,
    onLongClick: () -> Unit,
    longClick: Map<Time, Boolean>,
    onGroupDelete: (Int) -> Unit,
    onDateChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showSlider by remember { mutableStateOf(false) }

    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    val date = try {
        LocalDate.parse(selectedDate, formatter)
    } catch (e: DateTimeParseException) {
        LocalDate.now() // default value
    }

    val isDatePickerDialogOpen = remember { mutableStateOf(false) }


    if (isDatePickerDialogOpen.value) {
        `Date-picker`(initialDate = date, onDismiss = {isDatePickerDialogOpen.value = false}, onSaveDate = saveUiDate, modifier = Modifier.padding(15.dp), onDateChange = onDateChange)
        // It should update `selectedDate` when a new date is selected
        // and set `isDatePickerDialogOpen.value` to false when dismissed
    }
    Column {



        CenterAlignedTopAppBar(title = {
            Text(
                text = displayDate(selectedDate), style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.clickable{ isDatePickerDialogOpen.value = true
                    if(longClick.containsValue(true)){onLongClick()}}
            )
        }, navigationIcon = {
            if (blockInput){
            IconButton(onClick = { onBlockInput(false)
                onDeletableBlock()}) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "close block creation screen"
                )
        }}} ,actions = {




            Box(modifier = Modifier.padding(horizontal = 10.dp)) {

                if (blockInput){
                    Row (horizontalArrangement = Arrangement.SpaceBetween) {


                        if(deletableBlock) {
                            IconButton(onClick = {
                                onBlockDelete()
                                onDeletableBlock()
                                onDateChange(selectedDate)
                                saveUiDate(selectedDate)
                            }) {
                                Icon(
                                    Icons.Filled.Delete, contentDescription = "delete block"
                                )
                            }
                        }

                        IconButton(onClick = {
                            onBlockSave()
                            onDeletableBlock()
                            onDateChange(selectedDate)
                            saveUiDate(selectedDate)
                        }) {
                            Icon(
                                Icons.Filled.Done, contentDescription = "save block"
                            )
                        }
                    }
                }

                else {
                        Row(horizontalArrangement = Arrangement.End) {
                            if(longClick.containsValue(true)){

                                var deletableGroup = true

                                for ((key, value) in longClick) {
                                    if (value) {
                                        if (key.uid == -1){ // -1 = chunk
                                            deletableGroup = false
                                        }
                                    }
                                }

                                if(deletableGroup) { //true as long theres no blocks that arent deletable
                                    IconButton(onClick = {
                                        val deletedGroup = mutableListOf<Int>()

                                        longClick.forEach{
                                            if(it.value){
                                                deletedGroup.add(it.key.uid)
                                            }
                                        }
                                        deletedGroup.forEach{
                                            onGroupDelete(it)
                                        }
                                        onLongClick()
                                    }) {
                                        Icon(
                                            Icons.Filled.Delete, contentDescription = "delete a group of blocks"
                                        )
                                    }
                                }
                                IconButton(onClick = { onLongClick() }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "go back"
                                    )
                                }
                            }
                            else {
                                IconButton(onClick = { onCurrentBlock(true) }) {
                                    Icon(
                                        painter = painterResource(R.drawable.baseline_beenhere_24),
                                        contentDescription = "slide to the current block"
                                    )
                                }

                                IconButton(
                                    onClick = { expanded = true },
                                ) {
                                    Icon(
                                        Icons.Filled.Build,
                                        contentDescription = "change the size of the chunks"
                                    )
                                }
                            }
                        }
                }

                val options = listOf("15 Minutes", "30 Minutes", "1 Hour", "Dynamic", "Custom")
                var selectedOptionText by remember { mutableStateOf(options[0]) }

                MaterialTheme(
                    shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(16.dp))
                ) {
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        options.forEach { selectionOption ->
                            DropdownMenuItem(onClick = {
                                selectedOptionText = selectionOption
                                expanded = false
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
                                        saveChunksDivider(0)
                                    }

                                    "Custom" -> {
                                        showSlider = true
                                    }
                                }
                            }, text = { Text(text = selectionOption) })
                        }
                    }
                }
            }
            if (showSlider) {
                CustomDialog(
                    onDismissRequest = { showSlider = false },
                    onSave = saveChunksDivider,
                    initialChunkSize = chunksDivider
                )
            }

        })
        CalendarRow(selectedDate = selectedDate, saveUiDate = saveUiDate, onLongClick = onLongClick, onDateChange = onDateChange)

        Spacer(Modifier.height(10.dp))
    }
}

fun getDate(days: Int): String //0 means today, 1 tomorrow and -1 yesterday and so on...
{
    val mCalendar = Calendar.getInstance()

    return (mCalendar[Calendar.DAY_OF_MONTH] + days).toString()
        .padStart(2, '0') + "-" + ((mCalendar[Calendar.MONTH] + 1)).toString()
        .padStart(2, '0') + "-" + (mCalendar[Calendar.YEAR]).toString().padStart(4, '0')
}

@RequiresApi(Build.VERSION_CODES.O)
fun displayDate(selectedDate: String): String {
    val formattedDate = when (selectedDate) {
        getDate(0) -> "Today"
        getDate(1) -> "Tomorrow"
        getDate(-1) -> "Yesterday"
        else -> selectedDate.replace('-', '/')
    }

    return formattedDate
}




@Composable
fun CustomDialog(onDismissRequest: () -> Unit, onSave: (Int) -> Unit, initialChunkSize: Int) {
    val divisorsOf1440 = (1..1440).filter { 1440 % it == 0 }
    var sliderPosition by remember {
        (mutableFloatStateOf(
            divisorsOf1440.indexOf(
                if (initialChunkSize != 0) {
                    initialChunkSize
                } else {
                    1
                }
            ).toFloat()
        ))
    }
    
    Box(modifier = Modifier.padding(20.dp) ){

    AlertDialog(onDismissRequest = onDismissRequest, title = { Text("Select chunk size") }, text = {
        Column {
            Slider(
                value = sliderPosition,
                onValueChange = { newValue ->
                    sliderPosition = newValue.coerceIn(0f, (divisorsOf1440.size - 1).toFloat())
                },
                valueRange = 0f..(divisorsOf1440.size - 1).toFloat(),
                steps = divisorsOf1440.size - 1
            )
            val divisor = divisorsOf1440[sliderPosition.roundToInt()]

            val minutes = if (divisor % 60 == 0 && (divisor / 60 != 0)) {
                ""
            } else {
                (divisor % 60).toString() + " m"
            }
            val hours = if (divisor / 60 == 0) {
                ""
            } else {
                (divisor / 60).toString() + " h"
            }

            Text(text = "Current chunk size: $hours $minutes")
        }
    }, confirmButton = {
        TextButton(onClick = {
            onSave(divisorsOf1440[sliderPosition.roundToInt()])
            onDismissRequest()
        }) {
            Text("Save")
        }
    }, dismissButton = {
        TextButton(onClick = onDismissRequest) {
            Text("Dismiss")
        }
    })
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun `Date-picker`(initialDate: LocalDate, onSaveDate: (String) -> Unit, onDismiss: (Boolean) -> Unit, modifier: Modifier = Modifier, onDateChange: (String) -> Unit) {

    val initialDateMillis = initialDate.toEpochDay() * 24 * 60 * 60 * 1000
    val datePickerState = rememberDatePickerState(initialDateMillis)

    DatePickerDialog(
        modifier = Modifier.scale(0.875f), //.heightIn(max = LocalConfiguration.current.screenHeightDp.dp * 0.7f).widthIn(max = LocalConfiguration.current.screenWidthDp.dp * 0.7f),
        onDismissRequest = { onDismiss(false) },
        confirmButton = {
            TextButton(onClick = {
                val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val selectedDateMillis = datePickerState.selectedDateMillis ?: 0L
                val selectedDate = Date(selectedDateMillis)
                val formattedDate = sdf.format(selectedDate)
                onSaveDate(formattedDate)
                onDateChange(formattedDate)
                onDismiss(false)
            }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss(false) }) {
                Text("Dismiss")
            }
        },
        content = {
            Column (modifier = Modifier) {
              //  Text("Select Date", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface, modifier = modifier)
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    DatePicker(state = datePickerState)
                }
            }
        }
    )
}



@Preview
@Composable
fun CalenderPrev(){
    `Date-picker`(LocalDate.now(), {}, {}, Modifier,{})
}


