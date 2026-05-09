package io.tvelu77.freya.presentation.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import io.tvelu77.freya.ui.theme.Rose300

@Composable
fun StepIndicator(
  totalSteps: Int,
  currentStep: Int,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(6.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    repeat(totalSteps) { index ->
      val isActive = index == currentStep
      Box(
        modifier = Modifier
          .height(6.dp)
          .width(if (isActive) 18.dp else 6.dp)
          .clip(RoundedCornerShape(3.dp))
          .background(
            if (isActive) Rose300
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
          )
      )
    }
  }
}