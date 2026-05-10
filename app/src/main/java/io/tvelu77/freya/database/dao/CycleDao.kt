package io.tvelu77.freya.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.tvelu77.freya.database.entities.CycleEntryEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface CycleDao {

  @Query("SELECT * FROM cycle_entries ORDER BY startDate DESC")
  fun getAllCycles(): Flow<List<CycleEntryEntity>>

  @Query("SELECT * FROM cycle_entries ORDER BY startDate DESC LIMIT 1")
  fun getLatestCycle(): Flow<CycleEntryEntity?>

  @Query("SELECT * FROM cycle_entries ORDER BY startDate DESC")
  suspend fun getAllCyclesOnce(): List<CycleEntryEntity>

  @Query("SELECT * FROM cycle_entries WHERE startDate <= :date AND (endDate IS NULL OR endDate >= :date) LIMIT 1")
  suspend fun getCycleByDate(date: LocalDate): CycleEntryEntity?

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insert(entry: CycleEntryEntity): Long

  @Update
  suspend fun update(entry: CycleEntryEntity)

  @Query("DELETE FROM cycle_entries WHERE id = :id")
  suspend fun deleteById(id: Long)
}