package io.tvelu77.freya.database.repositories

import io.tvelu77.freya.database.dao.DailyActivityDao
import io.tvelu77.freya.database.entities.DailyActivityEntity
import io.tvelu77.freya.domain.models.DailyActivity
import io.tvelu77.freya.domain.ports.spi.DailyActivityRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class RoomDailyActivityRepository @Inject constructor(
  private val dao: DailyActivityDao
) : DailyActivityRepository {

  override fun getDailyActivity(date: LocalDate): Flow<DailyActivity?> =
    dao.getDailyActivity(date).map { it?.toDomain() }

  override suspend fun saveDailyActivity(activity: DailyActivity) =
    dao.upsert(DailyActivityEntity.fromDomain(activity))
}
