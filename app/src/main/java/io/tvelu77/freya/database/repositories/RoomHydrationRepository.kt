package io.tvelu77.freya.database.repositories

import io.tvelu77.freya.database.dao.HydrationDao
import io.tvelu77.freya.database.entities.HydrationEntity
import io.tvelu77.freya.domain.models.DailyHydration
import io.tvelu77.freya.domain.ports.spi.HydrationRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class RoomHydrationRepository @Inject constructor(
  private val dao: HydrationDao
) : HydrationRepository {

  override fun getHydration(date: LocalDate): Flow<DailyHydration?> =
    dao.getHydration(date).map { it?.toDomain() }

  override suspend fun saveHydration(hydration: DailyHydration) =
    dao.upsert(HydrationEntity.fromDomain(hydration))
}
