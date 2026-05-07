package io.tvelu77.freya.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.tvelu77.freya.database.entities.FoodEntryEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface FoodDao {

  @Query("SELECT * FROM food_entries WHERE date = :date ORDER BY timeHour, timeMinute")
  fun getFoodEntriesForDate(date: LocalDate): Flow<List<FoodEntryEntity>>

  @Query("SELECT * FROM food_entries WHERE date >= :from AND date <= :to ORDER BY date, timeHour")
  fun getFoodEntriesForDateRange(from: LocalDate, to: LocalDate): Flow<List<FoodEntryEntity>>

  @Query("SELECT COALESCE(SUM(calories), 0) FROM food_entries WHERE date = :date")
  suspend fun getTotalCaloriesForDate(date: LocalDate): Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(entry: FoodEntryEntity): Long

  @Query("DELETE FROM food_entries WHERE id = :id")
  suspend fun deleteById(id: Long)

}