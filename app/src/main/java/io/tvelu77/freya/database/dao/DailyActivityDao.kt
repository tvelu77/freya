package io.tvelu77.freya.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.tvelu77.freya.database.entities.DailyActivityEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface DailyActivityDao {

  @Query("SELECT * FROM daily_activity WHERE date = :date")
  fun getDailyActivity(date: LocalDate): Flow<DailyActivityEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsert(entity: DailyActivityEntity)
}
