package io.tvelu77.freya.database.repositories

import io.tvelu77.freya.database.dao.ActivityDao
import io.tvelu77.freya.database.entities.ActivityEntryEntity
import io.tvelu77.freya.domain.models.ActivityEntry
import io.tvelu77.freya.domain.ports.spi.ActivityRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class RoomActivityRepository @Inject constructor(
  private val dao: ActivityDao
) : ActivityRepository {

  override fun getActivitiesForDate(date: LocalDate): Flow<List<ActivityEntry>> =
    dao.getActivitiesForDate(date).map { list -> list.map { it.toDomain() } }

  override suspend fun saveActivity(entry: ActivityEntry): Long =
    dao.insert(ActivityEntryEntity.fromDomain(entry))

  override suspend fun deleteActivity(id: Long) =
    dao.deleteById(id)

  override suspend fun getTotalKcalBurned(date: LocalDate): Int =
    dao.getTotalKcalBurned(date)
}
