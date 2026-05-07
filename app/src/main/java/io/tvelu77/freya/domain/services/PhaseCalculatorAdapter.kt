package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.CycleEntry
import io.tvelu77.freya.domain.models.NutritionAdvice
import io.tvelu77.freya.domain.models.PhaseInfo
import io.tvelu77.freya.domain.models.PhaseType
import io.tvelu77.freya.domain.ports.api.PhaseCalculator
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class PhaseCalculatorAdapter: PhaseCalculator  {

  override fun getCurrentPhase(
    cycleEntry: CycleEntry,
    today: LocalDate
  ): PhaseInfo {
    val dayInCycle = dayInCycle(cycleEntry.startDate, today)
    val phase = phaseForDay(dayInCycle, cycleEntry.cycleLengthDays)
    val (start, end) = phaseRange(cycleEntry.startDate, phase, cycleEntry.cycleLengthDays)

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

  private fun dayInCycle(startDate: LocalDate, today: LocalDate): Int {
    val day = (startDate.until(today, ChronoUnit.DAYS) % 28 + 1).toInt()
    return if (day < 1) 1 else day
  }

  private fun phaseForDay(day: Int, cycleLength: Int = 28): PhaseType {
    val menstrualEnd = 5
    val follicularEnd = (cycleLength * 0.46).toInt()
    val ovulatoryEnd = (cycleLength * 0.57).toInt()
    return when {
      day <= menstrualEnd -> PhaseType.MENSTRUAL
      day <= follicularEnd -> PhaseType.FOLLICULAR
      day <= ovulatoryEnd -> PhaseType.OVULATORY
      else -> PhaseType.LUTEAL
    }
  }

  private fun phaseRange(
    cycleStart: LocalDate,
    phase: PhaseType,
    cycleLength: Int
  ): Pair<LocalDate, LocalDate> {
    val follicularEnd = (cycleLength * 0.46).toInt()
    val ovulatoryEnd = (cycleLength * 0.57).toInt()

    return when (phase) {
      PhaseType.MENSTRUAL -> cycleStart to cycleStart.plusDays(4)
      PhaseType.FOLLICULAR -> cycleStart.plusDays(5) to cycleStart.plusDays(follicularEnd.toLong())
      PhaseType.OVULATORY  -> cycleStart.plusDays((follicularEnd + 1).toLong()) to cycleStart.plusDays(ovulatoryEnd.toLong())
      PhaseType.LUTEAL     -> cycleStart.plusDays((ovulatoryEnd + 1).toLong()) to cycleStart.plusDays((cycleLength - 1).toLong())
    }
  }

}