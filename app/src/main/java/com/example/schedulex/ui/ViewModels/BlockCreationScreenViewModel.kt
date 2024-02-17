package com.example.schedulex.ui.ViewModels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CreationViewModel @Inject constructor(
) : ViewModel() {


   private val _uiState = MutableStateFlow<CreationUiState>(
       CreationUiState("00:00", "00:00", "", "", "", -1)
   )

   val uiState: StateFlow<CreationUiState> = _uiState.asStateFlow()

   fun saveUiState(title: String, text: String, startTime: String, endTime: String, color: String, id: Int) {
       _uiState.value = CreationUiState(startTime = startTime, endTime = endTime, title = title, text = text, color = color, id = id)
   }
   fun saveTitle(title: String) {
      _uiState.value = uiState.value.copy(title = title)
   }
   fun saveText(text: String) {
      _uiState.value = uiState.value.copy(text = text)
   }
   fun saveStartTime(startTime: String) {
      _uiState.value = uiState.value.copy(startTime = startTime)
   }
   fun saveEndTime(endTime: String) {
      _uiState.value = uiState.value.copy(endTime = endTime)
   }
   fun saveColor(color: String) {
      _uiState.value = uiState.value.copy(color = color)
   }
   fun saveId(id: Int) {
      _uiState.value = uiState.value.copy(id = id)
   }
}

data class CreationUiState(
   val startTime: String,
   val endTime: String,
   val title: String,
   val text: String,
   val color: String,
   val id: Int

)

