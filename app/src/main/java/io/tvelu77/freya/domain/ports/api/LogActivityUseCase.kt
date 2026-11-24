package io.tvelu77.freya.domain.ports.api

import io.tvelu77.freya.domain.models.ActivityEntry
import io.tvelu77.freya.domain.models.SportType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface LogActivityUseCase {

  suspend fun logActivity(
    sport: SportType,
    durationMin: Int,
    weightKg: Float?,
    date: LocalDate = LocalDate.now()
  ): Long

  fun getActivitiesForDay(date: LocalDate = LocalDate.now()): Flow<List<ActivityEntry>>

  suspend fun deleteActivity(id: Long)

  suspend fun updateActivity(entry: ActivityEntry)

  suspend fun getTotalKcalBurned(date: LocalDate = LocalDate.now()): Int
}
