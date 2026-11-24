package io.tvelu77.freya.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.tvelu77.freya.database.entities.HydrationEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface HydrationDao {

  @Query("SELECT * FROM hydration WHERE date = :date")
  fun getHydration(date: LocalDate): Flow<HydrationEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsert(entity: HydrationEntity)
}
