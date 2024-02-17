package com.example.kashio.ui.TimeUi

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kashio.data.local.database.Time
import com.example.kashio.data.local.database.Title
import com.example.kashio.ui.HSLA
import java.time.LocalTime

@Composable
fun TimeSubApp(
    modifier: Modifier = Modifier,
    timeBlocks: List<Time>,
    onSave: (title: String, text: String, tag: String, startTime: String, endTime: String, id: Int, color: String) -> Unit,
    blockInput: Boolean,
    onBlockInput: (Boolean) -> Unit,
    currentBlock: Boolean,
    onCurrentBlock: (Boolean) -> Unit,
    onBlockSave: () -> Unit,
    blockSave: Boolean,
    onBlockDelete: () -> Unit,
    blockDelete: Boolean,
    onDelete: (id: Int) -> Unit,
    onDeletableBlock: (Boolean) -> Unit,
    longClick: Map<Time, Boolean>,
    onLongClick: (Time) -> Unit,
    savedTitles: List<Title>,
    onNewTitle: (Boolean) -> Unit,
    newTitle: Boolean,
    insertTitle: (String, String, Int) -> Unit,
    sortIndex: HSLA,
    onSortIndex: (HSLA) -> Unit
    //onShowSnackbar: (Boolean) -> Unit
) {
    var startTime by remember { mutableStateOf("00:00") }

    var endTime by remember { mutableStateOf("00:00") }

    var title by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("") }
    var id by remember { mutableIntStateOf(-1) }  // the id for a new block is -1
    var color by remember { mutableStateOf("") }

    val currentTime = LocalTime.now()

    Box(modifier = modifier.fillMaxSize()) {
            if (!blockInput) {

                BlockList(
                    timeBlocks = timeBlocks,
                    onBlockInput = { blockInputLam, startTimeLam, endTimeLam, chosenTag, chosenText, chosenTitle, chosenId, chosenColor->
                        onBlockInput(blockInputLam)

                        startTime = if (startTimeLam == "24:00") {
                            "00:00"
                        } else {
                            startTimeLam
                        }
                        endTime = if (endTimeLam == "24:00") {
                            "00:00"
                        } else {
                            endTimeLam
                        }

                        tag = chosenTag
                        title = chosenTitle
                        text = chosenText
                        id = chosenId
                        color = chosenColor

                        if(id != -1) {onDeletableBlock(true)}
                    },
                    currentBlock = currentBlock,
                    onCurrentBlock = onCurrentBlock,
                    onLongClick = onLongClick,
                    longClick = longClick
                )

                FloatingActionButton(
                    onClick = {
                        onBlockInput(true)
                        startTime = "${currentTime.hour.toString().padStart(2, '0')}:${
                            currentTime.minute.toString().padStart(2, '0')
                        }"
                        endTime = "${currentTime.hour.toString().padStart(2, '0')}:${
                            currentTime.minute.toString().padStart(2, '0')
                       }"

                        tag = ""
                        title = ""
                        text = ""
                        id = -1 // new block
                        color = ""

                        onDeletableBlock(false)
                    },

                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(15.dp),
                        //   backgroundColor = MaterialTheme.colors.primary
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Floating action button")
                }
            }
            else{
                BlockCreationScreen(
                    startTime = startTime,
                    endTime = endTime,
                    onSave = onSave,
                    blockSave = blockSave,
                    onBlockSave = onBlockSave,
                    onBlockInput = onBlockInput,
                    onBlockDelete = onBlockDelete,
                    blockDelete = blockDelete,
                    onDelete = onDelete,
                    formerTag = tag,
                    formerText = text,
                    formerTitle = title,
                    formerId = id,
                    formerColor = color,
                    savedTitles = savedTitles,
                    onNewTitle = onNewTitle,
                    newTitle = newTitle,
                    insertTitle = insertTitle,
                    sortIndex = sortIndex,
                    onSortIndex = onSortIndex
                   // onShowSnackbar = onShowSnackbar
                )
            }
    }
}






