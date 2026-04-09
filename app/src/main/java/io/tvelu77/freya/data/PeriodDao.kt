package io.tvelu77.freya.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.tvelu77.freya.models.Period
import kotlinx.coroutines.flow.Flow

@Dao
interface PeriodDao {

    @Query("SELECT * FROM periods ORDER BY startDate DESC")
    fun findAll(): Flow<List<Period>>

    @Query("SELECT * FROM periods ORDER BY startDate DESC LIMIT 1")
    fun findLast(): Flow<Period?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(period: Period)

    @Update
    suspend fun update(period: Period)

}