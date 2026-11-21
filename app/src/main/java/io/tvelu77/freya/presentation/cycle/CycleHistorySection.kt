package io.tvelu77.freya.presentation.cycle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Circle
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.tvelu77.freya.domain.models.CycleEntry
import io.tvelu77.freya.domain.models.CycleStats
import io.tvelu77.freya.ui.theme.ColorMenstrual
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CycleHistorySection(
  cycles: List<CycleEntry>,
  onEdit: (CycleEntry) -> Unit,
  onDelete: (CycleEntry) -> Unit,
  stats: CycleStats? = null
) {
  Column(modifier = Modifier.padding(horizontal = 16.dp)) {
    Text("Historique", style = MaterialTheme.typography.titleMedium)
    stats?.let {
      Spacer(Modifier.height(4.dp))
      Text(
        text = "Sur ${it.cyclesCounted} cycles : moyen ${it.averageCycleLengthDays}j · " +
          "règles ${it.averagePeriodLengthDays}j · variabilité ±${
            String.format(Locale.FRENCH, "%.1f", it.variabilityDays)
          }j",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
    Spacer(Modifier.height(10.dp))

    cycles.take(6).forEachIndexed { index, cycle ->
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Icon(
            imageVector = Icons.Rounded.Circle,
            contentDescription = null,
            tint = ColorMenstrual.copy(alpha = 0.6f),
            modifier = Modifier.size(10.dp)
          )
          Column {
            Text(
              text = cycle.startDate.format(
                DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH)
              ),
              style = MaterialTheme.typography.bodyMedium
            )
            cycle.endDate?.let {
              Text(
                text = "→ ${it.format(DateTimeFormatter.ofPattern("d MMMM", Locale.FRENCH))}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
          Text(
            text = "${cycle.cycleLengthDays}j",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          IconButton(
            onClick = { onEdit(cycle) },
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Rounded.Edit,
              contentDescription = "Modifier ce cycle",
              modifier = Modifier.size(18.dp)
            )
          }
          IconButton(
            onClick = { onDelete(cycle) },
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Rounded.DeleteOutline,
              contentDescription = "Supprimer ce cycle",
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
      if (index < minOf(cycles.size, 6) - 1) {
        HorizontalDivider(
          thickness = 0.5.dp,
          color = MaterialTheme.colorScheme.outlineVariant
        )
      }
    }
  }
}