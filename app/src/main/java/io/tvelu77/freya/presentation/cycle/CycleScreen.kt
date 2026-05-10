package io.tvelu77.freya.presentation.cycle

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.tvelu77.freya.domain.models.PhaseType
import io.tvelu77.freya.presentation.cycle.dialogs.EndPeriodDialog
import io.tvelu77.freya.presentation.cycle.dialogs.StartCycleDialog
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CycleScreen(
  viewModel: CycleViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val getPhaseForDate: (LocalDate) -> PhaseType? by remember(uiState.cycleHistory) {
    derivedStateOf {
      { date: LocalDate ->
        viewModel.getPhaseForDate(date)
      }
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text("Mon cycle", style = MaterialTheme.typography.titleLarge)
        },
        actions = {
          IconButton(
            onClick = {
              if (uiState.currentPhase?.phase == PhaseType.MENSTRUAL) {
                viewModel.onEndPeriodClicked()
              } else {
                viewModel.onStartCycleClicked()
              }
            }
          ) {
            Icon(
              imageVector = if (uiState.currentPhase?.phase == PhaseType.MENSTRUAL)
                Icons.Rounded.Check
              else
                Icons.Rounded.Add,
              contentDescription = if (uiState.currentPhase?.phase == PhaseType.MENSTRUAL)
                "Fin des règles"
              else
                "Nouveau cycle"
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    }
  ) { padding ->
    if (uiState.isLoading) {
      Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
      }
      return@Scaffold
    }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .verticalScroll(rememberScrollState())
    ) {

      CycleCalendar(
        selectedMonth = uiState.selectedMonth,
        selectedDate = uiState.selectedDate,
        onDateSelected = viewModel::onDateSelected,
        onMonthChanged = viewModel::onMonthChanged,
        getPhaseForDate = getPhaseForDate,
        isPredictedStart = viewModel::isPredictedCycleStart
      )

      HorizontalDivider(
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outlineVariant
      )

      uiState.currentPhase?.let { phase ->
        CurrentPhaseSection(phase = phase)
      }

      if (uiState.nextCycles.isNotEmpty()) {
        NextCyclesSection(nextCycles = uiState.nextCycles)
      }

      PhaseLegendSection()

      if (uiState.cycleHistory.isNotEmpty()) {
        CycleHistorySection(cycles = uiState.cycleHistory)
      }

      Spacer(Modifier.height(24.dp))
    }
  }

  if (uiState.showStartCycleDialog) {
    StartCycleDialog(
      onConfirm = viewModel::onStartCycleConfirmed,
      onDismiss = viewModel::onDismissDialog
    )
  }

  if (uiState.showEndPeriodDialog) {
    EndPeriodDialog(
      onConfirm = viewModel::onEndPeriodConfirmed,
      onDismiss = viewModel::onDismissEndPeriodDialog
    )
  }
}