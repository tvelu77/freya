package io.tvelu77.freya.domain.ports.api

import io.tvelu77.freya.domain.models.FoodEntry
import io.tvelu77.freya.domain.models.MealType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime

interface LogFoodUseCase {

  suspend fun logFood(
    foodName: String,
    quantity: Float,
    calories: Int,
    proteins: Float,
    carbs: Float,
    fats: Float,
    mealType: MealType,
    date: LocalDate = LocalDate.now(),
    time: LocalTime = LocalTime.now()
  ): Long

  fun getFoodForDay(date: LocalDate = LocalDate.now()): Flow<List<FoodEntry>>

  suspend fun deleteFood(id: Long)

  suspend fun getTotalCalories(date: LocalDate = LocalDate.now()): Int

}