package io.tvelu77.freya.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.tvelu77.freya.domain.models.NutritionAdvice

@Composable
fun NutritionAdviceCard(advice: NutritionAdvice, modifier: Modifier = Modifier) {
  Card(
    modifier = modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.secondaryContainer
    ),
    shape = MaterialTheme.shapes.large
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = "Conseil du moment",
        style = MaterialTheme.typography.titleMedium
      )
      Spacer(Modifier.height(8.dp))
      Text(
        text = advice.tipMessage,
        style = MaterialTheme.typography.bodyMedium
      )
      Spacer(Modifier.height(12.dp))
      Text(
        text = "Nutriments clés",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
      )
      Spacer(Modifier.height(4.dp))
      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        advice.keyNutrients.forEach { nutrient ->
          SuggestionChip(
            onClick = {},
            label = { Text(nutrient, style = MaterialTheme.typography.labelSmall) }
          )
        }
      }
    }
  }
}