package io.tvelu77.freya.domain.ports.api

import io.tvelu77.freya.domain.models.CycleEntry
import io.tvelu77.freya.domain.models.CycleEntryResult
import io.tvelu77.freya.domain.models.PhaseInfo
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TrackCycleUseCase {

  suspend fun startCycle(startDate: LocalDate, cycleLengthDays: Int = 28): CycleEntryResult
  suspend fun endPeriod(cycleEntry: CycleEntry, endDate: LocalDate): CycleEntryResult
  suspend fun updateCycle(entry: CycleEntry): CycleEntryResult
  suspend fun deleteCycle(id: Long)
  fun getCurrentPhase(today: LocalDate = LocalDate.now()): Flow<PhaseInfo?>
  fun getNextCyclePredictions(count: Int = 3): Flow<List<LocalDate>>
  fun getCycleHistory(): Flow<List<CycleEntry>>

}
