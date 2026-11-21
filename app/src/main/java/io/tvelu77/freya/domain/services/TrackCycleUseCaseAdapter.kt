package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.CycleEntry
import io.tvelu77.freya.domain.models.CycleEntryResult
import io.tvelu77.freya.domain.models.PhaseInfo
import io.tvelu77.freya.domain.ports.api.PhaseCalculator
import io.tvelu77.freya.domain.ports.api.PhaseNotificationPlanner
import io.tvelu77.freya.domain.ports.api.TrackCycleUseCase
import io.tvelu77.freya.domain.ports.spi.CycleRepository
import io.tvelu77.freya.domain.ports.spi.NotificationScheduler
import io.tvelu77.freya.domain.ports.spi.UserProfileRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class TrackCycleUseCaseAdapter @Inject constructor(
  private val repository: CycleRepository,
  private val calculator: PhaseCalculator,
  private val profileRepository: UserProfileRepository,
  private val notificationPlanner: PhaseNotificationPlanner,
  private val notificationScheduler: NotificationScheduler
): TrackCycleUseCase {

  override suspend fun startCycle(
    startDate: LocalDate,
    cycleLengthDays: Int
  ): CycleEntryResult {
    val allCycles = repository.getAllCyclesOnce()
    if (overlapsRecordedPeriod(startDate, excludeId = -1L, allCycles)) {
      return CycleEntryResult.Error("This date falls inside an already recorded period.")
    }
    val current = repository.getCycleByDate(startDate)
    if (current != null && current.isOngoing) {
      repository.updateCycle(current.copy(endDate = startDate.minusDays(1)))
    }
    val id = repository.saveCycle(
      CycleEntry(startDate = startDate, endDate = null, cycleLengthDays = cycleLengthDays)
    )
    rescheduleNotifications()
    return CycleEntryResult.Success(id)
  }

  override suspend fun endPeriod(
    cycleEntry: CycleEntry,
    endDate: LocalDate
  ): CycleEntryResult {
    if (endDate.isBefore(cycleEntry.startDate)) {
      return CycleEntryResult.Error("The end of the period must be after its start.")
    }
    val previousCycle = repository.getAllCyclesOnce()
      .sortedByDescending { it.startDate }
      .getOrNull(1)

    val realCycleLength = previousCycle?.let {
      ChronoUnit.DAYS.between(it.startDate, cycleEntry.startDate).toInt()
    } ?: cycleEntry.cycleLengthDays

    val updated = cycleEntry.copy(
      endDate = endDate,
      cycleLengthDays = realCycleLength.coerceIn(21, 45)
    )
    repository.updateCycle(updated)
    rescheduleNotifications()
    return CycleEntryResult.Success(updated.id)
  }

  override suspend fun updateCycle(entry: CycleEntry): CycleEntryResult {
    if (entry.endDate != null && entry.endDate.isBefore(entry.startDate)) {
      return CycleEntryResult.Error("The end of the period must be after its start.")
    }
    val allCycles = repository.getAllCyclesOnce()
    val datesToCheck = listOfNotNull(entry.startDate, entry.endDate)
    if (datesToCheck.any { overlapsRecordedPeriod(it, entry.id, allCycles) }) {
      return CycleEntryResult.Error("This cycle overlaps an already recorded period.")
    }
    repository.updateCycle(entry)
    rescheduleNotifications()
    return CycleEntryResult.Success(entry.id)
  }

  override suspend fun deleteCycle(id: Long) {
    repository.deleteCycle(id)
    rescheduleNotifications()
  }

  override suspend fun rescheduleNotifications() {
    val enabled = profileRepository.getProfile().first().notificationsEnabled
    val cycles = repository.getAllCyclesOnce()
    val latest = cycles.maxByOrNull { it.startDate }
    if (!enabled || latest == null) {
      notificationScheduler.cancelAll()
      return
    }
    val averaged = latest.withAverageLength(cycles)
    val phaseInfo = calculator.getCurrentPhase(averaged)
    val nextPeriod = calculator.predictNextCycles(averaged, count = 1).firstOrNull()
    notificationScheduler.schedule(notificationPlanner.plan(phaseInfo, nextPeriod))
  }

  private fun overlapsRecordedPeriod(
    date: LocalDate,
    excludeId: Long,
    cycles: List<CycleEntry>
  ): Boolean =
    cycles.any { it.id != excludeId && it.endDate != null && date in it.startDate..it.endDate }

  override fun getCurrentPhase(today: LocalDate): Flow<PhaseInfo?> =
    repository.getAllCycles().map { cycles ->
      val latest = cycles.maxByOrNull { it.startDate }
      latest?.let { calculator.getCurrentPhase(it.withAverageLength(cycles), today) }
    }

  override fun getNextCyclePredictions(count: Int): Flow<List<LocalDate>> =
    repository.getAllCycles().map { cycles ->
      val latest = cycles.maxByOrNull { it.startDate }
      if (latest == null) {
        emptyList()
      } else {
        calculator.predictNextCycles(latest.withAverageLength(cycles), count)
      }
    }

  override fun getCycleHistory(): Flow<List<CycleEntry>> = repository.getAllCycles()

  private fun CycleEntry.withAverageLength(cycles: List<CycleEntry>): CycleEntry =
    copy(cycleLengthDays = calculator.averageCycleLength(cycles, fallback = cycleLengthDays))
}
