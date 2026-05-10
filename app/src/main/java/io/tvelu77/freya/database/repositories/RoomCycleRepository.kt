package io.tvelu77.freya.database.repositories

import io.tvelu77.freya.database.dao.CycleDao
import io.tvelu77.freya.database.entities.CycleEntryEntity
import io.tvelu77.freya.domain.models.CycleEntry
import io.tvelu77.freya.domain.ports.spi.CycleRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class RoomCycleRepository @Inject constructor(
  private val dao: CycleDao
) : CycleRepository {
  override fun getAllCycles(): Flow<List<CycleEntry>> =
    dao.getAllCycles().map { list -> list.map { it.toDomain() } }

  override fun getLatestCycle(): Flow<CycleEntry?> =
    dao.getLatestCycle().map { it?.toDomain() }

  override suspend fun getAllCyclesOnce(): List<CycleEntry> =
    dao.getAllCyclesOnce().map { it.toDomain() }

  override suspend fun saveCycle(entry: CycleEntry): Long =
    dao.insert(CycleEntryEntity.fromDomain(entry))

  override suspend fun updateCycle(entry: CycleEntry) =
    dao.update(CycleEntryEntity.fromDomain(entry))

  override suspend fun deleteCycle(id: Long) =
    dao.deleteById(id)

  override suspend fun getCycleByDate(date: LocalDate): CycleEntry? =
    dao.getCycleByDate(date)?.toDomain()
}