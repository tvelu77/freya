package io.tvelu77.freya.database.config

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.tvelu77.freya.database.dao.ActivityDao
import io.tvelu77.freya.database.dao.CycleDao
import io.tvelu77.freya.database.dao.DailyActivityDao
import io.tvelu77.freya.database.dao.FoodDao
import io.tvelu77.freya.database.dao.FoodItemDao
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

  @Provides
  @Singleton
  fun provideDatabase(@ApplicationContext context: Context): FreyaDatabase =
    Room.databaseBuilder(context, FreyaDatabase::class.java, "freya.db")
      .fallbackToDestructiveMigration(true)
      .build()

  @Provides fun provideCycleDao(db: FreyaDatabase): CycleDao = db.cycleDao()
  @Provides fun provideFoodDao(db: FreyaDatabase): FoodDao = db.foodDao()
  @Provides fun provideFoodItemDao(db: FreyaDatabase): FoodItemDao = db.foodItemDao()
  @Provides fun provideActivityDao(db: FreyaDatabase): ActivityDao = db.activityDao()
  @Provides fun provideDailyActivityDao(db: FreyaDatabase): DailyActivityDao = db.dailyActivityDao()

  @Provides @Singleton
  fun provideUserProfileDataStore(
    @ApplicationContext context: Context
  ): UserProfileDataStore = UserProfileDataStore(context)
}
