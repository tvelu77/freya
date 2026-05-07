package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.CycleEntry
import io.tvelu77.freya.domain.models.PhaseInfo
import io.tvelu77.freya.domain.ports.api.PhaseCalculator
import io.tvelu77.freya.domain.ports.api.TrackCycleUseCase
import io.tvelu77.freya.domain.ports.spi.CycleRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class TrackCycleUseCaseAdapter @Inject constructor(
  private val repository: CycleRepository,
  private val calculator: PhaseCalculator
): TrackCycleUseCase {

  override suspend fun startCycle(
    startDate: LocalDate,
    cycleLengthDays: Int
  ): Long {
    val current = repository.getCycleByDate(startDate)
    if (current != null && current.isOngoing) {
      repository.updateCycle(current.copy(endDate = startDate.minusDays(1)))
    }
    return repository.saveCycle(CycleEntry(startDate = startDate, endDate = null, cycleLengthDays = cycleLengthDays))
  }

  override fun getCurrentPhase(today: LocalDate): Flow<PhaseInfo?> =
    repository.getLatestCycle().map { cycleEntry -> cycleEntry?.let { calculator.getCurrentPhase(it, today) }  }

  override fun getNextCyclePredictions(count: Int): Flow<List<LocalDate>> =
    repository.getLatestCycle().map { cycleEntry -> cycleEntry?.let { calculator.predictNextCycles(it, count) } ?: emptyList() }

  override fun getCycleHistory(): Flow<List<CycleEntry>> = repository.getAllCycles()
}