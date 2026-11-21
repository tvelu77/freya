package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.CycleEntry
import io.tvelu77.freya.domain.models.NutritionAdvice
import io.tvelu77.freya.domain.models.PhaseInfo
import io.tvelu77.freya.domain.models.PhaseType
import io.tvelu77.freya.domain.ports.api.PhaseCalculator
import jakarta.inject.Inject
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class PhaseCalculatorAdapter @Inject constructor(): PhaseCalculator  {

  override fun getCurrentPhase(
    cycleEntry: CycleEntry,
    today: LocalDate
  ): PhaseInfo {
    val cycleLength = cycleEntry.cycleLengthDays.coerceIn(MIN_CYCLE_LENGTH, MAX_CYCLE_LENGTH)
    val periodLength = periodLengthDays(cycleEntry)
    val dayInCycle = dayInCycle(cycleEntry.startDate, today, cycleLength)
    val phase = if (
      cycleEntry.endDate != null &&
      today.isAfter(cycleEntry.endDate) &&
      phaseForDay(dayInCycle, cycleLength, periodLength) == PhaseType.MENSTRUAL
    ) {
      PhaseType.FOLLICULAR
    } else {
      phaseForDay(dayInCycle, cycleLength, periodLength)
    }
    val (start, end) = phaseRange(cycleEntry.startDate, phase, cycleLength, periodLength)
    return PhaseInfo(
      phase,
      start,
      end,
      dayInCycle,
      NutritionAdvice.forPhase(phase)
    )
  }

  override fun predictNextCycles(
    lastCycle: CycleEntry,
    count: Int
  ): List<LocalDate> {
    return (1..count).map { i ->
      lastCycle.startDate.plusDays((lastCycle.cycleLengthDays * i).toLong())
    }
  }

  override fun averageCycleLength(
    cycles: List<CycleEntry>,
    fallback: Int
  ): Int {
    val sorted = cycles.sortedBy { it.startDate }
    val gaps = sorted.zipWithNext().map { pair ->
      ChronoUnit.DAYS.between(pair.first.startDate, pair.second.startDate).toInt()
    }
    if (gaps.isEmpty()) return fallback.coerceIn(MIN_CYCLE_LENGTH, MAX_CYCLE_LENGTH)
    return (gaps.sum() / gaps.size).coerceIn(MIN_CYCLE_LENGTH, MAX_CYCLE_LENGTH)
  }

  private fun dayInCycle(startDate: LocalDate, today: LocalDate, cycleLength: Int): Int {
    val day = (startDate.until(today, ChronoUnit.DAYS) % cycleLength + 1).toInt()
    return if (day < 1) 1 else day
  }

  private fun periodLengthDays(cycleEntry: CycleEntry): Int {
    val end = cycleEntry.endDate ?: return DEFAULT_PERIOD_LENGTH
    return (ChronoUnit.DAYS.between(cycleEntry.startDate, end).toInt() + 1)
      .coerceIn(MIN_PERIOD_LENGTH, MAX_PERIOD_LENGTH)
  }

  private fun ovulationDay(cycleLength: Int, periodLength: Int): Int =
    (cycleLength - DEFAULT_LUTEAL_LENGTH).coerceAtLeast(periodLength + 1)

  private fun phaseForDay(day: Int, cycleLength: Int, periodLength: Int): PhaseType {
    val ovulation = ovulationDay(cycleLength, periodLength)
    val ovulatoryStart = (ovulation - 2).coerceAtLeast(periodLength + 1)
    val ovulatoryEnd = ovulation + 1
    return when {
      day <= periodLength -> PhaseType.MENSTRUAL
      day < ovulatoryStart -> PhaseType.FOLLICULAR
      day <= ovulatoryEnd -> PhaseType.OVULATORY
      else -> PhaseType.LUTEAL
    }
  }

  private fun phaseRange(
    cycleStart: LocalDate,
    phase: PhaseType,
    cycleLength: Int,
    periodLength: Int
  ): Pair<LocalDate, LocalDate> {
    val ovulation = ovulationDay(cycleLength, periodLength)
    val ovulatoryStart = (ovulation - 2).coerceAtLeast(periodLength + 1)
    val ovulatoryEnd = ovulation + 1

    return when (phase) {
      PhaseType.MENSTRUAL -> cycleStart to cycleStart.plusDays((periodLength - 1).toLong())
      PhaseType.FOLLICULAR -> cycleStart.plusDays(periodLength.toLong()) to cycleStart.plusDays((ovulatoryStart - 1).toLong())
      PhaseType.OVULATORY  -> cycleStart.plusDays((ovulatoryStart - 1).toLong()) to cycleStart.plusDays(ovulatoryEnd.toLong())
      PhaseType.LUTEAL     -> cycleStart.plusDays((ovulatoryEnd + 1).toLong()) to cycleStart.plusDays((cycleLength - 1).toLong())
    }
  }

  companion object {
    private const val MIN_CYCLE_LENGTH = 21
    private const val MAX_CYCLE_LENGTH = 45
    private const val DEFAULT_PERIOD_LENGTH = 5
    private const val MIN_PERIOD_LENGTH = 2
    private const val MAX_PERIOD_LENGTH = 8
    private const val DEFAULT_LUTEAL_LENGTH = 14
  }

}
