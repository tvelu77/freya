package io.tvelu77.freya.presentation.onboarding.target

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import io.tvelu77.freya.presentation.onboarding.components.ContentCard
import io.tvelu77.freya.presentation.onboarding.components.OnboardingFooter
import io.tvelu77.freya.presentation.onboarding.components.WelcomeCard

@Composable
fun TargetPage(
  currentStep: Int,
  calories: Int,
  onCaloriesChanged: (Int) -> Unit,
  onNext: () -> Unit
) {
  Column(modifier = Modifier.fillMaxSize()) {
    WelcomeCard(
      emoji = "🍽️",
      title = "Ton énergie quotidienne",
      backgroundColor = Color(0xFFFFF8E1),
      subtitleColor = Color(0xFFE65100)
    )
    ContentCard(
      title = "Objectif calorique",
      subtitle = "Pas d'inquiétude si tu ne sais pas exactement — " +
              "2000 kcal est une bonne base pour commencer."
    ) {
      Text(
        text = "$calories kcal",
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
        text = "Minimum recommandé : 1 200 kcal",
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