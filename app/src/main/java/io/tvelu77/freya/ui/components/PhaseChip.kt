package io.tvelu77.freya.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.tvelu77.freya.domain.models.PhaseType
import io.tvelu77.freya.ui.theme.ColorFollicular
import io.tvelu77.freya.ui.theme.ColorLuteal
import io.tvelu77.freya.ui.theme.ColorMenstrual
import io.tvelu77.freya.ui.theme.ColorOvulatory

@Composable
fun PhaseChip(phase: PhaseType, modifier: Modifier = Modifier) {
  val (color, emoji) = when (phase) {
    PhaseType.MENSTRUAL  -> ColorMenstrual to "🌑"
    PhaseType.FOLLICULAR -> ColorFollicular to "🌱"
    PhaseType.OVULATORY  -> ColorOvulatory to "✨"
    PhaseType.LUTEAL     -> ColorLuteal to "🌙"
  }
  Row(
    modifier = modifier
      .background(color.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
      .padding(horizontal = 12.dp, vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    Text(emoji)
    Text(
      text = phase.displayName,
      style = MaterialTheme.typography.labelSmall,
      color = color.copy(alpha = 0.9f).let {
        Color(it.red * 0.7f, it.green * 0.7f, it.blue * 0.7f)
      }
    )
  }
}