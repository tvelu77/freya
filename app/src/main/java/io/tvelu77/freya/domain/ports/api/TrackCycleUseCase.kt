package io.tvelu77.freya.domain.ports.api

import io.tvelu77.freya.domain.models.CycleEntry
import io.tvelu77.freya.domain.models.PhaseInfo
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TrackCycleUseCase {

  suspend fun startCycle(startDate: LocalDate, cycleLengthDays: Int = 28): Long
  suspend fun endPeriod(cycleEntry: CycleEntry, endDate: LocalDate)
  fun getCurrentPhase(today: LocalDate = LocalDate.now()): Flow<PhaseInfo?>
  fun getNextCyclePredictions(count: Int = 3): Flow<List<LocalDate>>
  fun getCycleHistory(): Flow<List<CycleEntry>>

}