package io.tvelu77.freya.domain.ports.spi

import io.tvelu77.freya.domain.models.CycleEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface CycleRepository {
  fun getAllCycles(): Flow<List<CycleEntry>>
  fun getLatestCycle(): Flow<CycleEntry?>
  suspend fun getAllCyclesOnce(): List<CycleEntry>
  suspend fun saveCycle(entry: CycleEntry): Long
  suspend fun updateCycle(entry: CycleEntry)
  suspend fun deleteCycle(id: Long)
  suspend fun getCycleByDate(date: LocalDate): CycleEntry?
}