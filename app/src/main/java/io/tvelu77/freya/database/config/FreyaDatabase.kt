package io.tvelu77.freya.database.config

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.tvelu77.freya.database.converters.LocalDateConverter
import io.tvelu77.freya.database.dao.CycleDao
import io.tvelu77.freya.database.dao.FoodDao
import io.tvelu77.freya.database.dao.FoodItemDao
import io.tvelu77.freya.database.entities.CycleEntryEntity
import io.tvelu77.freya.database.entities.FoodEntryEntity
import io.tvelu77.freya.database.entities.FoodItemEntity

@Database(
  entities = [CycleEntryEntity::class, FoodEntryEntity::class, FoodItemEntity::class],
  version = 2,
  exportSchema = true
)
@TypeConverters(LocalDateConverter::class)
abstract class FreyaDatabase: RoomDatabase() {
  abstract fun cycleDao(): CycleDao
  abstract fun foodDao(): FoodDao
  abstract fun foodItemDao(): FoodItemDao
}