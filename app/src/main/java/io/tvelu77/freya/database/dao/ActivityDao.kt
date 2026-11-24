package io.tvelu77.freya.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.tvelu77.freya.database.entities.ActivityEntryEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface ActivityDao {

  @Query("SELECT * FROM activity_entries WHERE date = :date ORDER BY id")
  fun getActivitiesForDate(date: LocalDate): Flow<List<ActivityEntryEntity>>

  @Query("SELECT COALESCE(SUM(kcalBurned), 0) FROM activity_entries WHERE date = :date")
  suspend fun getTotalKcalBurned(date: LocalDate): Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(entry: ActivityEntryEntity): Long

  @Query("DELETE FROM activity_entries WHERE id = :id")
  suspend fun deleteById(id: Long)
}
