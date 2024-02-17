/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.kashio.ui.TimeUi

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kashio.data.SettingsRepository
import com.example.kashio.data.TimeRepository
import com.example.kashio.data.local.database.Time
import com.example.kashio.data.local.database.Title
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject




 /* @RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class TimeViewModel @Inject constructor(
    private val timeRepository: TimeRepository
) : ViewModel() {

    private val mCalendar = Calendar.getInstance()

    private val _uiState = MutableStateFlow<TimeUiState>(
        TimeUiState(
            date = (mCalendar[Calendar.DAY_OF_MONTH]).toString().padStart(2, '0') + "-" +
                    ((mCalendar[Calendar.MONTH] + 1)).toString().padStart(2, '0') + "-" +
                    (mCalendar[Calendar.YEAR]).toString().padStart(4, '0'),
            chunksDivider = 0,
            timeBlocks = emptyList()
        )
    )

    val uiState: StateFlow<TimeUiState> = _uiState

    init {
        viewModelScope.launch {
            _uiState.collect { uiState ->
                updateUiState(uiState.date, uiState.chunksDivider)
            }
        }
    }

    private fun updateUiState(date: String, chunksDivider: Int) {
        viewModelScope.launch {
            timeRepository.getAllTimesForDate(date).collect { times ->
                _uiState.value = TimeUiState(date, chunksDivider, blocksAndChunks(blocks = times, chunks = chunksGenerator(chunksDivider)))
            }
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun setDate(newDate: String) {
        _uiState.value = _uiState.value.copy(date = newDate)
    }

    fun setChunksDivider(newDivider: Int) {
        _uiState.value = _uiState.value.copy(chunksDivider = newDivider)
    }

    fun insertTimeBlock(time: String, title: String, text: String, tag: String, startTime: String, endTime: String) {
        viewModelScope.launch {
            timeRepository.insert(Time(date = time, title = title, text = text, tag = tag, startTime = startTime, endTime = endTime))
        }
    }
}

data class TimeUiState(
    val date: String,
    val chunksDivider: Int,
    val timeBlocks: List<Time> = emptyList<Time>(),
)
*/

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class TimeViewModel @Inject constructor(
    private val timeRepository: TimeRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TimeUiState>(
        TimeUiState(
            date = getCurrentDate(),
            chunksDivider = 0,
            timeBlocks = emptyList(),
            savedTitles = emptyList()
        )
    )

    val uiState: StateFlow<TimeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.chunksDivider.collect { divider ->
                _uiState.value = _uiState.value.copy(chunksDivider = divider)
                updateUiState()
            }
        }
    }


    suspend fun getPlay(): Int {
        var playId = -1

        viewModelScope.launch {
            val playString = settingsRepository.returnPlay()
            if (playString.isNotEmpty() && playString.length > 5) {
                playId = playString.substring(5).toIntOrNull() ?: -1
            }
        }.join() // Wait for the coroutine to finish

        return playId
    }

    fun onPlay(id: Int) {
        viewModelScope.launch {
            settingsRepository.updatePlay("${getCurrentTime()}$id")
        }
    }

    fun onStop(title: String, color: String) {
        var time = ""

        viewModelScope.launch {
            time = settingsRepository.returnPlay().substring(0, 5)

            settingsRepository.updatePlay("")

            setDate(getCurrentDate())

            if (time != "") {
                if (getTime(time) < getTime(getCurrentTime())) {
                    insertTimeBlock(
                        title = title,
                        color = color,
                        startTime = time,
                        endTime = getCurrentTime(),
                        tag = "",
                        text = "",
                        id = -1
                    )
                }
            }
        }
    }

    fun loadTitles() {
        viewModelScope.launch {
            timeRepository.getTitles().collect { titles ->
                _uiState.value = _uiState.value.copy(savedTitles = titles.map {
                    Title(
                        it.title,
                        it.color,
                        it.uid
                    )
                })
            }
        }
    }

    fun insertTitle(title: String, color: String, id: Int) {
        if (id == -1) { // valid id new title
            val newTitle = Title(title, color)
            _uiState.value =
                _uiState.value.copy(savedTitles = _uiState.value.savedTitles + newTitle)
            viewModelScope.launch {
                timeRepository.insertTitle(newTitle)
            }
            loadTitles()
        } else {
            viewModelScope.launch {
                _uiState.value =
                    _uiState.value.copy(
                        savedTitles = _uiState.value.savedTitles + Title(
                            title,
                            color,
                            id
                        )
                    )
                timeRepository.insertTitle(Title(title, color, id))
                loadTitles()
                updateUiState()

                timeRepository.getTimeByTitle(title).collect { timeBlocks ->
                    timeBlocks.forEach { time ->
                        _uiState.value.savedTitles.forEach { title ->
                            if (time.title == title.title && time.color != title.color) { // when color is wrong update it
                                insertTimeBlock(
                                    time.title,
                                    time.text,
                                    time.tag,
                                    time.startTime,
                                    time.endTime,
                                    time.uid,
                                    title.color
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun deleteTitle(title: String, color: String, id: Int) {
        viewModelScope.launch {

       _uiState.value =
            _uiState.value.copy(
                savedTitles = _uiState.value.savedTitles - Title(
                    title,
                    color,
                    id
                )
            )

            timeRepository.deleteTitle(Title(title, color, id))

            loadTitles()
            updateUiState()

            timeRepository.getTimeByTitle(title).collect { timeBlocks ->
                timeBlocks.forEach { time ->
                        if (time.title == title && time.color == color) { // when color is wrong reset it
                            insertTimeBlock(
                                time.title,
                                time.text,
                                time.tag,
                                time.startTime,
                                time.endTime,
                                time.uid,
                                ""
                            )
                    }
                }
            }
            /* timeRepository.getTimeByTitle(title).collect { timeBlocks ->
                timeBlocks.forEach { time ->
                    if (time.title == title) {
                        insertTimeBlock(
                            time.title,
                            time.text,
                            time.tag,
                            time.startTime,
                            time.endTime,
                            time.uid,
                            ""
                        )
                    }
                }
            } */
    }
}

    fun getDatesInRange(startDate: String, endDate: String, callback: (List<Time>) -> Unit) {
        viewModelScope.launch {
            timeRepository.getTimeBetweenDates(startDate, endDate).collect { times ->
                callback(times)
            }
        }
    }



    fun setDate(newDate: String) {
        _uiState.value = _uiState.value.copy(date = newDate)
        updateUiState()
    }

    fun setChunksDivider(newDivider: Int) {
        viewModelScope.launch {
            settingsRepository.updateChunksDivider(newDivider)
        }
    }

    private fun updateUiState() {
        viewModelScope.launch {
            timeRepository.getAllTimesForDate(_uiState.value.date).collect { times ->
                _uiState.value = _uiState.value.copy(timeBlocks = blocksAndChunks(blocks = times, chunks = chunksGenerator(_uiState.value.chunksDivider)))
            }
        }
    }

    fun insertTimeBlock(title: String, text: String, tag: String, startTime: String, endTime: String, id: Int, color: String) {
        for (block in _uiState.value.timeBlocks) {

            if (block.uid != -1) {
                viewModelScope.launch {
                    timeRepository.delete(block.uid)
                }
                if (getTime(block.startTime) < getTime(startTime) || getTime(block.endTime) > getTime(
                        endTime
                    )
                ) {

                    if (getTime(block.startTime) < getTime(startTime)) {
                        viewModelScope.launch {
                            timeRepository.insert(
                                Time(
                                    date = block.date,
                                    startTime = block.startTime,
                                    endTime = startTime,
                                    title = block.title,
                                    text = block.text,
                                    tag = block.tag,
                                    color = block.color
                                )
                            )
                        }
                    }
                    if (getTime(block.endTime) > getTime(endTime)) {
                        viewModelScope.launch {
                            timeRepository.insert(
                                Time(
                                    date = block.date,
                                    startTime = endTime,
                                    endTime = block.endTime,
                                    title = block.title,
                                    text = block.text,
                                    tag = block.tag,
                                    color = block.color
                                )
                            )
                        }

                    }
                } else if (getTime(block.startTime) > getTime(startTime) && getTime(block.endTime) > getTime(
                        endTime
                    )
                ) {
                    viewModelScope.launch {
                        timeRepository.insert(
                            Time(
                                date = block.date,
                                startTime = endTime,
                                endTime = block.endTime,
                                title = block.title,
                                text = block.text,
                                tag = block.tag,
                                color = block.color
                            )
                        )
                    }
                } else if (getTime(block.startTime) < getTime(startTime) && getTime(block.endTime) < getTime(
                        endTime
                    )
                ) {
                    viewModelScope.launch {
                        timeRepository.insert(
                            Time(
                                date = block.date,
                                startTime = block.startTime,
                                endTime = startTime,
                                title = block.title,
                                text = block.text,
                                tag = block.tag,
                                color = block.color
                            )
                        )
                    }
                }
            }
        }

                val newTime = Time(
                    date = _uiState.value.date,
                    title = title,
                    text = text,
                    tag = tag,
                    startTime = startTime,
                    endTime = endTime,
                    color = color
                )

                if (id != -1) {
                    newTime.uid = id
                }

                viewModelScope.launch {
                    timeRepository.insert(newTime)
                    val updatedTimeBlocks = _uiState.value.timeBlocks + newTime
                    _uiState.value = _uiState.value.copy(timeBlocks = updatedTimeBlocks)
                }
        }

    fun deleteTimeBlock(id: Int) {

        viewModelScope.launch {
            timeRepository.delete(id)
        }
    }
}

data class TimeUiState(
    val date: String,
    val chunksDivider: Int,
    val timeBlocks: List<Time> = emptyList<Time>(),
    val savedTitles: List<Title>
)




fun chunksGenerator(chunksDivider: Int) : List<Time>
    {
    if (chunksDivider != 0) {
        return List(1440 / chunksDivider) { index ->
            Time(
                date = "",
                title = "",
                text = "",
                tag = "",
                startTime = "${
                    ((index * chunksDivider) / 60).toString().padStart(2, '0')
                }:${((index * chunksDivider) % 60).toString().padStart(2, '0')}",
                endTime = "${
                    (((index + 1) * chunksDivider) / 60).toString().padStart(2, '0')
                }:${(((index + 1) * chunksDivider) % 60).toString().padStart(2, '0')}",
                uid = -1,
                color = ""
            )
        }
    }
        else{return emptyList()
    }
}
fun blocksAndChunks(blocks: List<Time>, chunks: List<Time>) : List<Time> {
    val mergedLists = mutableListOf<Time>()

    // Add all blocks to the merged list
    mergedLists.addAll(blocks)

    // Iterate over each chunk
    for (chunk in chunks) {
        // Check if the chunk overlaps with any block
        val overlappingBlock = blocks.find { block ->
            getTime(block.startTime) < getTime(chunk.endTime) && getTime(block.endTime) > getTime(chunk.startTime)
        }

        // If the chunk does not overlap with any block, add it to the merged list
        if (overlappingBlock == null) {
            mergedLists.add(chunk)
        }
    }

    // Sort the merged list by start time
    val sortedList = mergedLists.sortedBy { it.startTime }

    // Create a new list to hold the final result
    val finalList = mutableListOf<Time>()

    // Initialize the end of the last event to the start of the day
    var lastEndTime = "00:00"

    // Iterate over each event in the sorted list
    for (event in sortedList) {
        // If there is a gap between the end of the last event and the start of the current event, fill it
        if (getTime(lastEndTime) < getTime(event.startTime)) {
            finalList.add(Time(date = "", startTime = lastEndTime, endTime = event.startTime, title = "", text = "", tag = "", uid = -1, color = ""))
        }

        // Add the current event to the final list
        finalList.add(event)

        // Update the end of the last event
        lastEndTime = event.endTime
    }

    // If there is a gap between the end of the last event and the end of the day, fill it
    if (getTime(lastEndTime) < getTime("24:00")) {
        finalList.add(Time(date = "", startTime = lastEndTime, endTime = "24:00", title = "", text = "", tag = "", uid = -1, color = ""))
    }

    return finalList
}
