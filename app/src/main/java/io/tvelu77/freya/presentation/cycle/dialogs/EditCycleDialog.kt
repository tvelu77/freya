package io.tvelu77.freya.presentation.cycle.dialogs

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.tvelu77.freya.R
import io.tvelu77.freya.domain.models.CycleEntry
import io.tvelu77.freya.ui.theme.Amber300
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun EditCycleDialog(
  cycle: CycleEntry,
  onConfirm: (LocalDate, LocalDate?) -> Unit,
  onDismiss: () -> Unit,
  errorMessage: String? = null
) {
  val today = LocalDate.now()
  var startDate by remember { mutableStateOf(cycle.startDate) }
  var endDate by remember { mutableStateOf(cycle.endDate) }

  val startChoices = remember(cycle.startDate) {
    val from = minOf(cycle.startDate, today.minusDays(14))
    generateSequence(from) { it.plusDays(1) }
      .takeWhile { !it.isAfter(today) }
      .take(45)
      .toList()
  }
  val endChoices = remember(startDate) {
    generateSequence(startDate) { it.plusDays(1) }
      .takeWhile { !it.isAfter(today) }
      .toList()
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    icon = { Text("✏️", style = MaterialTheme.typography.headlineMedium) },
    title = {
      Text(
        text = stringResource(R.string.dialog_edit_cycle_title),
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center
      )
    },
    text = {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = stringResource(R.string.dialog_period_start_label),
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 6.dp)
        ) {
          startChoices.forEach { date ->
            FilterChip(
              selected = startDate == date,
              onClick = {
                startDate = date
                val currentEnd = endDate
                if (currentEnd != null && currentEnd.isBefore(date)) endDate = null
              },
              label = {
                Text(
                  date.format(DateTimeFormatter.ofPattern("d MMM", Locale.FRENCH)),
                  style = MaterialTheme.typography.labelSmall
                )
              }
            )
          }
        }
        Text(
          text = stringResource(R.string.dialog_period_end_label),
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 6.dp)
        ) {
          FilterChip(
            selected = endDate == null,
            onClick = { endDate = null },
            label = {
              Text(
                text = stringResource(R.string.dialog_ongoing),
                style = MaterialTheme.typography.labelSmall
              )
            }
          )
          endChoices.forEach { date ->
            FilterChip(
              selected = endDate == date,
              onClick = { endDate = date },
              label = {
                Text(
                  date.format(DateTimeFormatter.ofPattern("d MMM", Locale.FRENCH)),
                  style = MaterialTheme.typography.labelSmall
                )
              }
            )
          }
        }
        errorMessage?.let {
          Spacer(Modifier.height(10.dp))
          Text(
            text = it,
            color = Amber300,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
          )
        }
      }
    },
    confirmButton = {
      Button(onClick = { onConfirm(startDate, endDate) }) {
        Text(stringResource(R.string.dialog_save))
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(stringResource(R.string.action_cancel))
      }
    }
  )
}
