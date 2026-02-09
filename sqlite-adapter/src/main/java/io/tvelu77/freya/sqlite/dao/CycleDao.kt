package io.tvelu77.freya.sqlite.dao

import androidx.room.Insert
import androidx.room.Query
import io.tvelu77.freya.sqlite.models.CycleEntity

interface CycleDao {

    @Query("SELECT * FROM CycleEntity")
    fun findAll(): List<CycleEntity>

    @Insert
    fun save(cycle: CycleEntity)

}