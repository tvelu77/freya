package io.tvelu77.freya.presentation.food

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.tvelu77.freya.domain.models.FoodEntry
import io.tvelu77.freya.domain.models.MealType

@Composable
fun MealSection(
  mealType: MealType,
  entries: List<FoodEntry>,
  showCalories: Boolean,
  onAddFood: () -> Unit,
  onDeleteFood: (Long) -> Unit
) {
  val emoji = when (mealType) {
    MealType.BREAKFAST -> "☀️"
    MealType.LUNCH     -> "🥗"
    MealType.DINNER    -> "🌙"
    MealType.SNACK     -> "🍎"
  }

  Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text(emoji, style = MaterialTheme.typography.titleMedium)
        Text(mealType.displayName, style = MaterialTheme.typography.titleMedium)
        if (entries.isNotEmpty() && showCalories) {
          Text(
            text = "${entries.sumOf { it.calories }} kcal",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
      IconButton(
        onClick = onAddFood,
        modifier = Modifier
          .size(32.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.primaryContainer)
      ) {
        Icon(
          Icons.Rounded.Add,
          contentDescription = "Ajouter un aliment",
          modifier = Modifier.size(16.dp),
          tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
      }
    }

    Spacer(Modifier.height(6.dp))

    if (entries.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
          .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "Rien d'enregistré",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    } else {
      Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
      ) {
        entries.forEachIndexed { index, entry ->
          FoodEntryRow(
            entry = entry,
            showCalories = showCalories,
            onDelete = { onDeleteFood(entry.id) }
          )
          if (index < entries.size - 1) {
            HorizontalDivider(
              thickness = 0.5.dp,
              modifier = Modifier.padding(horizontal = 16.dp),
              color = MaterialTheme.colorScheme.outlineVariant
            )
          }
        }
      }
    }

    Spacer(Modifier.height(8.dp))
  }
}