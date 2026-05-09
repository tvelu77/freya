package io.tvelu77.freya.presentation.onboarding.disclaimer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.tvelu77.freya.ui.theme.Amber100
import io.tvelu77.freya.ui.theme.Amber300

@Composable
fun DisclaimerBox(modifier: Modifier = Modifier) {
  Card(
    modifier = modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(
      containerColor = Amber100
    ),
    shape = MaterialTheme.shapes.large,
    border = BorderStroke(0.5.dp, Amber300)
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      DisclaimerRow(
        emoji = "🔒",
        text = "Aucun traqueur, aucune publicité. Tes données restent sur ton téléphone."
      )
      DisclaimerRow(
        emoji = "💛",
        text = "Cet outil n'est pas un substitut à un avis médical professionnel."
      )
      DisclaimerRow(
        emoji = "✨",
        text = "Conçu pour aider, jamais pour juger."
      )
    }
  }
}

@Composable
private fun DisclaimerRow(emoji: String, text: String) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalAlignment = Alignment.Top
  ) {
    Text(emoji, style = MaterialTheme.typography.bodyMedium)
    Text(
      text = text,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}