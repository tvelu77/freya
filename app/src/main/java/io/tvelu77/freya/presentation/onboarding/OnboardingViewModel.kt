package io.tvelu77.freya.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.tvelu77.freya.domain.models.Goal
import io.tvelu77.freya.domain.ports.api.UpdateUserProfileUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OnboardingUiState(
  val firstName: String = "",
  val baseCalories: Int = 2000,
  val goal: Goal = Goal.MAINTENANCE,
  val cycleLengthInput: String = "28",
  val weightInput: String = "",
  val ageInput: String = "",
  val heightInput: String = "",
  val friendlyModeEnabled: Boolean = false,
  val notificationsEnabled: Boolean = true,
  val isSaving: Boolean = false
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
  private val updateProfile: UpdateUserProfileUseCase
) : ViewModel() {

  private val _uiState = MutableStateFlow(OnboardingUiState())
  val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

  fun onFirstNameChanged(value: String) =
    _uiState.update { it.copy(firstName = value) }

  fun onCaloriesChanged(value: Int) =
    _uiState.update { it.copy(baseCalories = value) }

  fun onGoalChanged(goal: Goal) =
    _uiState.update { it.copy(goal = goal) }

  fun onCycleLengthInputChanged(value: String) =
    _uiState.update { it.copy(cycleLengthInput = value.filter { c -> c.isDigit() }) }

  fun onWeightInputChanged(value: String) =
    _uiState.update {
      it.copy(weightInput = value.filter { c -> c.isDigit() || c == '.' || c == ',' })
    }

  fun onAgeInputChanged(value: String) =
    _uiState.update { it.copy(ageInput = value.filter { c -> c.isDigit() }) }

  fun onHeightInputChanged(value: String) =
    _uiState.update { it.copy(heightInput = value.filter { c -> c.isDigit() }) }

  fun onFriendlyModeToggled(show: Boolean) =
    _uiState.update { it.copy(friendlyModeEnabled = show) }

  fun onNotificationsToggled(enabled: Boolean) =
    _uiState.update { it.copy(notificationsEnabled = enabled) }

  fun onFinish(onCompleted: () -> Unit) {
    viewModelScope.launch {
      _uiState.update { it.copy(isSaving = true) }
      val state = _uiState.value
      updateProfile.updateFirstName(state.firstName)
      updateProfile.updateBaseCalories(state.baseCalories)
      updateProfile.updateGoal(state.goal)
      updateProfile.updateCycleLength(state.cycleLengthInput.toIntOrNull() ?: 28)
      updateProfile.updateWeight(state.weightInput.replace(',', '.').toFloatOrNull())
      updateProfile.updateAgeYears(state.ageInput.toIntOrNull())
      updateProfile.updateHeightCm(state.heightInput.toIntOrNull())
      updateProfile.updateTcaFriendlyMode(state.friendlyModeEnabled)
      updateProfile.updateNotifications(state.notificationsEnabled)
      updateProfile.completeOnboarding()
      onCompleted()
    }
  }
}