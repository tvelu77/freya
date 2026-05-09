package io.tvelu77.freya.presentation.onboarding.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.tvelu77.freya.ui.theme.Rose300

@Composable
fun OnboardingFooter(
  totalSteps: Int,
  currentStep: Int,
  buttonLabel: String,
  onNext: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 16.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    StepIndicator(
      totalSteps = totalSteps,
      currentStep = currentStep
    )

    Button(
      onClick = onNext,
      shape = RoundedCornerShape(12.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = Rose300,
        contentColor = Color.White
      )
    ) {
      Text(
        text = buttonLabel,
        style = MaterialTheme.typography.labelLarge
      )
    }
  }
}