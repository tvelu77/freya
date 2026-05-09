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
import io.tvelu77.freya.presentation.onboarding.components.ContentCard
import io.tvelu77.freya.presentation.onboarding.components.OnboardingFooter
import io.tvelu77.freya.presentation.onboarding.components.WelcomeCard

@Composable
fun DisclaimerPage(currentStep: Int, onNext: () -> Unit) {
  Column(modifier = Modifier.fillMaxSize()) {
    WelcomeCard(
      emoji = "🌸",
      title = "Bienvenue",
      backgroundColor = Color(0xFFFCE4EC),
      subtitleColor = Color(0xFF880E4F)
    )
    ContentCard(
      title = "Freya, ton espace à toi",
      subtitle = "Une appli bienveillante, sans jugement."
    ) {
      DisclaimerBox()
    }
    Spacer(Modifier.weight(1f))
    OnboardingFooter(
      modifier = Modifier
        .windowInsetsPadding(WindowInsets.navigationBars),
      totalSteps = 4,
      currentStep = currentStep,
      buttonLabel = "Commencer →",
      onNext = onNext
    )
  }
}