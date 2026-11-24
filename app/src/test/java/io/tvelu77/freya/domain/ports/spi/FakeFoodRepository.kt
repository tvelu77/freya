package io.tvelu77.freya.domain.ports.spi

import io.tvelu77.freya.domain.models.FoodEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate

class FakeFoodRepository : FoodRepository {

  val entries = mutableListOf<FoodEntry>()

  override fun getFoodEntriesForDate(date: LocalDate): Flow<List<FoodEntry>> =
    flowOf(entries.filter { it.date == date })

  override fun getFoodEntriesForDateRange(from: LocalDate, to: LocalDate): Flow<List<FoodEntry>> =
    flowOf(entries.filter { it.date in from..to })

  override suspend fun saveFoodEntry(entry: FoodEntry): Long {
    val id = if (entry.id == 0L) (entries.size + 1).toLong() else entry.id
    entries.removeAll { it.id == id }
    entries.add(entry.copy(id = id))
    return id
  }

  override suspend fun getFoodEntryById(id: Long): FoodEntry? =
    entries.firstOrNull { it.id == id }

  override suspend fun deleteFoodEntry(id: Long) {
    entries.removeAll { it.id == id }
  }

  override suspend fun getTotalCaloriesForDate(date: LocalDate): Int =
    entries.filter { it.date == date }.sumOf { it.calories }
}
