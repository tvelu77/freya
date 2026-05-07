package io.tvelu77.freya.database.config

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.tvelu77.freya.database.dao.CycleDao
import io.tvelu77.freya.database.dao.FoodDao
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

  @Provides
  @Singleton
  fun provideDatabase(@ApplicationContext context: Context): FreyaDatabase =
    Room.databaseBuilder(context, FreyaDatabase::class.java, "freya.db")
      .build()

  @Provides fun provideCycleDao(db: FreyaDatabase): CycleDao = db.cycleDao()
  @Provides fun provideFoodDao(db: FreyaDatabase): FoodDao = db.foodDao()

}