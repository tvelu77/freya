package io.tvelu77.freya.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.tvelu77.freya.domain.models.HealthScore
import io.tvelu77.freya.domain.models.NutritionAdvice
import io.tvelu77.freya.domain.models.PhaseInfo
import io.tvelu77.freya.domain.models.TCAAlert
import io.tvelu77.freya.domain.ports.api.GetHealthScoreUseCase
import io.tvelu77.freya.domain.ports.api.GetNutritionAdviceUseCase
import io.tvelu77.freya.domain.ports.api.TCAGuardUseCase
import io.tvelu77.freya.domain.ports.api.TrackCycleUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

data class HomeUiState(
  val isLoading: Boolean = true,
  val greetingName: String = "",
  val currentDate: String = "",
  val phaseInfo: PhaseInfo? = null,
  val nutritionAdvice: NutritionAdvice? = null,
  val healthScore: HealthScore? = null,
  val tcaAlert: TCAAlert? = null,
  val hasActiveCycle: Boolean = false,
  val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
  private val trackCycleUseCase: TrackCycleUseCase,
  private val getNutritionAdviceUseCase: GetNutritionAdviceUseCase,
  private val getHealthScoreUseCase: GetHealthScoreUseCase,
  private val tcaGuardUseCase: TCAGuardUseCase
) : ViewModel() {

  private val _uiState = MutableStateFlow(HomeUiState())
  val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

  private val today = LocalDate.now()

  init {
    loadHomeData()
  }

  private fun loadHomeData() {
    viewModelScope.launch {
      _uiState.update { it.copy(currentDate = formatDate(today)) }

      combine(
        trackCycleUseCase.getCurrentPhase(today),
        getNutritionAdviceUseCase.getAdviceForToday(today),
        getHealthScoreUseCase.getScoreForDay(today)
      ) { phase, advice, score ->
        Triple(phase, advice, score)
      }.catch { e ->
        _uiState.update { it.copy(isLoading = false, error = e.message) }
      }.collect { (phase, advice, score) ->
        _uiState.update {
          it.copy(
            isLoading = false,
            phaseInfo = phase,
            nutritionAdvice = advice,
            healthScore = score,
            hasActiveCycle = phase != null
          )
        }
      }
    }

    viewModelScope.launch {
      val alert = tcaGuardUseCase.checkLastDays()
      if (alert.triggered) {
        _uiState.update { it.copy(tcaAlert = alert) }
      }
    }
  }

  fun dismissTcaAlert() {
    _uiState.update { it.copy(tcaAlert = null) }
  }

  fun startNewCycle() {
    viewModelScope.launch {
      trackCycleUseCase.startCycle(today)
    }
  }

  fun refresh() {
    _uiState.update { it.copy(isLoading = true, error = null) }
    loadHomeData()
  }

  private fun formatDate(date: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("EEEE d MMMM", Locale.FRENCH)
    return date.format(formatter).replaceFirstChar { it.uppercase() }
  }
}