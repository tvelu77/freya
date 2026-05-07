package io.tvelu77.freya.database.config

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.tvelu77.freya.domain.ports.api.GetHealthScoreUseCase
import io.tvelu77.freya.domain.ports.api.GetNutritionAdviceUseCase
import io.tvelu77.freya.domain.ports.api.LogFoodUseCase
import io.tvelu77.freya.domain.ports.api.PhaseCalculator
import io.tvelu77.freya.domain.ports.api.TCAGuardUseCase
import io.tvelu77.freya.domain.ports.api.TrackCycleUseCase
import io.tvelu77.freya.domain.services.GetHealthScoreUseCaseAdapter
import io.tvelu77.freya.domain.services.GetNutritionAdviceUseCaseAdapter
import io.tvelu77.freya.domain.services.LogFoodUseCaseAdapter
import io.tvelu77.freya.domain.services.PhaseCalculatorAdapter
import io.tvelu77.freya.domain.services.TCAGuardUseCaseAdapter
import io.tvelu77.freya.domain.services.TrackCycleUseCaseAdapter
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

  @Binds
  @Singleton
  abstract fun bindPhaseCalculator(adapter: PhaseCalculatorAdapter): PhaseCalculator

  @Binds
  @Singleton
  abstract fun bindTrackCycleUseCase(adapter: TrackCycleUseCaseAdapter): TrackCycleUseCase

  @Binds
  @Singleton
  abstract fun bindGetNutritionAdviceUseCase(adapter: GetNutritionAdviceUseCaseAdapter): GetNutritionAdviceUseCase

  @Binds
  @Singleton
  abstract fun bindGetHealthScoreUseCase(adapter: GetHealthScoreUseCaseAdapter): GetHealthScoreUseCase

  @Binds
  @Singleton
  abstract fun bindTCAGuardUseCase(adapter: TCAGuardUseCaseAdapter): TCAGuardUseCase

  @Binds
  @Singleton
  abstract fun bindLogFoodUseCase(adapter: LogFoodUseCaseAdapter): LogFoodUseCase
}
