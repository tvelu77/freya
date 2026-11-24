package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.ActivityEntry
import io.tvelu77.freya.domain.models.SportType
import io.tvelu77.freya.domain.ports.api.LogActivityUseCase
import io.tvelu77.freya.domain.ports.spi.ActivityRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class LogActivityUseCaseAdapter @Inject constructor(
  private val repository: ActivityRepository
) : LogActivityUseCase {

  override suspend fun logActivity(
    sport: SportType,
    durationMin: Int,
    weightKg: Float?,
    date: LocalDate
  ): Long = repository.saveActivity(
    ActivityEntry(
      date = date,
      sportType = sport,
      durationMin = durationMin,
      kcalBurned = ActivityEntry.computeKcal(sport, durationMin, weightKg)
    )
  )

  override fun getActivitiesForDay(date: LocalDate): Flow<List<ActivityEntry>> =
    repository.getActivitiesForDate(date)

  override suspend fun deleteActivity(id: Long) =
    repository.deleteActivity(id)

  override suspend fun updateActivity(entry: ActivityEntry) {
    repository.saveActivity(entry)
  }

  override suspend fun getTotalKcalBurned(date: LocalDate): Int =
    repository.getTotalKcalBurned(date)
}
