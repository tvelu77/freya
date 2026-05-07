package io.tvelu77.freya.presentation.cycle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.tvelu77.freya.domain.models.PhaseType
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CycleCalendar(
  selectedMonth: YearMonth,
  selectedDate: LocalDate?,
  onDateSelected: (LocalDate) -> Unit,
  onMonthChanged: (YearMonth) -> Unit,
  getPhaseForDate: (LocalDate) -> PhaseType?,
  isPredictedStart: (LocalDate) -> Boolean
) {
  Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(onClick = { onMonthChanged(selectedMonth.minusMonths(1)) }) {
        Icon(Icons.Rounded.ChevronLeft, contentDescription = "Mois précédent")
      }
      Text(
        text = selectedMonth.format(
          DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH)
        ).replaceFirstChar { it.uppercase() },
        style = MaterialTheme.typography.titleMedium
      )
      IconButton(onClick = { onMonthChanged(selectedMonth.plusMonths(1)) }) {
        Icon(Icons.Rounded.ChevronRight, contentDescription = "Mois suivant")
      }
    }

    Spacer(Modifier.height(8.dp))

    // En-têtes jours
    Row(modifier = Modifier.fillMaxWidth()) {
      listOf("L", "M", "M", "J", "V", "S", "D").forEach { day ->
        Text(
          text = day,
          modifier = Modifier.weight(1f),
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    Spacer(Modifier.height(4.dp))

    val firstDay = selectedMonth.atDay(1)
    val daysInMonth = selectedMonth.lengthOfMonth()

    val startOffset = (firstDay.dayOfWeek.value - 1)
    val totalCells = startOffset + daysInMonth
    val rows = (totalCells + 6) / 7

    for (row in 0 until rows) {
      Row(modifier = Modifier.fillMaxWidth()) {
        for (col in 0 until 7) {
          val cellIndex = row * 7 + col
          val dayNumber = cellIndex - startOffset + 1
          if (dayNumber !in 1..daysInMonth) {
            Box(modifier = Modifier.weight(1f).height(40.dp))
          } else {
            val date = selectedMonth.atDay(dayNumber)
            val phase = getPhaseForDate(date)
            val isToday = date == LocalDate.now()
            val isSelected = date == selectedDate
            val isPredicted = isPredictedStart(date)

            CalendarDay(
              day = dayNumber,
              phase = phase,
              isToday = isToday,
              isSelected = isSelected,
              isPredicted = isPredicted,
              onClick = { onDateSelected(date) },
              modifier = Modifier.weight(1f)
            )
          }
        }
      }
    }
  }
}