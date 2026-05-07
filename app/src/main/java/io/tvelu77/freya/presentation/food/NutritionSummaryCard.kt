package io.tvelu77.freya.presentation.food

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import io.tvelu77.freya.ui.theme.ScoreGood
import io.tvelu77.freya.ui.theme.ScoreLow
import io.tvelu77.freya.ui.theme.ScoreMedium

@Composable
fun NutritionSummaryCard(
  totalCalories: Int,
  caloriesTarget: Int,
  proteins: Float,
  carbs: Float,
  fats: Float,
  showCalories: Boolean,
  adviceMessage: String?
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer
    ),
    shape = MaterialTheme.shapes.large
  ) {
    Column(modifier = Modifier.padding(16.dp)) {

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
      ) {
        Column {
          Text(
            text = if (showCalories) "$totalCalories kcal" else "— kcal",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
          )
          Text(
            text = if (showCalories) "sur $caloriesTarget kcal" else "Mode discret activé",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
          )
        }
        if (showCalories) {
          Text(
            text = "${((totalCalories.toFloat() / caloriesTarget) * 100).toInt()}%",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
          )
        }
      }

      if (showCalories) {
        Spacer(Modifier.height(10.dp))
        LinearProgressIndicator(
          progress = { (totalCalories.toFloat() / caloriesTarget).coerceIn(0f, 1f) },
          modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp)),
          color = when {
            totalCalories < caloriesTarget * 0.6f -> ScoreLow
            totalCalories <= caloriesTarget * 1.1f -> ScoreGood
            else -> ScoreMedium
          },
          trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f),
          strokeCap = StrokeCap.Round
        )
      }

      Spacer(Modifier.height(14.dp))

      // Macros
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        MacroItem("Protéines", proteins, Color(0xFF7986CB), showCalories)
        MacroItem("Glucides",  carbs,    Color(0xFF4DB6AC), showCalories)
        MacroItem("Lipides",   fats,     Color(0xFFFFB74D), showCalories)
      }

      adviceMessage?.let {
        Spacer(Modifier.height(12.dp))
        HorizontalDivider(
          thickness = 0.5.dp,
          color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
        )
        Spacer(Modifier.height(10.dp))
        Text(
          text = "💡 $it",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
        )
      }
    }
  }
}