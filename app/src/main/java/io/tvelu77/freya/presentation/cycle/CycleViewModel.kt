package io.tvelu77.freya.presentation.cycle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.tvelu77.freya.domain.models.CycleEntry
import io.tvelu77.freya.domain.models.PhaseInfo
import io.tvelu77.freya.domain.models.PhaseType
import io.tvelu77.freya.domain.ports.api.PhaseCalculator
import io.tvelu77.freya.domain.ports.api.TrackCycleUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

data class CycleUiState(
  val isLoading: Boolean = true,
  val currentPhase: PhaseInfo? = null,
  val cycleHistory: List<CycleEntry> = emptyList(),
  val nextCycles: List<LocalDate> = emptyList(),
  val selectedMonth: YearMonth = YearMonth.now(),
  val selectedDate: LocalDate? = null,
  val showStartCycleDialog: Boolean = false,
  val error: String? = null
)

@HiltViewModel
class CycleViewModel @Inject constructor(
  private val trackCycleUseCase: TrackCycleUseCase,
  private val phaseCalculator: PhaseCalculator
) : ViewModel() {

  private val _uiState = MutableStateFlow(CycleUiState())
  val uiState: StateFlow<CycleUiState> = _uiState.asStateFlow()

  init {
    loadCycleData()
  }

  private fun loadCycleData() {
    viewModelScope.launch {
      combine(
        trackCycleUseCase.getCurrentPhase(),
        trackCycleUseCase.getCycleHistory(),
        trackCycleUseCase.getNextCyclePredictions()
      ) { phase, history, predictions ->
        Triple(phase, history, predictions)
      }.catch { e ->
        _uiState.update { it.copy(isLoading = false, error = e.message) }
      }.collect { (phase, history, predictions) ->
        _uiState.update {
          it.copy(
            isLoading = false,
            currentPhase = phase,
            cycleHistory = history,
            nextCycles = predictions
          )
        }
      }
    }
  }

  fun onDateSelected(date: LocalDate) {
    _uiState.update { it.copy(selectedDate = date) }
  }

  fun onMonthChanged(month: YearMonth) {
    _uiState.update { it.copy(selectedMonth = month) }
  }

  fun onStartCycleClicked() {
    _uiState.update { it.copy(showStartCycleDialog = true) }
  }

  fun onStartCycleConfirmed(date: LocalDate) {
    viewModelScope.launch {
      trackCycleUseCase.startCycle(date)
      _uiState.update { it.copy(showStartCycleDialog = false) }
    }
  }

  fun onDismissDialog() {
    _uiState.update { it.copy(showStartCycleDialog = false) }
  }

  fun getPhaseForDate(date: LocalDate): PhaseType? {
    val latestCycle = _uiState.value.cycleHistory.firstOrNull() ?: return null
    return try {
      phaseCalculator.getCurrentPhase(latestCycle, date).phase
    } catch (e: Exception) {
      null
    }
  }

  fun isPredictedCycleStart(date: LocalDate): Boolean =
    _uiState.value.nextCycles.any { it == date }
}