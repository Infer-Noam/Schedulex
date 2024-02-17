package com.example.kashio.ui.TimeUi

//import com.example.kashio.ui.TimeUi.destinations.BlockCreationScreenDestination

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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.kashio.R
import com.example.kashio.data.local.database.Time
import kotlinx.coroutines.launch
import java.time.LocalTime

@Composable
fun BlockList(
    timeBlocks: List<Time>,
    onBlockInput: (blockInput: Boolean, startTime: String, endTime: String, chosenTag: String, chosenText: String, chosenTitle: String, id: Int, color: String) -> Unit,
    currentBlock: Boolean, onCurrentBlock: (Boolean) -> Unit, longClick: Map<Time, Boolean>,
    onLongClick: (Time) -> Unit
){

    val currentTime = LocalTime.now()
    val hoursMinutes = String.format("%02d:%02d", currentTime.hour, currentTime.minute)

    var currentBlockIndex = 0

    for ((index, block) in timeBlocks.withIndex()){
        if(getTime(block.startTime) <= getTime(hoursMinutes) &&
            getTime(block.endTime) >= getTime(hoursMinutes)){
            currentBlockIndex = index
            break
        }
    }

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = currentBlockIndex)
    val coroutineScope = rememberCoroutineScope()

    LazyColumn (state = listState){
        itemsIndexed(timeBlocks) { index, block ->
            Block(
                length = getLength(endTime = block.endTime, startTime = block.startTime).toString(),
                text = block.text, title = block.title, id = block.uid, modifier = Modifier.padding(bottom = 8.dp), tag = block.tag, endTime = block.endTime, startTime = block.startTime, onBlockInput = onBlockInput,
                currentBlock = currentBlockIndex == index, longClick = longClick, onLongClick = onLongClick, date = block.date, color = block.color
            )
        }
    }

    if(currentBlock){
        DisposableEffect(Unit) {
            coroutineScope.launch {
                listState.animateScrollToItem(currentBlockIndex, scrollOffset = -15)
            }
            onDispose { }
        }
        onCurrentBlock(false)
    }
}


    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    @Composable
    fun Block(
    length: String,
    date: String,
    text: String,
    title: String,
    color: String,
    tag: String,
    id: Int,
    modifier: Modifier,
    startTime: String,
    endTime: String,
    onBlockInput: (blockInput: Boolean, startTime: String, endTime: String, chosenTag: String, chosenText: String, chosenTitle: String, id: Int, color: String) -> Unit,
    currentBlock: Boolean,
    longClick: Map<Time, Boolean>,
    onLongClick: (Time) -> Unit
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
                                tag = tag,
                                startTime = startTime,
                                endTime = endTime,
                                date = date,
                                color = color
                            )
                        )
                    } else {
                        onBlockInput(true, startTime, endTime, tag, text, title, id, color)
                    }
                },
                    onLongClick = {
                        onLongClick(
                            Time(
                                uid = id,
                                text = text,
                                title = title,
                                tag = tag,
                                startTime = startTime,
                                endTime = endTime,
                                date = date,
                                color = color
                            )
                        )
                    }),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),

            colors = if (longClick[Time(
                    uid = id,
                    text = text,
                    title = title,
                    tag = tag,
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
                if(color != ""){CardDefaults.cardColors(
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
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    if (text != "") {
                        if (expanded) {
                            Text(text = text, color = Color.Gray)
                        }
                    }

                    //Spacer(Modifier.height(8.dp))
                    Text("${hours}${minutes}", color = Color.Gray)
                }

                if (text != "") {
                    IconButton(
                        onClick = { expanded = !expanded },
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



