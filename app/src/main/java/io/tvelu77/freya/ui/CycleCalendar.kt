package io.tvelu77.freya.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.tvelu77.freya.models.Period
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CycleCalendar(
    periods: List<Period>,
    nextPeriodDate: LocalDate?,
    avgCycleLength: Int,
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            CalendarHeader(
                currentMonth = currentMonth,
                onMonthChange = { currentMonth = it }
            )
            CalendarGrid(
                currentMonth = currentMonth,
                periods = periods,
                nextPeriodDate = nextPeriodDate,
                avgCycleLength = avgCycleLength,
                onDayClick = onDayClick
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun CalendarHeader(
    currentMonth: YearMonth,
    onMonthChange: (YearMonth) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onMonthChange(currentMonth.minusMonths(1)) }) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Mois précédent")
        }
        Text(
            text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.FRANCE).replaceFirstChar { it.uppercase() }} ${currentMonth.year}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = { onMonthChange(currentMonth.plusMonths(1)) }) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Mois suivant")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun CalendarGrid(
    currentMonth: YearMonth,
    periods: List<Period>,
    nextPeriodDate: LocalDate?,
    avgCycleLength: Int,
    onDayClick: (LocalDate) -> Unit
) {
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek.value
    val startOffset = (firstDayOfMonth - 1) % 7

    val totalGridCells = (daysInMonth + startOffset + 6) / 7 * 7

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("L", "M", "M", "J", "V", "S", "D").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        for (i in 0 until totalGridCells step 7) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (j in 0 until 7) {
                    val dayIndex = i + j - startOffset
                    if (dayIndex in 0 until daysInMonth) {
                        val date = currentMonth.atDay(dayIndex + 1)
                        CalendarDay(
                            date = date,
                            status = getDayStatus(date, periods, nextPeriodDate, avgCycleLength),
                            onClick = { onDayClick(date) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun CalendarDay(
    date: LocalDate,
    status: DayStatus,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isToday = date == LocalDate.now()

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(getDayColor(status, isToday))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = if (status != DayStatus.NONE || isToday) Color.White else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun getDayStatus(
    date: LocalDate,
    periods: List<Period>,
    nextPeriodDate: LocalDate?,
    avgCycleLength: Int
): DayStatus {
    if (periods.any { !date.isBefore(it.startDate) && !date.isAfter(it.endDate) }) {
        return DayStatus.PERIOD
    }

    if (nextPeriodDate != null) {
        if (!date.isBefore(nextPeriodDate) && date.isBefore(nextPeriodDate.plusDays(5))) {
            return DayStatus.PREDICTED_PERIOD
        }

        val ovulationStart = nextPeriodDate.minusDays(15)
        if (!date.isBefore(ovulationStart) && date.isBefore(ovulationStart.plusDays(3))) {
            return DayStatus.OVULATION
        }
    }
    
    return DayStatus.NONE
}

@Composable
private fun getDayColor(status: DayStatus, isToday: Boolean): Color {
    return when (status) {
        DayStatus.PERIOD -> Color(0xFFE91E63) 
        DayStatus.PREDICTED_PERIOD -> Color(0xFFFF80AB)
        DayStatus.OVULATION -> Color(0xFF9C27B0)
        DayStatus.NONE -> if (isToday) MaterialTheme.colorScheme.primary else Color.Transparent
    }
}

enum class DayStatus {
    NONE, PERIOD, PREDICTED_PERIOD, OVULATION
}
