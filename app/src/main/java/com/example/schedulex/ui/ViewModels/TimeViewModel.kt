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

package com.example.schedulex.ui.ViewModels

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schedulex.data.SettingsRepository
import com.example.schedulex.data.TimeRepository
import com.example.schedulex.data.local.database.Time
import com.example.schedulex.data.local.database.Title
import com.example.schedulex.ui.MainActivity
import com.example.schedulex.ui.TimeUi.AlarmItem
import com.example.schedulex.ui.TimeUi.AndroidAlarmScheduler
import com.example.schedulex.ui.TimeUi.adjustDateTime
import com.example.schedulex.ui.TimeUi.calculateDifference
import com.example.schedulex.ui.TimeUi.dateTimeToLocalDateTime
import com.example.schedulex.ui.TimeUi.getCurrentDate
import com.example.schedulex.ui.TimeUi.getCurrentTime
import com.example.schedulex.ui.TimeUi.getTime
import com.example.schedulex.ui.TimeUi.notificationMessage
import com.example.schedulex.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject




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
            savedTitles = emptyList(),
            darkTheme = false,
            fontType = "Sans Serif",
            fontSize = "Medium"
    ))

    val uiState: StateFlow<TimeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.chunksDivider.collect { divider ->
                _uiState.value = _uiState.value.copy(chunksDivider = divider)
                updateUiState()
            }
        }
        viewModelScope.launch {
            settingsRepository.fontSize.collect { fontSize ->
                _uiState.value = _uiState.value.copy(fontSize = fontSize)
            }
        }
        viewModelScope.launch {
            settingsRepository.fontType.collect { fontType ->
                _uiState.value = _uiState.value.copy(fontType = fontType)
            }
        }
        viewModelScope.launch {
            settingsRepository.darkTheme.collect { darkTheme ->
                _uiState.value = _uiState.value.copy(darkTheme = darkTheme)
            }
        }
    }

    fun updateSettings(darkTheme: Boolean, fontSize: String, fontType: String) {
        viewModelScope.launch {
            settingsRepository.updateSettings(darkTheme, fontSize, fontType)
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun onPlay(id: Int, context: Context, title: String, startTime: String) {
        viewModelScope.launch {
            settingsRepository.updatePlay("${getCurrentTime()}$id")
            val CHANNEL_ID = "your_channel_id"
            val notificationId = -2

// Create a notification channel
            val descriptionText = "active block"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, title, importance).apply {
                description = descriptionText
            }

// Register the channel with the system
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

// Create an explicit intent for an Activity in your app
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE
            )

// Build the notification
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.round_play_circle_outline_24)
                .setLargeIcon(Icon.createWithResource(context,R.mipmap.ic_launcher_round))
                .setContentTitle(title)
                .setContentText("Started at $startTime")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)





// Show the notification
            with(NotificationManagerCompat.from(context)) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    val settingsIntent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    settingsIntent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    context.startActivity(settingsIntent)

                    return@launch
                }
                notify(notificationId, builder.build())
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun onStop(title: String, color: String, context: Context) {
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
                        notification = "",
                        text = "",
                        id = -1,
                        context = context
                    )
                }
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(-2)
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

    @RequiresApi(Build.VERSION_CODES.S)
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
                                timeRepository.insert(Time(
                                    title = time.title,
                                    text =  time.text,
                                    notification = time.notification,
                                    startTime = time.startTime,
                                    endTime = time.endTime,
                                    uid = time.uid,
                                    color = title.color,
                                    date = time.date)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
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

            //loadTitles()
           // updateUiState()

            timeRepository.getTimeByTitle(title).collect { timeBlocks ->
                timeBlocks.forEach { time ->
                    if (time.title == title && time.color == color) { // when color is wrong reset it
                        timeRepository.insert(Time(
                            title = time.title,
                            text =  time.text,
                            notification = time.notification,
                            startTime = time.startTime,
                            endTime = time.endTime,
                            uid = time.uid,
                            color = "",
                            date = time.date)
                        )
                    }
                }
            }
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
                _uiState.value = _uiState.value.copy(
                    timeBlocks = blocksAndChunks(
                        blocks = times,
                        chunks = chunksGenerator(_uiState.value.chunksDivider)
                    )
                )
            }


        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun insertTimeBlock(
        title: String,
        text: String,
        notification: String,
        startTime: String,
        endTime: String,
        id: Int,
        color: String,
        context: Context,
    ) {

        val scheduler = AndroidAlarmScheduler(context)
        val currentTime = LocalDateTime.now()

                for (block in _uiState.value.timeBlocks) {
                    val difference =  calculateDifference(
                        date2 = block.startTime.substring(
                            0,
                            2
                        ) + "-" + block.startTime.substring(3, 5) + "-" + block.date,
                        date1 = block.notification)

                    var adjustedNotification = block.notification

                    if(difference != -1L){
                        adjustedNotification = adjustDateTime(block.notification, difference.toInt())
                    }


                    if (block.uid != -1) {
                        viewModelScope.launch {
                            timeRepository.delete(block.uid)

                            if(adjustedNotification != "") {
                                AlarmItem(
                                    time = dateTimeToLocalDateTime(adjustedNotification),
                                    message = notificationMessage(calculateDifference(
                                        date2 = block.startTime.substring(
                                            0,
                                            2
                                        ) + "-" + block.startTime.substring(3, 5) + "-" + block.date,
                                        date1 = adjustedNotification).toInt()) + ",${block.startTime}-${block.endTime}",
                                    id = block.uid,
                                    title = block.title
                                ).let(scheduler::cancel)
                            }
                        }
                        if ((getTime(block.startTime) <= getTime(startTime) && getTime(startTime) <= getTime(block.endTime)) || (getTime(block.endTime) >= getTime(endTime) && getTime(block.startTime) <= getTime(endTime))
                        ) {

                            if (getTime(block.startTime) <= getTime(startTime) && getTime(startTime) <= getTime(block.endTime)) {
                                viewModelScope.launch {
                                    timeRepository.insert(Time(date = block.date, startTime = block.startTime, endTime = startTime, title = block.title, text = block.text, notification = adjustedNotification, color = block.color, uid = block.uid))
                                    if(block.notification != "" ){
                                        if(dateTimeToLocalDateTime(adjustedNotification).isAfter(currentTime)){
                                        AlarmItem(
                                            time = dateTimeToLocalDateTime(adjustedNotification),
                                            message = notificationMessage(calculateDifference(
                                                date2 = block.startTime.substring(
                                                    0,
                                                    2
                                                ) + "-" + block.startTime.substring(3, 5) + "-" + block.date,
                                                date1 = adjustedNotification).toInt()) + ",${block.startTime}-${block.endTime}",
                                            id = block.uid,
                                            title = block.title
                                        ).let(scheduler::schedule)
                                        }
                                    }
                                }
                            }
                            if (getTime(block.endTime) >= getTime(endTime) && getTime(block.startTime) <= getTime(endTime)) {
                                viewModelScope.launch {
                                    timeRepository.insert(
                                        Time(date = block.date, startTime = endTime, endTime = block.endTime, title = block.title, text = block.text, notification = adjustedNotification, color = block.color, uid = block.uid)
                                    )
                                    if( adjustedNotification != ""){
                                        if(dateTimeToLocalDateTime(adjustedNotification).isAfter(currentTime)) {
                                            AlarmItem(
                                                time = dateTimeToLocalDateTime(adjustedNotification),
                                                message = notificationMessage(calculateDifference(
                                                    date2 = block.startTime.substring(
                                                        0,
                                                        2
                                                    ) + "-" + block.startTime.substring(3, 5) + "-" + block.date,
                                                    date1 = adjustedNotification).toInt()) + ",${block.startTime}-${block.endTime}",
                                                id = block.uid,
                                                title = block.title
                                            ).let(scheduler::schedule)
                                        }
                                    }
                                }

                            }
                        } else if (getTime(block.startTime) >= getTime(startTime) && getTime(block.endTime) >= getTime(
                                endTime
                            ) && getTime(endTime) >= getTime(block.startTime)
                        ) {
                            viewModelScope.launch {
                                timeRepository.insert(
                                    Time(date = block.date, startTime = endTime, endTime = block.endTime, title = block.title, text = block.text, notification = adjustedNotification, color = block.color, uid = block.uid)
                                )
                                if( adjustedNotification != ""){
                                    if(dateTimeToLocalDateTime(adjustedNotification).isAfter(currentTime)){
                                    AlarmItem(
                                        time = dateTimeToLocalDateTime( adjustedNotification),
                                        message = notificationMessage(calculateDifference(
                                            date2 = block.startTime.substring(
                                                0,
                                                2
                                            ) + "-" + block.startTime.substring(3, 5) + "-" + block.date,
                                            date1 = adjustedNotification).toInt()) + ",${block.startTime}-${block.endTime}",
                                        id = block.uid,
                                        title = block.title
                                    ).let(scheduler::schedule)
                                    }
                                }
                            }
                        } else if (getTime(block.startTime) <= getTime(startTime) && getTime(block.endTime) <= getTime(
                                endTime
                            ) && getTime(block.startTime) >= getTime(endTime)
                        ) {
                            viewModelScope.launch {
                                timeRepository.insert(
                                    Time(date = block.date, startTime = block.startTime, endTime = startTime, title = block.title, text = block.text, notification = adjustedNotification, color = block.color, uid = block.uid)
                                )
                                if( adjustedNotification != ""){
                                    if(dateTimeToLocalDateTime(adjustedNotification).isAfter(currentTime)) {
                                        AlarmItem(
                                            time = dateTimeToLocalDateTime(adjustedNotification),
                                            message = notificationMessage(calculateDifference(
                                                date2 = block.startTime.substring(
                                                    0,
                                                    2
                                                ) + "-" + block.startTime.substring(3, 5) + "-" + block.date,
                                                date1 = adjustedNotification).toInt()) + ",${block.startTime}-${block.endTime}",
                                            id = block.uid,
                                            title = block.title
                                        ).let(scheduler::schedule)
                                    }
                                }
                            }
                        }
                        else if(getTime(block.endTime) <= getTime(startTime) || getTime(block.startTime) >= getTime(endTime)){
                            viewModelScope.launch {
                                timeRepository.insert(
                                    Time(date = block.date, startTime = block.startTime, endTime = block.endTime, title = block.title, text = block.text, notification = adjustedNotification, color = block.color, uid = block.uid)
                                )
                                if( adjustedNotification != ""){
                                    if(dateTimeToLocalDateTime(adjustedNotification).isAfter(currentTime)) {
                                        AlarmItem(
                                            time = dateTimeToLocalDateTime(adjustedNotification),
                                            message = notificationMessage(calculateDifference(
                                                date2 = block.startTime.substring(
                                                    0,
                                                    2
                                                ) + "-" + block.startTime.substring(3, 5) + "-" + block.date,
                                                date1 = adjustedNotification).toInt()) + ",${block.startTime}-${block.endTime}",
                                            id = block.uid,
                                            title = block.title
                                        ).let(scheduler::schedule)
                                    }
                                }
                            }
                        }
                    }
                }

                val newTime = Time(
                    date = _uiState.value.date,
                    title = title,
                    text = text,
                    notification = notification,
                    startTime = startTime,
                    endTime = endTime,
                    color = color
                )

                if (id != -1) {
                    newTime.uid = id
                }

                viewModelScope.launch {
                    timeRepository.insert(newTime)
                   // val updatedTimeBlocks = _uiState.value.timeBlocks + newTime
                   // _uiState.value = _uiState.value.copy(timeBlocks = updatedTimeBlocks)
                    updateUiState()
                    if(newTime.notification != ""){
                        if(dateTimeToLocalDateTime(newTime.notification).isAfter(currentTime)) {
                            AlarmItem(
                                time = dateTimeToLocalDateTime(newTime.notification),
                                message =  notificationMessage(calculateDifference(
                                    date2 = newTime.startTime.substring(
                                        0,
                                        2
                                    ) + "-" + newTime.startTime.substring(3, 5) + "-" + newTime.date,
                                    date1 = newTime.notification).toInt())  + ",${newTime.startTime}-${newTime.endTime}",
                                id = newTime.uid,
                                title = newTime.title
                            ).let(scheduler::schedule)
                        }
                    }
                }
    }

    fun deleteTimeBlock(id: Int, context: Context) {

        val scheduler = AndroidAlarmScheduler(context)

        viewModelScope.launch {
            timeRepository.delete(id)

        AlarmItem(
            time = LocalDateTime.now(),
            message = "",
            id = id,
            title = ""
        ).let(scheduler::cancel)
    }
    }
}
data class TimeUiState(
    val date: String,
    val chunksDivider: Int,
    val timeBlocks: List<Time> = emptyList<Time>(),
    val savedTitles: List<Title>,
    val darkTheme: Boolean,
    val fontType: String,
    val fontSize: String
)




fun chunksGenerator(chunksDivider: Int) : List<Time>
    {
    if (chunksDivider != 0) {
        return List(1440 / chunksDivider) { index ->
            Time(
                date = "",
                title = "",
                text = "",
                notification = "",
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
            finalList.add(Time(date = "", startTime = lastEndTime, endTime = event.startTime, title = "", text = "", notification = "", uid = -1, color = ""))
        }

        // Add the current event to the final list
        finalList.add(event)

        // Update the end of the last event
        lastEndTime = event.endTime
    }

    // If there is a gap between the end of the last event and the end of the day, fill it
    if (getTime(lastEndTime) < getTime("24:00")) {
        finalList.add(Time(date = "", startTime = lastEndTime, endTime = "24:00", title = "", text = "", notification = "", uid = -1, color = ""))
    }

    return finalList
}
