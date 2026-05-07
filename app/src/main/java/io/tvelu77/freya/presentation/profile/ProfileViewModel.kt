package io.tvelu77.freya.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.tvelu77.freya.domain.ports.api.GetUserProfileUseCase
import io.tvelu77.freya.domain.ports.api.UpdateUserProfileUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
  private val getProfile: GetUserProfileUseCase,
  private val updateProfile: UpdateUserProfileUseCase
) : ViewModel() {

  private val _uiState = MutableStateFlow(ProfileUiState())
  val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

  init {
    viewModelScope.launch {
      getProfile.execute().collect { profile ->
        _uiState.update {
          it.copy(
            isLoading        = false,
            profile          = profile,
            firstNameInput   = profile.firstName,
            caloriesInput    = profile.baseCalories.toString(),
            cycleLengthInput = profile.cycleLengthDays.toString()
          )
        }
      }
    }
  }

  fun onFirstNameChanged(value: String) =
    _uiState.update { it.copy(firstNameInput = value) }

  fun onCaloriesChanged(value: String) =
    _uiState.update { it.copy(caloriesInput = value.filter { c -> c.isDigit() }) }

  fun onCycleLengthChanged(value: String) =
    _uiState.update { it.copy(cycleLengthInput = value.filter { c -> c.isDigit() }) }

  fun onShowCaloriesToggled(show: Boolean) {
    viewModelScope.launch { updateProfile.updateShowCalories(show) }
  }

  fun onTcaFriendlyToggled(enabled: Boolean) {
    viewModelScope.launch { updateProfile.updateTcaFriendlyMode(enabled) }
  }

  fun onNotificationsToggled(enabled: Boolean) {
    viewModelScope.launch { updateProfile.updateNotifications(enabled) }
  }

  fun onSaveProfile() {
    viewModelScope.launch {
      _uiState.update { it.copy(isSaving = true) }
      val state = _uiState.value
      updateProfile.updateFirstName(state.firstNameInput)
      updateProfile.updateBaseCalories(
        state.caloriesInput.toIntOrNull() ?: 2000
      )
      updateProfile.updateCycleLength(
        state.cycleLengthInput.toIntOrNull() ?: 28
      )
      _uiState.update { it.copy(isSaving = false, savedFeedback = true) }
      kotlinx.coroutines.delay(2000)
      _uiState.update { it.copy(savedFeedback = false) }
    }
  }
}