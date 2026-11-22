package io.tvelu77.freya.presentation.food

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
fun NoCycleOnFoodScreen(modifier: Modifier = Modifier) {
  Box(
    modifier = modifier,
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.padding(32.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      IllustrationPlaceholder(emoji = "🌸")
      Text(
        text = stringResource(R.string.food_no_cycle_title),
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center
      )
      Text(
        text = stringResource(R.string.food_no_cycle_body),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
      )
    }
  }
}
