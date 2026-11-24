package io.tvelu77.freya.database.config

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.tvelu77.freya.database.converters.LocalDateConverter
import io.tvelu77.freya.database.dao.ActivityDao
import io.tvelu77.freya.database.dao.CycleDao
import io.tvelu77.freya.database.dao.DailyActivityDao
import io.tvelu77.freya.database.dao.FoodDao
import io.tvelu77.freya.database.dao.FoodItemDao
import io.tvelu77.freya.database.dao.HydrationDao
import io.tvelu77.freya.database.entities.ActivityEntryEntity
import io.tvelu77.freya.database.entities.CycleEntryEntity
import io.tvelu77.freya.database.entities.DailyActivityEntity
import io.tvelu77.freya.database.entities.HydrationEntity
import io.tvelu77.freya.database.entities.FoodEntryEntity
import io.tvelu77.freya.database.entities.FoodItemEntity

@Database(
  entities = [
    CycleEntryEntity::class,
    FoodEntryEntity::class,
    FoodItemEntity::class,
    ActivityEntryEntity::class,
    DailyActivityEntity::class,
    HydrationEntity::class
  ],
  version = 5,
  exportSchema = true
)
@TypeConverters(LocalDateConverter::class)
abstract class FreyaDatabase: RoomDatabase() {
  abstract fun cycleDao(): CycleDao
  abstract fun foodDao(): FoodDao
  abstract fun foodItemDao(): FoodItemDao
  abstract fun activityDao(): ActivityDao
  abstract fun dailyActivityDao(): DailyActivityDao
  abstract fun hydrationDao(): HydrationDao
}