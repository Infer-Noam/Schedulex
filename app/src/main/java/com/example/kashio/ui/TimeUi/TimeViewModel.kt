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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import com.example.kashio.data.TimeRepository
import com.example.kashio.data.local.database.Time
import kotlinx.coroutines.launch
import javax.inject.Inject




@HiltViewModel
class TimeViewModel @Inject constructor(
    private val timeRepository:TimeRepository
) : ViewModel() {


    val uiState: StateFlow<TimeUiState> = timeRepository.times.map { TimeUiState(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TimeUiState())


    fun insertTimeBlock(time: String, title: String, text: String, tag: String){
        viewModelScope.launch {
            timeRepository.insert(Time(time = time , title = title, text = text , tag = tag))
        }
    }

}

data class TimeUiState(
  //  val splitView: Boolean = false,
    val timeBlocks: List<Time> = listOf<Time>(),
)

