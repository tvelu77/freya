package io.tvelu77.freya.presentation.food

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

@Composable
fun MacroItem(label: String, value: Float, color: Color, show: Boolean) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
      text = if (show) "${value.toInt()}g" else "—",
      style = MaterialTheme.typography.titleMedium,
      color = color
    )
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
    )
  }
}