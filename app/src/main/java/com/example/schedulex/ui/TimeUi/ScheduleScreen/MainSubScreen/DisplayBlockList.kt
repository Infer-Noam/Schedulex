package com.example.schedulex.ui.TimeUi.ScheduleScreen.MainSubScreen

//import com.example.kashio.ui.TimeUi.destinations.BlockCreationScreenDestination

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.schedulex.data.local.database.Time
import com.example.schedulex.ui.TimeUi.ScheduleScreen.SubScreens
import com.example.schedulex.ui.TimeUi.getLength
import com.example.schedulex.ui.TimeUi.getTime
import com.example.schedulex.R
import kotlinx.coroutines.launch
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun DisplayBlockList( // a function for displaying a list of blocks in a column like manner
    timeBlocks: List<Time>, // a list of time blocks
    onBlockInput: (subScreen: SubScreens, startTime: String, endTime: String, chosenTag: String, chosenText: String, chosenTitle: String, id: Int, color: String) -> Unit, // a lambda when moving to creation sub screen after a block click
    currentBlock: Boolean, // if current block icon was clicked
    onCurrentBlock: (Boolean) -> Unit, longClick: Map<Time, Boolean>, // a lambda to change current block
    onLongClick: (Time) -> Unit, // a lambda to change a keys value in longClick map
    fontSize: String,
    fontType: String,
    expendedIcons: () -> Unit, // a lambda to change the expand state of the icon
    saveCreationState: (String, String, String, String, String, Int) -> Unit // a function that saves the values in creation screen for it's intial value
    ){

    val currentTime = LocalTime.now() // current time in LocalTime
    val hoursMinutes = String.
    format("%02d:%02d", currentTime.hour, currentTime.minute) // time in minutes and hours in hh:mm

    var currentBlockIndex = 0 // the index of the block that it's time is right noew

    for ((index, block) in timeBlocks.withIndex()){
        if(getTime(block.startTime) <= getTime(hoursMinutes) &&
            getTime(block.endTime) >= getTime(hoursMinutes)
        ){
            currentBlockIndex = index
            break
        }
    }

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = currentBlockIndex) // state of the list starts at currentBlockIndex
    val coroutineScope = rememberCoroutineScope()

    LazyColumn (state = listState) { // a list that is scrollable
        itemsIndexed(timeBlocks) { index, block -> // goes over each time block by time and index
            DisplayBlock( // a function for each time block in the column
                length = getLength(endTime = block.endTime, startTime = block.startTime).toString(), // the length of the time block
                text = block.text, // the text of the time block
                title = block.title, // the title of the time block
                id = block.uid, // the id of the time block
                modifier = Modifier.padding(bottom = 8.dp), // passes a modifier with bottom padding
                notification = block.notification, // the notification of the time block
                endTime = block.endTime, // the end time of the time block
                startTime = block.startTime, // the start time of the time block
                onBlockInput = onBlockInput, // a lambda when moving to creation sub screen after a block click
                currentBlock = currentBlockIndex == index, // true only if the block is the block that is current
                longClick = longClick, // a map that contains keys that are time blocks and values that are booleans. The booleans represent if the user long clicked on each block or not.
                onLongClick = onLongClick, // changes longClick
                date = block.date, // block's date
                color = block.color, // block's color
                fontType = fontType, // font type
                fontSize = fontSize, // font size
                expendedIcons = expendedIcons, // toggles the expanded icon off
                saveCreationState = saveCreationState // a lambda for saving the block parameters in the creation viewModel

            )
        }
    }

    if(currentBlock){ // scrolls to the current block after clicking currentBlock
        DisposableEffect(Unit) {
            coroutineScope.launch {
                listState.animateScrollToItem(currentBlockIndex, scrollOffset = -15)
            }
            onDispose { }
        }
        onCurrentBlock(false)  // change current block to false
    }
}


    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun DisplayBlock(
        length: String,
        date: String,
        text: String,
        title: String,
        color: String,
        notification: String,
        id: Int,
        modifier: Modifier,
        startTime: String,
        endTime: String,
        onBlockInput: (subScreen: SubScreens, startTime: String, endTime: String, chosenTag: String, chosenText: String, chosenTitle: String, id: Int, color: String) -> Unit,
        currentBlock: Boolean,
        longClick: Map<Time, Boolean>,
        onLongClick: (Time) -> Unit,
        fontSize: String,
        fontType: String,
        expendedIcons: () -> Unit,
        saveCreationState: (String, String, String, String, String, Int) -> Unit,
    ) {

    var expanded by remember { mutableStateOf(false) }

    Column{
        Text(
            text = startTime,
            color = Color.Gray,
            modifier = Modifier
                .padding(bottom = 4.dp)
                .padding(horizontal = 3.5.dp)
        )

        OutlinedCard(
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(
                if (currentBlock) {
                    3.dp
                } else {
                    0.dp
                }, MaterialTheme.colorScheme.primary
            ),
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
                .combinedClickable(onClick = {
                    if (longClick.containsValue(true)) {
                        onLongClick(
                            Time(
                                uid = id,
                                text = text,
                                title = title,
                                notification = notification,
                                startTime = startTime,
                                endTime = endTime,
                                date = date,
                                color = color
                            )
                        )
                        expendedIcons()
                    } else {
                        onBlockInput(
                            SubScreens.CREATION,
                            startTime,
                            endTime,
                            notification,
                            text,
                            title,
                            id,
                            color
                        )
                        saveCreationState( title, text, startTime, endTime, color, id)
                    }
                },
                    onLongClick = {
                        onLongClick(
                            Time(
                                uid = id,
                                text = text,
                                title = title,
                                notification = notification,
                                startTime = startTime,
                                endTime = endTime,
                                date = date,
                                color = color
                            )
                        )
                        expendedIcons()
                    }),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),

            colors = if (longClick[Time(
                    uid = id,
                    text = text,
                    title = title,
                    notification = notification,
                    startTime = startTime,
                    endTime = endTime,
                    date = date,
                    color = color
                )] == true
            ) {
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            } else {
                if(color != "" && color != "#FFFFFFFF"){CardDefaults.cardColors(
                    containerColor = Color(android.graphics.Color.parseColor(color))
                )}
                else{
                    CardDefaults.cardColors(

                    )
                }
            }

        ) {
            Row {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .animateContentSize(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        )
                        .weight(1F)
                ) {

                    val minutes = if (length.toInt() % 60 == 0 && (length.toInt() / 60 != 0)) {
                        ""
                    } else {
                        (length.toInt() % 60).toString() + " m"
                    }
                    val hours = if (length.toInt() / 60 == 0) {
                        ""
                    } else {
                        (length.toInt() / 60).toString() + " h "
                    }

                    if (title != "") {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 4.dp),
                            fontSize = when(fontSize){"Large" -> 18.sp; "Small" -> 14.sp; else -> 16.sp},
                            fontFamily = when(fontType){"Sans Serif" -> FontFamily.SansSerif; "Monospace" -> FontFamily.Monospace; else -> FontFamily.SansSerif}
                        )
                    }

                    if (text != "") {
                        if (expanded) {
                            Text(text = text, color = Color.Gray, fontSize = when(fontSize){"Large" -> 18.sp; "Small" -> 14.sp; else -> 16.sp},
                                fontFamily = when(fontType){"Sans Serif" -> FontFamily.SansSerif; "Monospace" -> FontFamily.Monospace; else -> FontFamily.SansSerif}

                            )
                        }
                    }

                    //Spacer(Modifier.height(8.dp))
                    Text("${hours}${minutes}", color = Color.Gray, fontSize = when(fontSize){"Large" -> 18.sp; "Small" -> 14.sp; else -> 16.sp},
                        fontFamily = when(fontType){"Sans Serif" -> FontFamily.SansSerif; "Monospace" -> FontFamily.Monospace; else -> FontFamily.SansSerif})
                }



                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (notification != "") {
                        IconButton(
                            onClick = {  },
                            modifier = modifier,
                            enabled = false
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.notification_icon),
                                contentDescription = stringResource(R.string.time_block_notification_symbol),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    if (text != "") {
                        IconButton(
                            onClick = { expanded = !expanded; expendedIcons() },
                            modifier = modifier
                        ) {
                            Icon(
                                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                contentDescription = stringResource(R.string.time_block_expand_button_content_description),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
        }
        if (endTime == "24:00") {
            Text(
                text = "00:00",
                color = Color.Gray,
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .padding(horizontal = 3.5.dp)
            )
        }
    }
}



