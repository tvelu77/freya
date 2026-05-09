package io.tvelu77.freya.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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
      updateProfile.updateTcaFriendlyMode(state.friendlyModeEnabled)
      updateProfile.updateNotifications(state.notificationsEnabled)
      updateProfile.completeOnboarding()
      onCompleted()
    }
  }
}