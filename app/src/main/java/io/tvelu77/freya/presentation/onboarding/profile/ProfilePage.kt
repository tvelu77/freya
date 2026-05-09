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
      title = "Faisons connaissance",
      backgroundColor = Color(0xFFF3E5F5),
      subtitleColor = Color(0xFF4A148C)
    )
    ContentCard(
      title = "Comment tu t'appelles ?",
      subtitle = "Juste ton prénom pour personnaliser ton expérience. " +
              "Tu pourras le changer à tout moment."
    ) {
      NameInput(
        value = name,
        onValueChange = onNameChanged
      )
      Text(
        text = "Optionnel — tu peux laisser vide",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
    Spacer(Modifier.weight(1f))
    OnboardingFooter(
      modifier = Modifier
        .windowInsetsPadding(WindowInsets.navigationBars),
      totalSteps = 4,
      currentStep = currentStep,
      buttonLabel = "Continuer →",
      onNext = onNext
    )
  }
}