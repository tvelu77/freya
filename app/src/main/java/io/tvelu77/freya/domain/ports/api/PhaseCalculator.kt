package io.tvelu77.freya.domain.ports.api

import io.tvelu77.freya.domain.models.CycleEntry
import io.tvelu77.freya.domain.models.PhaseInfo
import java.time.LocalDate

interface PhaseCalculator {

  fun getCurrentPhase(cycleEntry: CycleEntry, today: LocalDate = LocalDate.now()): PhaseInfo
  fun predictNextCycles(lastCycle: CycleEntry, count: Int = 3): List<LocalDate>
  fun averageCycleLength(cycles: List<CycleEntry>, fallback: Int = 28): Int

}