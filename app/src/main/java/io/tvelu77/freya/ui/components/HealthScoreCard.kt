package io.tvelu77.freya.ui.components

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import io.tvelu77.freya.domain.models.HealthScore
import io.tvelu77.freya.domain.models.ScoreLevel
import io.tvelu77.freya.ui.theme.ScoreGood
import io.tvelu77.freya.ui.theme.ScoreLow
import io.tvelu77.freya.ui.theme.ScoreMedium

@Composable
fun HealthScoreCard(score: HealthScore, modifier: Modifier = Modifier) {
  val animatedValue by animateFloatAsState(
    targetValue = score.value / 100f,
    animationSpec = tween(1000, easing = EaseOutCubic),
    label = "score_anim"
  )

  val arcColor = when (score.level) {
    ScoreLevel.GOOD   -> ScoreGood
    ScoreLevel.MEDIUM -> ScoreMedium
    ScoreLevel.LOW    -> ScoreLow
  }

  Card(
    modifier = modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(2.dp),
    shape = MaterialTheme.shapes.large
  ) {
    Column(
      modifier = Modifier.padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text("Ton score du jour", style = MaterialTheme.typography.titleMedium)

      Spacer(Modifier.height(16.dp))

      Box(contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(120.dp)) {
          val stroke = Stroke(12.dp.toPx(), cap = StrokeCap.Round)

          drawArc(
            color = androidx.compose.ui.graphics.Color.LightGray.copy(alpha = 0.3f),
            startAngle = -210f, sweepAngle = 240f,
            useCenter = false, style = stroke
          )

          drawArc(
            color = arcColor,
            startAngle = -210f,
            sweepAngle = 240f * animatedValue,
            useCenter = false, style = stroke
          )
        }
        Text(
          text = "${score.value}",
          style = MaterialTheme.typography.headlineLarge,
          color = arcColor
        )
      }

      Spacer(Modifier.height(12.dp))

      Text(
        text = score.message,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
      )

      Spacer(Modifier.height(16.dp))


      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        ScoreDetail("🍽️", "Nutrition", score.nutritionScore)
        ScoreDetail("💧", "Hydratation", score.hydrationScore)
        ScoreDetail("🚶", "Activité", score.activityScore)
      }
    }
  }
}

@Composable
private fun ScoreDetail(emoji: String, label: String, value: Int) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(emoji, style = MaterialTheme.typography.titleMedium)
    Text("$value", style = MaterialTheme.typography.titleMedium)
    Text(label, style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant)
  }
}