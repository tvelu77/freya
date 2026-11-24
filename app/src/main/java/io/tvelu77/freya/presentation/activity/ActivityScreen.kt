package io.tvelu77.freya.presentation.activity

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DirectionsRun
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.tvelu77.freya.R
import io.tvelu77.freya.domain.models.ActivityEntry
import io.tvelu77.freya.domain.models.DailyActivity
import io.tvelu77.freya.domain.models.DailyHydration
import io.tvelu77.freya.domain.models.HydrationGoal
import io.tvelu77.freya.domain.models.SportType
import io.tvelu77.freya.ui.components.ActivityAdviceCard
import io.tvelu77.freya.ui.components.SoftEmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(
  viewModel: ActivityViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = stringResource(R.string.activity_title),
            style = MaterialTheme.typography.titleLarge
          )
        },
        actions = {
          IconButton(onClick = viewModel::onAddClicked) {
            Icon(
              Icons.Rounded.Add,
              contentDescription = stringResource(R.string.activity_add_cd)
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
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Spacer(Modifier.height(32.dp))
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
      }
      return@Scaffold
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding),
      contentPadding = PaddingValues(bottom = 24.dp)
    ) {
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
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
            Text(
              text = stringResource(R.string.activity_title),
              style = MaterialTheme.typography.titleMedium
            )
            if (uiState.showCalories) {
              Text(
                text = stringResource(R.string.activity_total_burned, uiState.totalBurned),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
              )
            }
          }
        }
      }

      uiState.activityAdvice?.let { advice ->
        item {
          ActivityAdviceCard(
            advice = advice,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
          )
        }
      }

      item {
        DailyActivityCard(
          dailyActivity = uiState.dailyActivity,
          onSave = viewModel::saveDailyActivity,
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )
      }

      item {
        HydrationCard(
          hydration = uiState.hydration,
          goal = uiState.hydrationGoal,
          onAddWater = viewModel::addWater,
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )
      }

      if (uiState.activities.isEmpty()) {
        item {
          SoftEmptyState(
            emoji = "🌿",
            title = stringResource(R.string.activity_empty_title),
            subtitle = stringResource(R.string.activity_empty_subtitle),
            modifier = Modifier.padding(horizontal = 16.dp)
          )
        }
      } else {
        items(uiState.activities, key = { it.id }) { entry ->
          ActivityEntryRow(
            entry = entry,
            showCalories = uiState.showCalories,
            onDelete = { viewModel.deleteActivity(entry.id) }
          )
        }
      }

      item {
        Button(
          onClick = viewModel::onAddClicked,
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(52.dp),
          shape = RoundedCornerShape(16.dp)
        ) {
          Icon(Icons.Rounded.Add, contentDescription = null)
          Spacer(Modifier.width(8.dp))
          Text(stringResource(R.string.activity_add_button))
        }
      }
    }
  }

  if (uiState.showAddDialog) {
    AddActivityDialog(
      onConfirm = viewModel::logActivity,
      onDismiss = viewModel::onDismissDialog
    )
  }
}

@Composable
private fun DailyActivityCard(
  dailyActivity: DailyActivity?,
  onSave: (Int, Float) -> Unit,
  modifier: Modifier = Modifier
) {
  var steps by remember(dailyActivity) {
    mutableStateOf(dailyActivity?.steps?.takeIf { it > 0 }?.toString() ?: "")
  }
  var sleep by remember(dailyActivity) {
    mutableStateOf(dailyActivity?.sleepHours?.takeIf { it > 0f }?.toString() ?: "")
  }

  Card(
    modifier = modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    shape = MaterialTheme.shapes.large
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = stringResource(R.string.activity_daily_title),
        style = MaterialTheme.typography.titleMedium
      )
      Spacer(Modifier.height(12.dp))
      OutlinedTextField(
        value = steps,
        onValueChange = { steps = it.filter { c -> c.isDigit() } },
        label = { Text(stringResource(R.string.activity_steps_label)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
      )
      Spacer(Modifier.height(8.dp))
      OutlinedTextField(
        value = sleep,
        onValueChange = {
          sleep = it.filter { c -> c.isDigit() || c == '.' || c == ',' }.replace(',', '.')
        },
        label = { Text(stringResource(R.string.activity_sleep_label)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
      )
      Spacer(Modifier.height(12.dp))
      Button(
        onClick = { onSave(steps.toIntOrNull() ?: 0, sleep.toFloatOrNull() ?: 0f) },
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp),
        shape = RoundedCornerShape(16.dp)
      ) {
        Text(stringResource(R.string.activity_save))
      }
    }
  }
}

@Composable
private fun HydrationCard(
  hydration: DailyHydration?,
  goal: HydrationGoal,
  onAddWater: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  val consumed = hydration?.waterMl ?: 0
  val progress = (consumed.toFloat() / goal.targetMl).coerceIn(0f, 1f)

  Card(
    modifier = modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    shape = MaterialTheme.shapes.large
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = stringResource(R.string.hydration_title),
        style = MaterialTheme.typography.titleMedium
      )
      Spacer(Modifier.height(8.dp))
      Text(
        text = stringResource(R.string.hydration_progress, consumed, goal.targetMl),
        style = MaterialTheme.typography.bodyMedium
      )
      Spacer(Modifier.height(8.dp))
      LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.secondaryContainer
      )
      Spacer(Modifier.height(6.dp))
      Text(
        text = stringResource(R.string.hydration_hint),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(Modifier.height(12.dp))
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(
          onClick = { onAddWater(250) },
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text(stringResource(R.string.hydration_glass))
        }
        OutlinedButton(
          onClick = { onAddWater(500) },
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text(stringResource(R.string.hydration_bottle))
        }
      }
    }
  }
}

@Composable
private fun ActivityEntryRow(
  entry: ActivityEntry,
  showCalories: Boolean,
  onDelete: () -> Unit
) {
  var showDeleteConfirm by remember { mutableStateOf(false) }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
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
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Icon(
          Icons.Rounded.DirectionsRun,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary
        )
        Column {
          Text(entry.sportType.displayName, style = MaterialTheme.typography.bodyMedium)
          Text(
            text = stringResource(R.string.activity_duration_min, entry.durationMin),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        if (showCalories) {
          Text(
            text = stringResource(R.string.activity_kcal_burned, entry.kcalBurned),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        IconButton(onClick = { showDeleteConfirm = true }, modifier = Modifier.size(28.dp)) {
          Icon(
            Icons.Rounded.Delete,
            contentDescription = stringResource(R.string.action_delete),
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
          )
        }
      }
    }
  }

  if (showDeleteConfirm) {
    AlertDialog(
      onDismissRequest = { showDeleteConfirm = false },
      title = { Text(stringResource(R.string.activity_delete_title)) },
      confirmButton = {
        TextButton(onClick = { onDelete(); showDeleteConfirm = false }) {
          Text(stringResource(R.string.action_delete))
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteConfirm = false }) {
          Text(stringResource(R.string.action_cancel))
        }
      }
    )
  }
}

@Composable
private fun AddActivityDialog(
  onConfirm: (SportType, Int) -> Unit,
  onDismiss: () -> Unit
) {
  var selectedSport by remember { mutableStateOf(SportType.WALKING) }
  var duration by remember { mutableStateOf("30") }

  AlertDialog(
    onDismissRequest = onDismiss,
    icon = { Text("🏃", style = MaterialTheme.typography.headlineMedium) },
    title = {
      Text(
        text = stringResource(R.string.dialog_activity_title),
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center
      )
    },
    text = {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = stringResource(R.string.activity_sport_label),
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
          SportType.entries.forEach { sport ->
            FilterChip(
              selected = selectedSport == sport,
              onClick = { selectedSport = sport },
              label = {
                Text(sport.displayName, style = MaterialTheme.typography.labelSmall)
              }
            )
          }
        }
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
          value = duration,
          onValueChange = { duration = it.filter { c -> c.isDigit() } },
          label = { Text(stringResource(R.string.activity_duration_label)) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          shape = RoundedCornerShape(12.dp)
        )
      }
    },
    confirmButton = {
      Button(
        onClick = { onConfirm(selectedSport, duration.toIntOrNull() ?: 0) },
        enabled = (duration.toIntOrNull() ?: 0) > 0
      ) {
        Text(stringResource(R.string.food_add_button))
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(stringResource(R.string.action_cancel))
      }
    }
  )
}
