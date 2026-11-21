package io.tvelu77.freya.domain.ports.spi

import io.tvelu77.freya.domain.models.CycleEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate

class FakeCycleRepository : CycleRepository {

  val cycles = mutableListOf<CycleEntry>()

  override fun getAllCycles(): Flow<List<CycleEntry>> =
    flowOf(cycles.sortedByDescending { it.startDate })

  override fun getLatestCycle(): Flow<CycleEntry?> =
    flowOf(cycles.maxByOrNull { it.startDate })

  override suspend fun getAllCyclesOnce(): List<CycleEntry> = cycles.toList()

  override suspend fun saveCycle(entry: CycleEntry): Long {
    val id = (cycles.size + 1).toLong()
    cycles.add(entry.copy(id = id))
    return id
  }

  override suspend fun updateCycle(entry: CycleEntry) {
    val index = cycles.indexOfFirst { it.id == entry.id }
    if (index >= 0) cycles[index] = entry
  }

  override suspend fun deleteCycle(id: Long) {
    cycles.removeAll { it.id == id }
  }

  override suspend fun getCycleByDate(date: LocalDate): CycleEntry? =
    cycles
      .filter { it.startDate <= date && (it.endDate == null || it.endDate >= date) }
      .maxByOrNull { it.startDate }
}
