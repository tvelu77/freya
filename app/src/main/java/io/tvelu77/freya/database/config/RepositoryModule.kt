package io.tvelu77.freya.database.config

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.tvelu77.freya.database.repositories.DataStoreUserProfileRepository
import io.tvelu77.freya.database.repositories.RoomCycleRepository
import io.tvelu77.freya.database.repositories.RoomFoodRepository
import io.tvelu77.freya.domain.ports.spi.CycleRepository
import io.tvelu77.freya.domain.ports.spi.FoodRepository
import io.tvelu77.freya.domain.ports.spi.UserProfileRepository
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

  @Binds
  @Singleton
  abstract fun bindCycleRepository(adapter: RoomCycleRepository): CycleRepository

  @Binds
  @Singleton
  abstract fun bindFoodRepository(adapter: RoomFoodRepository): FoodRepository

  @Binds
  @Singleton
  abstract fun bindUserProfileRepository(adapter: DataStoreUserProfileRepository): UserProfileRepository
}