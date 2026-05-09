package io.tvelu77.freya.presentation.onboarding.preferences

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.tvelu77.freya.presentation.onboarding.components.ContentCard
import io.tvelu77.freya.presentation.onboarding.components.OnboardingFooter
import io.tvelu77.freya.presentation.onboarding.components.WelcomeCard
import io.tvelu77.freya.ui.components.ToggleRow

@Composable
fun PreferencesPage(
  currentStep: Int,
  friendlyModeEnabled: Boolean,
  notificationsEnabled: Boolean,
  onFriendlyModeToggled: (Boolean) -> Unit,
  onNotificationsToggled: (Boolean) -> Unit,
  onNext: () -> Unit
) {
  Column(modifier = Modifier.fillMaxSize()) {
    WelcomeCard(
      emoji = "💚",
      title = "Ton confort avant tout",
      backgroundColor = Color(0xFFE8F5E9),
      subtitleColor = Color(0xFF1B5E20)
    )
    ContentCard(
      title = "Mode bienveillant",
      subtitle = "Tu peux masquer tous les chiffres et te concentrer " +
              "uniquement sur le ressenti. Modifiable à tout moment."
    ) {
      ToggleRow(
        title = "Masquer les calories",
        subtitle = "Focus sur le ressenti",
        checked = friendlyModeEnabled,
        onCheckedChange = onFriendlyModeToggled,
        icon = Icons.Rounded.VisibilityOff
      )
      HorizontalDivider(
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outlineVariant
      )
      ToggleRow(
        title = "Notifications douces",
        subtitle = "Rappels et conseils bienveillants",
        checked = notificationsEnabled,
        onCheckedChange = onNotificationsToggled,
        icon = Icons.Rounded.Notifications
      )
    }
    Spacer(Modifier.weight(1f))
    OnboardingFooter(
      modifier = Modifier
        .windowInsetsPadding(WindowInsets.navigationBars),
      totalSteps = 4,
      currentStep = currentStep,
      buttonLabel = "C'est parti 🌸",
      onNext = onNext
    )
  }
}