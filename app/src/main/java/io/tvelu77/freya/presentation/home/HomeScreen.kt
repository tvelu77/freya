package io.tvelu77.freya.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.tvelu77.freya.R
import io.tvelu77.freya.domain.models.DailyEnergyNeed
import io.tvelu77.freya.domain.models.EnergyTarget
import io.tvelu77.freya.domain.models.PhaseInfo
import io.tvelu77.freya.domain.models.PhaseType
import io.tvelu77.freya.domain.models.TCAAlert
import io.tvelu77.freya.presentation.cycle.CycleCalendar
import io.tvelu77.freya.ui.components.HealthScoreCard
import io.tvelu77.freya.ui.components.IllustrationPlaceholder
import io.tvelu77.freya.ui.components.NutritionAdviceCard
import io.tvelu77.freya.ui.components.PhaseChip
import io.tvelu77.freya.ui.theme.Amber100
import io.tvelu77.freya.ui.theme.phaseGradientColors
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
  onNavigateToCycle: () -> Unit,
  onNavigateToFood: () -> Unit,
  onNavigateToResources: () -> Unit = {},
  viewModel: HomeViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  Scaffold(
    containerColor = MaterialTheme.colorScheme.background
  ) { padding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
    ) {
      when {
        uiState.isLoading -> HomeLoadingState()
        uiState.error != null -> HomeErrorState(
          message = uiState.error!!,
          onRetry = viewModel::refresh
        )
        else -> HomeContent(
          uiState = uiState,
          onStartCycle = viewModel::startNewCycle,
          onDismissTcaAlert = viewModel::dismissTcaAlert,
          onNavigateToCycle = onNavigateToCycle,
          onNavigateToFood = onNavigateToFood,
          onNavigateToResources = onNavigateToResources,
          onDateSelected = viewModel::onDateSelected,
          onMonthChanged = viewModel::onMonthChanged,
          phaseForDate = viewModel::phaseForDate,
          isPredictedStart = viewModel::isPredictedCycleStart
        )
      }
    }
  }
}

@Composable
private fun HomeContent(
  uiState: HomeUiState,
  onStartCycle: () -> Unit,
  onDismissTcaAlert: () -> Unit,
  onNavigateToCycle: () -> Unit,
  onNavigateToFood: () -> Unit,
  onNavigateToResources: () -> Unit,
  onDateSelected: (LocalDate) -> Unit,
  onMonthChanged: (YearMonth) -> Unit,
  phaseForDate: (LocalDate) -> PhaseType?,
  isPredictedStart: (LocalDate) -> Boolean
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
  ) {

    HomeHeader(
      uiState = uiState,
      date = uiState.currentDate,
      phaseInfo = uiState.phaseInfo,
    )

    Column(
      modifier = Modifier.padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

      AnimatedVisibility(
        visible = uiState.tcaAlert?.triggered == true,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
      ) {
        uiState.tcaAlert?.let { alert ->
          TCAAlertBanner(
            alert = alert,
            onDismiss = onDismissTcaAlert,
            onSeeResources = onNavigateToResources
          )
        }
      }

      if (!uiState.hasActiveCycle) {
        NoCycleCard(onStartCycle = onStartCycle)
      }

      uiState.phaseInfo?.let { phase ->
        PhaseCard(phaseInfo = phase, onClick = onNavigateToCycle)
      }

      TodaySummaryCard(
        date = uiState.currentDate,
        phase = uiState.phaseInfo?.phase,
        calories = uiState.todayCalories,
        energyTarget = uiState.energyTarget,
        energyNeed = uiState.energyNeed,
        showCalories = uiState.showCalories
      )

      uiState.nutritionAdvice?.let { advice ->
        NutritionAdviceCard(advice = advice)
      }

      uiState.healthScore?.let { score ->
        HealthScoreCard(
          score = score,
          onClick = onNavigateToFood,
          showNumber = uiState.showHealthScoreNumber
        )
      }

      if (uiState.hasActiveCycle) {
        CycleCalendar(
          selectedMonth = uiState.selectedMonth,
          selectedDate = uiState.selectedDate,
          onDateSelected = onDateSelected,
          onMonthChanged = onMonthChanged,
          getPhaseForDate = phaseForDate,
          isPredictedStart = isPredictedStart
        )
        SelectedDayKpis(
          date = uiState.selectedDate,
          phase = uiState.selectedPhase?.phase,
          calories = uiState.selectedDayCalories,
          energyTarget = uiState.energyTarget,
          showCalories = uiState.showCalories
        )
      }

      Spacer(Modifier.height(24.dp))
    }
  }
}

@Composable
private fun SelectedDayKpis(
  date: LocalDate,
  phase: PhaseType?,
  calories: Int,
  energyTarget: EnergyTarget,
  showCalories: Boolean
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant
    ),
    shape = MaterialTheme.shapes.large
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = date.format(DateTimeFormatter.ofPattern("d MMMM", Locale.FRENCH))
          .replaceFirstChar { it.uppercase() },
        style = MaterialTheme.typography.titleSmall
      )
      Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        phase?.let { PhaseChip(phase = it) }
        if (showCalories) {
          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = stringResource(R.string.home_kcal, calories),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = stringResource(
                R.string.energy_target_range,
                energyTarget.low,
                energyTarget.high
              ),
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }
  }
}

@Composable
private fun HomeHeader(uiState: HomeUiState, date: String, phaseInfo: PhaseInfo?) {
  val gradientColors = phaseGradientColors(phaseInfo?.phase)
  val textPrimary = MaterialTheme.colorScheme.onSurface
  val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .background(Brush.verticalGradient(gradientColors))
      .padding(horizontal = 20.dp, vertical = 28.dp)
  ) {
    Column {
      Text(
        text = date,
        style = MaterialTheme.typography.bodyMedium,
        color = textSecondary
      )
      Spacer(Modifier.height(4.dp))
      Text(
        text = if (uiState.greetingName.isNotBlank())
          stringResource(R.string.home_greeting_named, uiState.greetingName)
        else
          stringResource(R.string.home_greeting),
        style = MaterialTheme.typography.headlineLarge,
        color = textPrimary
      )
      phaseInfo?.let {
        Spacer(Modifier.height(8.dp))
        PhaseChip(phase = it.phase)
      }
    }
  }
  Spacer(Modifier.height(16.dp))
}

@Composable
private fun TodaySummaryCard(
  date: String,
  phase: PhaseType?,
  calories: Int,
  energyTarget: EnergyTarget,
  energyNeed: DailyEnergyNeed?,
  showCalories: Boolean
) {
  val message = if (energyNeed != null && energyNeed.totalKcal > energyTarget.high) {
    stringResource(R.string.energy_need_higher)
  } else {
    stringResource(R.string.energy_need_ok)
  }

  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.tertiaryContainer
    ),
    shape = MaterialTheme.shapes.large
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = date,
          style = MaterialTheme.typography.titleSmall
        )
        phase?.let { PhaseChip(phase = it) }
      }
      Spacer(Modifier.height(8.dp))
      if (showCalories) {
        Text(
          text = stringResource(R.string.home_kcal, calories),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onTertiaryContainer
        )
        Text(
          text = stringResource(
            R.string.energy_target_range,
            energyTarget.low,
            energyTarget.high
          ),
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
        )
        energyNeed?.let { need ->
          Spacer(Modifier.height(4.dp))
          Text(
            text = stringResource(R.string.energy_need_value, need.totalKcal),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onTertiaryContainer
          )
        }
        Spacer(Modifier.height(8.dp))
      }
      Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium
      )
      Spacer(Modifier.height(4.dp))
      Text(
        text = stringResource(R.string.energy_need_hint),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
      )
    }
  }
}

@Composable
private fun PhaseCard(
  phaseInfo: PhaseInfo,
  onClick: () -> Unit
) {
  Card(
    onClick = onClick,
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer
    ),
    shape = MaterialTheme.shapes.large
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = stringResource(R.string.home_day_of_cycle, phaseInfo.daysInCycle),
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
        Spacer(Modifier.height(4.dp))
        Text(
          text = phaseInfo.phase.displayName,
          style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(6.dp))
        PhaseChip(phase = phaseInfo.phase)
      }
      Icon(
        imageVector = Icons.Rounded.ChevronRight,
        contentDescription = stringResource(R.string.home_phase_detail_cd),
        tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
      )
    }
  }
}

@Composable
private fun NoCycleCard(onStartCycle: () -> Unit) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.tertiaryContainer
    ),
    shape = MaterialTheme.shapes.large
  ) {
    Column(
      modifier = Modifier.padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      IllustrationPlaceholder(emoji = "🌸")
      Spacer(Modifier.height(8.dp))
      Text(
        text = stringResource(R.string.home_no_cycle_title),
        style = MaterialTheme.typography.titleMedium,
        textAlign = TextAlign.Center
      )
      Spacer(Modifier.height(4.dp))
      Text(
        text = stringResource(R.string.home_no_cycle_body),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f),
        textAlign = TextAlign.Center
      )
      Spacer(Modifier.height(16.dp))
      Button(onClick = onStartCycle) {
        Text(stringResource(R.string.home_no_cycle_cta))
      }
    }
  }
}

@Composable
private fun TCAAlertBanner(
  alert: TCAAlert,
  onDismiss: () -> Unit,
  onSeeResources: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = Amber100),
    shape = MaterialTheme.shapes.large
  ) {
    Row(
      modifier = Modifier.padding(16.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalAlignment = Alignment.Top
    ) {
      Text("💛", style = MaterialTheme.typography.titleMedium)
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = stringResource(R.string.home_tca_reminder_title),
          style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(4.dp))
        Text(
          text = alert.message,
          style = MaterialTheme.typography.bodyMedium
        )
        if (alert.ctaLabel.isNotEmpty()) {
          Spacer(Modifier.height(8.dp))
          TextButton(onClick = onSeeResources) {
            Text(stringResource(R.string.tca_alert_cta))
          }
        }
      }
      IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
        Icon(
          imageVector = Icons.Rounded.Close,
          contentDescription = stringResource(R.string.home_close_cd),
          modifier = Modifier.size(16.dp)
        )
      }
    }
  }
}

@Composable
private fun HomeLoadingState() {
  Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
      Spacer(Modifier.height(16.dp))
      Text(
        text = stringResource(R.string.home_loading),
        style = MaterialTheme.typography.bodyMedium
      )
    }
  }
}

@Composable
private fun HomeErrorState(message: String, onRetry: () -> Unit) {
  Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.padding(32.dp)
    ) {
      Text("😔", style = MaterialTheme.typography.headlineLarge)
      Spacer(Modifier.height(8.dp))
      Text(
        text = stringResource(R.string.home_error_title),
        style = MaterialTheme.typography.titleMedium,
        textAlign = TextAlign.Center
      )
      Spacer(Modifier.height(4.dp))
      Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
      )
      Spacer(Modifier.height(16.dp))
      Button(onClick = onRetry) { Text(stringResource(R.string.home_error_retry)) }
    }
  }
}