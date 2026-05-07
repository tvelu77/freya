package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.HealthScore
import io.tvelu77.freya.domain.ports.api.GetHealthScoreUseCase
import io.tvelu77.freya.domain.ports.spi.FoodRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class GetHealthScoreUseCaseAdapter @Inject constructor(
  private val repository: FoodRepository
): GetHealthScoreUseCase {

  override fun getScoreForDay(
    date: LocalDate,
    caloriesTarget: Int,
    waterMl: Int,
    waterTargetMl: Int,
    stepsCount: Int
  ): Flow<HealthScore> =
    repository.getFoodEntriesForDate(date).map { foodEntries ->
      val totalCalories = foodEntries.sumOf { it.calories }
      HealthScore.compute(
        caloriesConsumed = totalCalories,
        caloriesTarget = caloriesTarget,
        waterMl = waterMl,
        waterTargetMl = waterTargetMl,
        stepsCount = stepsCount
      )
    }

}