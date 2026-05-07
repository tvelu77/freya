package io.tvelu77.freya.presentation.cycle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.tvelu77.freya.domain.models.PhaseType
import io.tvelu77.freya.ui.theme.ColorFollicular
import io.tvelu77.freya.ui.theme.ColorLuteal
import io.tvelu77.freya.ui.theme.ColorMenstrual
import io.tvelu77.freya.ui.theme.ColorOvulatory

@Composable
fun PhaseLegendSection() {
  Column(modifier = Modifier.padding(16.dp)) {
    Text("Légende", style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(10.dp))

    val phases = listOf(
      Triple(PhaseType.MENSTRUAL, ColorMenstrual,  "Menstruelle  • J1–J5"),
      Triple(PhaseType.FOLLICULAR, ColorFollicular, "Folliculaire • J6–J13"),
      Triple(PhaseType.OVULATORY, ColorOvulatory,  "Ovulatoire   • J14–J16"),
      Triple(PhaseType.LUTEAL, ColorLuteal,     "Lutéale      • J17–J28")
    )

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      phases.forEach { (_, color, label) ->
        Column(
          modifier = Modifier.weight(1f),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(6.dp)
              .clip(RoundedCornerShape(3.dp))
              .background(color)
          )
          Spacer(Modifier.height(4.dp))
          Text(
            text = label.substringBefore("•").trim(),
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}