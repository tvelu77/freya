package io.tvelu77.freya.presentation.cycle

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.tvelu77.freya.domain.models.PhaseType
import io.tvelu77.freya.ui.theme.ColorFollicular
import io.tvelu77.freya.ui.theme.ColorLuteal
import io.tvelu77.freya.ui.theme.ColorMenstrual
import io.tvelu77.freya.ui.theme.ColorOvulatory

@Composable
fun CalendarDay(
  day: Int,
  phase: PhaseType?,
  isToday: Boolean,
  isSelected: Boolean,
  isPredicted: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val phaseColor = when (phase) {
    PhaseType.MENSTRUAL  -> ColorMenstrual
    PhaseType.FOLLICULAR -> ColorFollicular
    PhaseType.OVULATORY  -> ColorOvulatory
    PhaseType.LUTEAL     -> ColorLuteal
    null                 -> Color.Transparent
  }

  Box(
    modifier = modifier
      .height(40.dp)
      .padding(2.dp)
      .clip(CircleShape)
      .background(
        when {
          isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
          isToday    -> MaterialTheme.colorScheme.primaryContainer
          phase != null -> phaseColor.copy(alpha = 0.2f)
          else       -> Color.Transparent
        }
      )
      .clickable(onClick = onClick),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = day.toString(),
        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
        color = when {
          isSelected -> MaterialTheme.colorScheme.onPrimary
          isToday    -> MaterialTheme.colorScheme.primary
          else       -> MaterialTheme.colorScheme.onSurface
        },
        fontWeight = if (isToday || isSelected)
          androidx.compose.ui.text.font.FontWeight.Bold
        else
          androidx.compose.ui.text.font.FontWeight.Normal
      )

      if (phase != null && !isSelected) {
        Box(
          modifier = Modifier
            .size(4.dp)
            .clip(CircleShape)
            .background(phaseColor)
        )
      }

      if (isPredicted) {
        Box(
          modifier = Modifier
            .size(4.dp)
            .clip(CircleShape)
            .background(ColorMenstrual)
        )
      }
    }
  }
}