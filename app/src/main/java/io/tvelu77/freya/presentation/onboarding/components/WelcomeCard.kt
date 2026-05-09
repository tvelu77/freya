package io.tvelu77.freya.presentation.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun WelcomeCard(emoji: String,
                title: String,
                backgroundColor: Color,
                subtitleColor: Color,
                modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(200.dp)
      .background(backgroundColor),
    contentAlignment = Alignment.Center
  ) {

    Box(
      modifier = Modifier
        .size(120.dp)
        .offset(x = 60.dp, y = (-40).dp)
        .background(
          color = subtitleColor.copy(alpha = 0.25f),
          shape = CircleShape
        )
        .align(Alignment.TopEnd)
    )

    Box(
      modifier = Modifier
        .size(80.dp)
        .offset(x = (-30).dp, y = 30.dp)
        .background(
          color = subtitleColor.copy(alpha = 0.15f),
          shape = CircleShape
        )
        .align(Alignment.BottomStart)
    )

    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Text(
        text = emoji,
        style = MaterialTheme.typography.displayMedium
      )
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = subtitleColor
      )
    }
  }
}