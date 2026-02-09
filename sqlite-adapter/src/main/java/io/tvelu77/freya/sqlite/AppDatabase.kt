package io.tvelu77.freya.sqlite

import androidx.room.Database
import androidx.room.RoomDatabase
import io.tvelu77.freya.sqlite.dao.CycleDao
import io.tvelu77.freya.sqlite.models.CycleEntity

@Database(entities = [CycleEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cycleDao(): CycleDao

}