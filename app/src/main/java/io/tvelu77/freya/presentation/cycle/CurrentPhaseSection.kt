package io.tvelu77.freya.presentation.cycle

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.tvelu77.freya.domain.models.PhaseInfo
import io.tvelu77.freya.ui.components.PhaseChip

@Composable
fun CurrentPhaseSection(phase: PhaseInfo) {
  Column(modifier = Modifier.padding(16.dp)) {
    Text(
      text = "Phase actuelle",
      style = MaterialTheme.typography.titleMedium
    )
    Spacer(Modifier.height(12.dp))

    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer
      ),
      shape = MaterialTheme.shapes.large
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          PhaseChip(phase = phase.phase)
          Text(
            text = "Jour ${phase.daysInCycle}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
          )
        }

        Spacer(Modifier.height(12.dp))

        Text(
          text = phase.nutritionAdvice.tipMessage,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onPrimaryContainer
        )

        Spacer(Modifier.height(10.dp))

        Text(
          text = "Aliments recommandés",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
        Spacer(Modifier.height(6.dp))
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
          phase.nutritionAdvice.recommendedFoods.forEach { food ->
            SuggestionChip(
              onClick = {},
              label = {
                Text(food, style = MaterialTheme.typography.labelSmall)
              }
            )
          }
        }
      }
    }
  }
}