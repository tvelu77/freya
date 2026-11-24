package io.tvelu77.freya.domain.ports.spi

import io.tvelu77.freya.domain.models.ActivityEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate

class FakeActivityRepository : ActivityRepository {

  val entries = mutableListOf<ActivityEntry>()

  override fun getActivitiesForDate(date: LocalDate): Flow<List<ActivityEntry>> =
    flowOf(entries.filter { it.date == date })

  override suspend fun saveActivity(entry: ActivityEntry): Long {
    val id = (entries.size + 1).toLong()
    entries.add(entry.copy(id = id))
    return id
  }

  override suspend fun deleteActivity(id: Long) {
    entries.removeAll { it.id == id }
  }

  override suspend fun getTotalKcalBurned(date: LocalDate): Int =
    entries.filter { it.date == date }.sumOf { it.kcalBurned }
}
