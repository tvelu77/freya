package io.tvelu77.freya.presentation.cycle

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Composable
fun StartCycleDialog(
  onConfirm: (LocalDate) -> Unit,
  onDismiss: () -> Unit
) {
  var selectedDate by remember { mutableStateOf(LocalDate.now()) }

  AlertDialog(
    onDismissRequest = onDismiss,
    icon = { Text("🌸", style = MaterialTheme.typography.headlineMedium) },
    title = {
      Text(
        "Nouveau cycle",
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center
      )
    },
    text = {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = "Quand ont commencé tes règles ?",
          style = MaterialTheme.typography.bodyMedium,
          textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))

        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
          (0..5).forEach { daysAgo ->
            val date = LocalDate.now().minusDays(daysAgo.toLong())
            val label = when (daysAgo) {
              0 -> "Aujourd'hui"
              1 -> "Hier"
              else -> "Il y a ${daysAgo}j"
            }
            FilterChip(
              selected = selectedDate == date,
              onClick = { selectedDate = date },
              label = { Text(label, style = MaterialTheme.typography.labelSmall) }
            )
          }
        }
      }
    },
    confirmButton = {
      Button(onClick = { onConfirm(selectedDate) }) {
        Text("Confirmer")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Annuler")
      }
    }
  )
}

