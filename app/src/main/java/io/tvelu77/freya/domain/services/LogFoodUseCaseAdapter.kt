package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.FoodEntry
import io.tvelu77.freya.domain.models.FoodItem
import io.tvelu77.freya.domain.models.MealType
import io.tvelu77.freya.domain.ports.api.LogFoodUseCase
import io.tvelu77.freya.domain.ports.spi.FoodRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime

class LogFoodUseCaseAdapter @Inject constructor(
  private val repository: FoodRepository
): LogFoodUseCase {

  override suspend fun logFood(
    food: FoodItem,
    quantityG: Float,
    mealType: MealType,
    date: LocalDate,
    time: LocalTime
  ): Long = repository.saveFoodEntry(
    FoodEntry(
      date = date,
      time = time,
      foodName = food.name,
      quantity = quantityG,
      calories = food.caloriesFor(quantityG),
      proteins = food.proteinsFor(quantityG),
      carbs = food.carbsFor(quantityG),
      fats = food.fatsFor(quantityG),
      mealType = mealType
    )
  )

  override fun getFoodForDay(date: LocalDate): Flow<List<FoodEntry>> =
    repository.getFoodEntriesForDate(date)

  override suspend fun deleteFood(id: Long) = repository.deleteFoodEntry(id)

  override suspend fun getTotalCalories(date: LocalDate): Int =
    repository.getTotalCaloriesForDate(date)
}
