package io.tvelu77.freya.presentation.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.tvelu77.freya.domain.models.ActivityEntry
import io.tvelu77.freya.domain.models.FatigueLevel
import io.tvelu77.freya.domain.models.SportType
import io.tvelu77.freya.domain.ports.api.GetUserProfileUseCase
import io.tvelu77.freya.domain.ports.api.LogActivityUseCase
import io.tvelu77.freya.domain.ports.api.TrackDailyActivityUseCase
import io.tvelu77.freya.domain.ports.api.TrackHydrationUseCase
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
  private val trackDailyActivityUseCase: TrackDailyActivityUseCase,
  private val trackHydrationUseCase: TrackHydrationUseCase,
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
    viewModelScope.launch {
      trackDailyActivityUseCase.getDailyActivity(today).collect { daily ->
        _uiState.update { it.copy(dailyActivity = daily) }
      }
    }
    viewModelScope.launch {
      trackDailyActivityUseCase.getActivityAdviceForToday(today).collect { advice ->
        _uiState.update { it.copy(activityAdvice = advice) }
      }
    }
    viewModelScope.launch {
      trackHydrationUseCase.getHydrationForDay(today).collect { hydration ->
        _uiState.update { it.copy(hydration = hydration) }
      }
    }
    viewModelScope.launch {
      trackHydrationUseCase.getGoalForDay(today).collect { goal ->
        _uiState.update { it.copy(hydrationGoal = goal) }
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

  private var lastDeletedActivity: ActivityEntry? = null

  fun deleteActivity(id: Long) {
    viewModelScope.launch {
      lastDeletedActivity = _uiState.value.activities.firstOrNull { it.id == id }
      logActivityUseCase.deleteActivity(id)
    }
  }

  fun undoDeleteActivity() {
    viewModelScope.launch {
      lastDeletedActivity?.let { logActivityUseCase.updateActivity(it) }
      lastDeletedActivity = null
    }
  }

  fun updateActivityDuration(id: Long, durationMin: Int) {
    viewModelScope.launch {
      val entry = _uiState.value.activities.firstOrNull { it.id == id } ?: return@launch
      logActivityUseCase.updateActivity(
        entry.copy(
          durationMin = durationMin,
          kcalBurned = ActivityEntry.computeKcal(entry.sportType, durationMin, weightKg)
        )
      )
    }
  }

  fun addSteps(delta: Int) {
    viewModelScope.launch {
      val current = _uiState.value.dailyActivity?.steps ?: 0
      trackDailyActivityUseCase.updateSteps(current + delta, today)
    }
  }

  fun saveDailyActivity(steps: Int, sleepHours: Float, fatigue: FatigueLevel?) {
    viewModelScope.launch {
      trackDailyActivityUseCase.updateSteps(steps, today)
      trackDailyActivityUseCase.updateSleep(sleepHours, today)
      trackDailyActivityUseCase.updateFatigue(fatigue, today)
    }
  }

  fun addWater(ml: Int) {
    viewModelScope.launch { trackHydrationUseCase.addWater(ml, today) }
  }
}
