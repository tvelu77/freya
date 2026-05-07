package io.tvelu77.freya.domain.ports.spi

import io.tvelu77.freya.domain.models.FoodEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface FoodRepository {
  fun getFoodEntriesForDate(date: LocalDate): Flow<List<FoodEntry>>
  fun getFoodEntriesForDateRange(from: LocalDate, to: LocalDate): Flow<List<FoodEntry>>
  suspend fun saveFoodEntry(entry: FoodEntry): Long
  suspend fun deleteFoodEntry(id: Long)
  suspend fun getTotalCaloriesForDate(date: LocalDate): Int
}