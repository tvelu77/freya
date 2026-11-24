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

  override suspend fun updateFoodEntry(entry: FoodEntry) {
    repository.saveFoodEntry(entry)
  }

  override suspend fun updateFoodQuantity(id: Long, quantityG: Float) {
    val entry = repository.getFoodEntryById(id) ?: return
    val safeQuantity = quantityG.coerceAtLeast(0f)
    val scale = if (entry.quantity > 0f) safeQuantity / entry.quantity else 1f
    repository.saveFoodEntry(
      entry.copy(
        quantity = safeQuantity,
        calories = (entry.calories * scale).toInt(),
        proteins = entry.proteins * scale,
        carbs = entry.carbs * scale,
        fats = entry.fats * scale
      )
    )
  }

  override suspend fun getTotalCalories(date: LocalDate): Int =
    repository.getTotalCaloriesForDate(date)
}
