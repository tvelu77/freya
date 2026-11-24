package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.DailyNutrition
import io.tvelu77.freya.domain.ports.api.GetDailyNutritionUseCase
import io.tvelu77.freya.domain.ports.spi.FoodRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class GetDailyNutritionUseCaseAdapter @Inject constructor(
  private val repository: FoodRepository
) : GetDailyNutritionUseCase {

  override fun getForDay(date: LocalDate): Flow<DailyNutrition> =
    repository.getFoodEntriesForDate(date).map { DailyNutrition.compute(date, it) }
}
