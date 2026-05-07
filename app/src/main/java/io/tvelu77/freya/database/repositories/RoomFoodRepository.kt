package io.tvelu77.freya.database.repositories

import io.tvelu77.freya.database.dao.FoodDao
import io.tvelu77.freya.database.entities.FoodEntryEntity
import io.tvelu77.freya.domain.models.FoodEntry
import io.tvelu77.freya.domain.ports.spi.FoodRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class RoomFoodRepository @Inject constructor(
  private val dao: FoodDao
) : FoodRepository {

  override fun getFoodEntriesForDate(date: LocalDate): Flow<List<FoodEntry>> =
    dao.getFoodEntriesForDate(date).map { list -> list.map { it.toDomain() } }

  override fun getFoodEntriesForDateRange(from: LocalDate, to: LocalDate): Flow<List<FoodEntry>> =
    dao.getFoodEntriesForDateRange(from, to).map { list -> list.map { it.toDomain() } }

  override suspend fun saveFoodEntry(entry: FoodEntry): Long =
    dao.insert(FoodEntryEntity.fromDomain(entry))

  override suspend fun deleteFoodEntry(id: Long) =
    dao.deleteById(id)

  override suspend fun getTotalCaloriesForDate(date: LocalDate): Int =
    dao.getTotalCaloriesForDate(date)
}