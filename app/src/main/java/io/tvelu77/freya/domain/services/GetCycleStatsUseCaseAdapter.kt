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
    val gaps = cycleGaps(cycles)
    val periods = periodLengths(cycles)
    return CycleStats(
      averageCycleLengthDays = gaps.meanOrFallback(cycles.last().cycleLengthDays),
      averagePeriodLengthDays = periods.meanOrFallback(0),
      variabilityDays = gaps.standardDeviation(),
      cyclesCounted = cycles.size
    )
  }

  private fun cycleGaps(cycles: List<CycleEntry>): List<Int> =
    cycles.zipWithNext().map { pair ->
      ChronoUnit.DAYS.between(pair.first.startDate, pair.second.startDate).toInt()
    }

  private fun periodLengths(cycles: List<CycleEntry>): List<Int> =
    cycles.mapNotNull { cycle ->
      cycle.endDate?.let { end -> ChronoUnit.DAYS.between(cycle.startDate, end).toInt() + 1 }
    }

  private fun List<Int>.meanOrFallback(fallback: Int): Int =
    if (isEmpty()) fallback else average().toInt()

  private fun List<Int>.standardDeviation(): Double {
    if (size < 2) return 0.0
    val mean = average()
    return sqrt(map { (it - mean) * (it - mean) }.average())
  }
}
