package io.tvelu77.freya.domain.models

import java.time.LocalDate
import java.time.LocalTime

data class FoodEntry(
  val id: Long = 0,
  val date: LocalDate,
  val time: LocalTime,
  val foodName: String,
  val quantity: Float,
  val calories: Int,
  val proteins: Float,
  val carbs: Float,
  val fats: Float,
  val mealType: MealType
)
