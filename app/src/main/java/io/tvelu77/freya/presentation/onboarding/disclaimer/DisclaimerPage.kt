package io.tvelu77.freya.presentation.onboarding.disclaimer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import io.tvelu77.freya.R
import io.tvelu77.freya.presentation.onboarding.components.ContentCard
import io.tvelu77.freya.presentation.onboarding.components.OnboardingFooter
import io.tvelu77.freya.presentation.onboarding.components.WelcomeCard

@Composable
fun DisclaimerPage(currentStep: Int, onNext: () -> Unit) {
  Column(modifier = Modifier.fillMaxSize()) {
    WelcomeCard(
      emoji = "🌸",
      title = stringResource(R.string.onboarding_disclaimer_welcome),
      backgroundColor = Color(0xFFFCE4EC),
      subtitleColor = Color(0xFF880E4F)
    )
    ContentCard(
      title = stringResource(R.string.onboarding_app_tagline),
      subtitle = stringResource(R.string.onboarding_app_sub)
    ) {
      DisclaimerBox()
    }
    Spacer(Modifier.weight(1f))
    OnboardingFooter(
      modifier = Modifier
        .windowInsetsPadding(WindowInsets.navigationBars),
      totalSteps = 5,
      currentStep = currentStep,
      buttonLabel = stringResource(R.string.onboarding_start),
      onNext = onNext
    )
  }
}