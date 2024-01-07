package com.example.kashio.ui.TimeUi

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kashio.R
import com.example.kashio.data.local.database.Time
import androidx.compose.material.icons.filled.KeyboardArrowDown //ExpandLess
import androidx.compose.material.icons.filled.KeyboardArrowUp //ExpandMore
import org.jetbrains.annotations.Blocking

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun BlockPreview() {

  //  BlockList(blocks =)
}

    fun getLength(endTime: String, startTime: String): Int{
        return ((endTime.substring(0,2).toInt() * 60 + endTime.substring(3,5).toInt()) -
                (startTime.substring(0,2).toInt() * 60 + startTime.substring(3,5).toInt()))
    }

    fun getTime(time: String): Int{ //return the time in minutes 02:10 -> 2 * 60 + 10
       return (time.substring(0,2).toInt() * 60 + time.substring(3,5).toInt())
    }

    fun blocksAndChunks(blocks: List<Time>, chunks: List<Time>) : List<Time> { //receives 2 list  of blocks. one filled by chunks and the other by blocks.
        val mergedLists = chunks.toMutableList() //mutableListOf<Time>()

         blocks.forEach { block ->
            mergedLists.add(block)
            chunks.forEachIndexed { index, chunk ->
                if (getTime(chunk.endTime) <= getTime(block.endTime) && getTime(block.startTime) > getTime(
                        chunk.startTime) && getTime(chunks[index + 1].startTime) > getTime(block.startTime)
                ) {
                    mergedLists.add(
                        Time(
                            date = chunk.date,
                            startTime = chunk.startTime,
                            endTime = block.startTime,
                            tag = "",
                            text = "",
                            title = ""
                        )
                    )
                    mergedLists.remove(chunk)
                } else {
                    if(index != chunks.lastIndex)
                        if (getTime(chunk.endTime) > getTime(block.endTime) && getTime(block.endTime) > getTime(
                                chunk.startTime) && getTime(chunks[index + 1].startTime) > getTime(block.endTime)
                            /*getTime(chunks[index + 1].endTime) > getTime(block.startTime)*/
                        ) {
                            mergedLists.add(
                                Time(
                                    date = chunk.date,
                                    startTime = block.endTime,
                                    endTime = chunk.endTime,
                                    tag = "",
                                    text = "",
                                    title = ""
                                )
                            )
                            mergedLists.remove(chunk)
                        }
                    }
                if(getTime(chunk.startTime) >= getTime(block.startTime) && getTime(chunk.endTime) <= getTime(block.endTime)){
                    mergedLists.remove(chunk)
                }
                }
            }
        return mergedLists.sortedBy { it.startTime } // returns a list of blocks and chunks
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun BlockList(viewModel: TimeViewModel, selectedDate: String, blockInput: Boolean,  onBlockInput: (blockInput: Boolean) -> Unit) {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val blocks = uiState.timeBlocks
        val chunksDivider = 60 // in minutes

        val chunks = List(1440/chunksDivider){ index -> Time(
            date = "",
            title = "",
            text = "",
            tag = "",
            startTime = "${((index * chunksDivider) / 60).toString().padStart(2, '0')}:${((index * chunksDivider) % 60).toString().padStart(2, '0')}",
            endTime = "${(((index + 1) * chunksDivider) / 60).toString().padStart(2, '0')}:${(((index + 1) * chunksDivider) % 60).toString().padStart(2, '0')}",
            )
        }


        LazyColumn {
            items(blocksAndChunks(blocks = blocks, chunks = chunks)) { block ->
                Block(
                    length = getLength(endTime = block.endTime, startTime = block.startTime).toString()
                , text = block.text, title = block.title, modifier = Modifier.padding(bottom = 8.dp), endTime = block.endTime, startTime = block.startTime,
                    viewModel = viewModel, selectedDate = selectedDate, blockInput = blockInput, onBlockInput = onBlockInput)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Block(length: String,
              text: String,
              title: String,
              modifier: Modifier,
              startTime: String,
              endTime: String,
              viewModel: TimeViewModel,
              selectedDate: String,
              blockInput: Boolean,
              onBlockInput: (blockInput: Boolean) -> Unit,){

        var expanded by remember { mutableStateOf(false) }

        if(blockInput){
            BlockCreationScreen(viewModel = viewModel, selectedDate = selectedDate)
        }
        else {
            OutlinedCard(
                onClick =  { onBlockInput(true)} ,
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp)

            ) {
                Row() {
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
                            (length.toInt() / 60).toString() + " h"
                        }

                        if (title != "") {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                        Text(
                            text = startTime,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = endTime,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        if (text != "") {
                            if (expanded) {
                                Text(text = text, color = Color.Gray)
                            }
                        }

                        Spacer(Modifier.height(8.dp))
                        Text("${hours} ${minutes}", color = Color.Gray)
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
        }
    }
