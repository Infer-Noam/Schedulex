package com.example.kashio.ui.TimeUi

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
    fun TimeSubApp( viewModel: TimeViewModel = hiltViewModel()) {
        val mCalendar = Calendar.getInstance()
        var selectedDate by remember { mutableStateOf<String>(
            ( mCalendar[Calendar.DAY_OF_MONTH].toString().padStart(2, '0')) + "-" +
                    ((mCalendar[Calendar.MONTH] + 1)).toString().padStart(2, '0') + "-" +
                    (mCalendar[Calendar.YEAR]).toString().padStart(4, '0'))}
    var blockInput by remember { mutableStateOf(false) }


        Scaffold(
            topBar = {
                    AppBar(selectedDate = selectedDate, onSelectDate = {selectedDate = it}, saveUiDate = viewModel::setDate)
                },
            content = { paddingValues ->
                Box(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.padding(paddingValues), contentAlignment = Alignment.BottomCenter) {
                        // BlockCreationScreen(modifier = Modifier.padding(16.dp), selectedDate = selectedDate, viewModel = viewModel)

                        BlockList(viewModel = viewModel, selectedDate = selectedDate, blockInput = blockInput, onBlockInput = {blockInput = it })
                    }

                    FloatingActionButton(
                        onClick = { blockInput = true },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 10.dp),
                     //   backgroundColor = MaterialTheme.colors.primary
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Floating action button")
                    }
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
fun DisplayDate(selectedDate: String) {
    val formattedDate = when (selectedDate) {
       getDate(0) -> "Today"
        getDate(1) -> "Tomorrow"
        getDate(-1) -> "Yesterday"
        else ->  selectedDate
    }

    Text(text = formattedDate, style = MaterialTheme.typography.bodyMedium)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppBar(selectedDate: String, onSelectDate: (String) -> Unit, saveUiDate: (date: String) -> Unit){
    Column {
        DisplayDate(selectedDate)

        Spacer(Modifier.height(20.dp))

        CalendarRow(selectedDate = selectedDate, onSelectDate = onSelectDate, saveUiDate = saveUiDate)

        Spacer(Modifier.height(5.dp))

    }
}