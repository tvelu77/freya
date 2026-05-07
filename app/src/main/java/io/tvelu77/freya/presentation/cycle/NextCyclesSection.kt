package io.tvelu77.freya.presentation.cycle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.tvelu77.freya.ui.theme.ColorMenstrual
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun NextCyclesSection(nextCycles: List<LocalDate>) {
  Column(modifier = Modifier.padding(horizontal = 16.dp)) {
    Text("Prochains cycles prévus", style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(10.dp))

    nextCycles.forEachIndexed { index, date ->
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(ColorMenstrual.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "${index + 1}",
            style = MaterialTheme.typography.labelSmall,
            color = ColorMenstrual
          )
        }
        Column {
          Text(
            text = date.format(
              DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.FRENCH)
            ).replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.bodyMedium
          )
          Text(
            text = "Estimation",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
      if (index < nextCycles.size - 1) {
        HorizontalDivider(
          thickness = 0.5.dp,
          color = MaterialTheme.colorScheme.outlineVariant
        )
      }
    }
    Spacer(Modifier.height(16.dp))
  }
}