package io.tvelu77.freya.domain.models

import java.time.LocalDate
import java.time.LocalTime

object FoodEntryFactory {

  fun foodEntry(
    date: LocalDate,
    calories: Int,
    foodName: String = "Test",
    quantity: Float = 100f,
    proteins: Float = 10f,
    carbs: Float = 20f,
    fats: Float = 5f,
    mealType: MealType = MealType.LUNCH,
    time: LocalTime = LocalTime.of(12, 0)
  ): FoodEntry = FoodEntry(
    date = date,
    time = time,
    foodName = foodName,
    quantity = quantity,
    calories = calories,
    proteins = proteins,
    carbs = carbs,
    fats = fats,
    mealType = mealType
  )
}
