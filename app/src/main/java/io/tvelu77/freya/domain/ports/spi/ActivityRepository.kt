package io.tvelu77.freya.domain.ports.spi

import io.tvelu77.freya.domain.models.ActivityEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface ActivityRepository {
  fun getActivitiesForDate(date: LocalDate): Flow<List<ActivityEntry>>
  suspend fun saveActivity(entry: ActivityEntry): Long
  suspend fun deleteActivity(id: Long)
  suspend fun getTotalKcalBurned(date: LocalDate): Int
}
