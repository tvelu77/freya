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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

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
      Text("🌸", style = MaterialTheme.typography.displayMedium)
      Text(
        text = "Commence par ton cycle",
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center
      )
      Text(
        text = "Les conseils nutritionnels sont adaptés à ta phase du cycle. " +
                "Renseigne tes règles dans l'onglet Cycle pour commencer !",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
      )
    }
  }
}