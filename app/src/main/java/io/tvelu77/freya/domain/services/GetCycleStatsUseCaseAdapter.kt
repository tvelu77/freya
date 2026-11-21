package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.CycleEntry
import io.tvelu77.freya.domain.models.CycleStats
import io.tvelu77.freya.domain.ports.api.GetCycleStatsUseCase
import io.tvelu77.freya.domain.ports.spi.CycleRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.temporal.ChronoUnit
import kotlin.math.sqrt

class GetCycleStatsUseCaseAdapter @Inject constructor(
  private val repository: CycleRepository
): GetCycleStatsUseCase {

  override fun execute(take: Int): Flow<CycleStats?> =
    repository.getAllCycles().map { cycles ->
      compute(cycles.sortedBy { it.startDate }.takeLast(take))
    }

  private fun compute(cycles: List<CycleEntry>): CycleStats? {
    if (cycles.isEmpty()) return null
    val gaps = cycles.zipWithNext().map { pair ->
      ChronoUnit.DAYS.between(pair.first.startDate, pair.second.startDate).toInt()
    }
    val averageCycle = if (gaps.isNotEmpty()) {
      (gaps.sum().toDouble() / gaps.size).toInt()
    } else {
      cycles.last().cycleLengthDays
    }
    val periodLengths = cycles.mapNotNull { cycle ->
      cycle.endDate?.let { ChronoUnit.DAYS.between(cycle.startDate, it).toInt() + 1 }
    }
    val averagePeriod = if (periodLengths.isNotEmpty()) {
      (periodLengths.sum().toDouble() / periodLengths.size).toInt()
    } else {
      0
    }
    val variability = if (gaps.size >= 2) {
      val mean = gaps.sum().toDouble() / gaps.size
      sqrt(gaps.sumOf { (it - mean) * (it - mean) } / gaps.size)
    } else {
      0.0
    }
    return CycleStats(averageCycle, averagePeriod, variability, cycles.size)
  }
}
