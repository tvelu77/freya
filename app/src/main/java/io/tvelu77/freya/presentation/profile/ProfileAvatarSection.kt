package io.tvelu77.freya.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ProfileAvatarSection(firstName: String) {
  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box(
      modifier = Modifier
        .size(80.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.primaryContainer),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = if (firstName.isNotBlank())
          firstName.first().uppercaseChar().toString()
        else "🌸",
        style = MaterialTheme.typography.headlineLarge,
        color = MaterialTheme.colorScheme.onPrimaryContainer
      )
    }
    if (firstName.isNotBlank()) {
      Spacer(Modifier.height(10.dp))
      Text(
        text = firstName,
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center
      )
    }
  }
}