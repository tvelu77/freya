package io.tvelu77.freya.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.tvelu77.freya.domain.ports.api.GetUserProfileUseCase
import io.tvelu77.freya.domain.ports.api.TrackHydrationUseCase
import io.tvelu77.freya.domain.ports.api.UpdateUserProfileUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class MainActivityViewModel @Inject constructor(
  private val getProfile: GetUserProfileUseCase,
  private val updateUserProfileUseCase: UpdateUserProfileUseCase,
  private val trackHydrationUseCase: TrackHydrationUseCase
): ViewModel() {

  init {
    viewModelScope.launch {
      getProfile.execute().collect {
        trackHydrationUseCase.rescheduleHydrationReminders()
      }
    }
  }

  val isOnboardingCompleted: StateFlow<Boolean?> = getProfile
    .execute()
    .map { it.onboardingCompleted }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = null
    )

  fun completeOnboarding() {
    viewModelScope.launch {
      updateUserProfileUseCase.completeOnboarding()
    }
  }

}