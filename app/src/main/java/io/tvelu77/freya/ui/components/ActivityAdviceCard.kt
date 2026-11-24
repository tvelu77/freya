package io.tvelu77.freya.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.tvelu77.freya.R
import io.tvelu77.freya.domain.models.ActivityAdvice

@Composable
fun ActivityAdviceCard(advice: ActivityAdvice, modifier: Modifier = Modifier) {
  Card(
    modifier = modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.secondaryContainer
    ),
    shape = MaterialTheme.shapes.large
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = stringResource(R.string.activity_advice_title),
        style = MaterialTheme.typography.titleMedium
      )
      Spacer(Modifier.height(8.dp))
      Text(
        text = advice.title,
        style = MaterialTheme.typography.bodyLarge
      )
      Spacer(Modifier.height(4.dp))
      Text(
        text = advice.message,
        style = MaterialTheme.typography.bodyMedium
      )
    }
  }
}
