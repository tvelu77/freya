package io.tvelu77.freya.presentation.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.tvelu77.freya.domain.models.SportType
import io.tvelu77.freya.domain.ports.api.GetUserProfileUseCase
import io.tvelu77.freya.domain.ports.api.LogActivityUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

@HiltViewModel
class ActivityViewModel @Inject constructor(
  private val logActivityUseCase: LogActivityUseCase,
  private val getUserProfileUseCase: GetUserProfileUseCase
) : ViewModel() {

  private val _uiState = MutableStateFlow(ActivityUiState())
  val uiState: StateFlow<ActivityUiState> = _uiState.asStateFlow()

  private val today = LocalDate.now()
  private var weightKg: Float? = null

  init {
    viewModelScope.launch {
      getUserProfileUseCase.execute().collect { profile ->
        weightKg = profile.weightKg
        _uiState.update { it.copy(showCalories = !profile.tcaFriendlyMode) }
      }
    }
    viewModelScope.launch {
      logActivityUseCase.getActivitiesForDay(today).collect { entries ->
        _uiState.update {
          it.copy(
            isLoading = false,
            activities = entries,
            totalBurned = entries.sumOf { e -> e.kcalBurned }
          )
        }
      }
    }
  }

  fun onAddClicked() = _uiState.update { it.copy(showAddDialog = true) }

  fun onDismissDialog() = _uiState.update { it.copy(showAddDialog = false) }

  fun logActivity(sport: SportType, durationMin: Int) {
    viewModelScope.launch {
      logActivityUseCase.logActivity(sport, durationMin, weightKg, today)
      _uiState.update { it.copy(showAddDialog = false) }
    }
  }

  fun deleteActivity(id: Long) {
    viewModelScope.launch { logActivityUseCase.deleteActivity(id) }
  }
}
