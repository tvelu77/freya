package io.tvelu77.freya.presentation.cycle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.tvelu77.freya.R
import io.tvelu77.freya.ui.components.IllustrationPlaceholder

@Composable
fun CycleEmptyState(onStartCycle: () -> Unit) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.tertiaryContainer
    ),
    shape = MaterialTheme.shapes.large
  ) {
    Column(
      modifier = Modifier.padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      IllustrationPlaceholder(emoji = "🌸")
      Text(
        text = stringResource(R.string.cycle_empty_title),
        style = MaterialTheme.typography.titleMedium,
        textAlign = TextAlign.Center
      )
      Text(
        text = stringResource(R.string.cycle_empty_body),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f),
        textAlign = TextAlign.Center
      )
      Button(onClick = onStartCycle) {
        Text(stringResource(R.string.cycle_empty_cta))
      }
    }
  }
}
