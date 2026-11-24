package io.tvelu77.freya.presentation.onboarding.target

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.tvelu77.freya.R
import io.tvelu77.freya.domain.models.Goal
import io.tvelu77.freya.presentation.onboarding.components.ContentCard
import io.tvelu77.freya.presentation.onboarding.components.OnboardingFooter
import io.tvelu77.freya.presentation.onboarding.components.WelcomeCard

@Composable
fun TargetPage(
  currentStep: Int,
  calories: Int,
  onCaloriesChanged: (Int) -> Unit,
  goal: Goal,
  onGoalChanged: (Goal) -> Unit,
  cycleLengthInput: String,
  onCycleLengthInputChanged: (String) -> Unit,
  onNext: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
  ) {
    WelcomeCard(
      emoji = "🍽️",
      title = stringResource(R.string.onboarding_target_welcome),
      backgroundColor = Color(0xFFFFF8E1),
      subtitleColor = Color(0xFFE65100)
    )
    ContentCard(
      title = stringResource(R.string.onboarding_target_title),
      subtitle = stringResource(R.string.onboarding_target_sub)
    ) {
      Text(
        text = stringResource(R.string.onboarding_target_value, calories),
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
      )
      Slider(
        value = calories.toFloat(),
        onValueChange = { onCaloriesChanged(it.toInt()) },
        valueRange = 1200f..3000f,
        steps = 0,
        modifier = Modifier.fillMaxWidth(),
        colors = SliderDefaults.colors(
          thumbColor = MaterialTheme.colorScheme.primary,
          activeTrackColor = MaterialTheme.colorScheme.primary,
          inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
        )
      )
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          "1 200",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
          "3 000",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
      Text(
        text = stringResource(R.string.min_calories_hint),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(Modifier.height(8.dp))
      Text(
        text = stringResource(R.string.onboarding_goal_label),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Goal.entries.forEach { g ->
          FilterChip(
            selected = goal == g,
            onClick = { onGoalChanged(g) },
            label = { Text(g.displayName, style = MaterialTheme.typography.labelSmall) }
          )
        }
      }
      Spacer(Modifier.height(8.dp))
      OutlinedTextField(
        value = cycleLengthInput,
        onValueChange = onCycleLengthInputChanged,
        label = { Text(stringResource(R.string.onboarding_cycle_length_label)) },
        placeholder = { Text(stringResource(R.string.profile_cycle_length_placeholder)) },
        supportingText = { Text(stringResource(R.string.profile_cycle_length_hint)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
      )
    }
    Spacer(Modifier.weight(1f))
    OnboardingFooter(
      modifier = Modifier
        .windowInsetsPadding(WindowInsets.navigationBars),
      totalSteps = 5,
      currentStep = currentStep,
      buttonLabel = stringResource(R.string.onboarding_continue),
      onNext = onNext
    )
  }
}