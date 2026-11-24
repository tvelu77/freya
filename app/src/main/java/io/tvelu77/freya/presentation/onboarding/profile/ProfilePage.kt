package io.tvelu77.freya.presentation.onboarding.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import io.tvelu77.freya.R
import io.tvelu77.freya.presentation.onboarding.components.ContentCard
import io.tvelu77.freya.presentation.onboarding.components.OnboardingFooter
import io.tvelu77.freya.presentation.onboarding.components.WelcomeCard

@Composable
fun ProfilePage(
  currentStep: Int,
  name: String,
  onNameChanged: (String) -> Unit,
  onNext: () -> Unit
) {
  Column(modifier = Modifier.fillMaxSize()) {
    WelcomeCard(
      emoji = "👋",
      title = stringResource(R.string.onboarding_profile_welcome),
      backgroundColor = Color(0xFFF3E5F5),
      subtitleColor = Color(0xFF4A148C)
    )
    ContentCard(
      title = stringResource(R.string.onboarding_profile_title),
      subtitle = stringResource(R.string.onboarding_profile_sub)
    ) {
      NameInput(
        value = name,
        onValueChange = onNameChanged
      )
      Text(
        text = stringResource(R.string.onboarding_optional),
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