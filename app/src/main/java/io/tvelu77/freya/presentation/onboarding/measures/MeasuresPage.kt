package io.tvelu77.freya.presentation.onboarding.measures

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.tvelu77.freya.R
import io.tvelu77.freya.presentation.onboarding.components.ContentCard
import io.tvelu77.freya.presentation.onboarding.components.OnboardingFooter
import io.tvelu77.freya.presentation.onboarding.components.WelcomeCard

@Composable
fun MeasuresPage(
  currentStep: Int,
  weightInput: String,
  ageInput: String,
  heightInput: String,
  onWeightInputChanged: (String) -> Unit,
  onAgeInputChanged: (String) -> Unit,
  onHeightInputChanged: (String) -> Unit,
  onNext: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
  ) {
    WelcomeCard(
      emoji = "📏",
      title = stringResource(R.string.onboarding_measures_welcome),
      backgroundColor = Color(0xFFE3F2FD),
      subtitleColor = Color(0xFF0D47A1)
    )
    ContentCard(
      title = stringResource(R.string.onboarding_measures_title),
      subtitle = stringResource(R.string.onboarding_measures_sub)
    ) {
      OutlinedTextField(
        value = weightInput,
        onValueChange = onWeightInputChanged,
        label = { Text(stringResource(R.string.profile_weight_label)) },
        placeholder = { Text(stringResource(R.string.profile_weight_placeholder)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
      )
      OutlinedTextField(
        value = ageInput,
        onValueChange = onAgeInputChanged,
        label = { Text(stringResource(R.string.profile_age_label)) },
        placeholder = { Text(stringResource(R.string.profile_age_placeholder)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
      )
      OutlinedTextField(
        value = heightInput,
        onValueChange = onHeightInputChanged,
        label = { Text(stringResource(R.string.profile_height_label)) },
        placeholder = { Text(stringResource(R.string.profile_height_placeholder)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
      )
      Text(
        text = stringResource(R.string.profile_measures_hint),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
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
