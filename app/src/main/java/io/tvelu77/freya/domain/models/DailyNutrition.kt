package io.tvelu77.freya.domain.models

import java.time.LocalDate

data class MealNutrition(
  val mealType: MealType,
  val calories: Int,
  val proteins: Float,
  val carbs: Float,
  val fats: Float
)

data class DailyNutrition(
  val date: LocalDate,
  val totalCalories: Int,
  val totalProteins: Float,
  val totalCarbs: Float,
  val totalFats: Float,
  val meals: Map<MealType, MealNutrition>
) {

  companion object {

    fun compute(date: LocalDate, entries: List<FoodEntry>): DailyNutrition {
      val meals = entries
        .groupBy { it.mealType }
        .mapValues { (mealType, mealEntries) ->
          MealNutrition(
            mealType = mealType,
            calories = mealEntries.sumOf { it.calories },
            proteins = mealEntries.sumOf { it.proteins.toDouble() }.toFloat(),
            carbs = mealEntries.sumOf { it.carbs.toDouble() }.toFloat(),
            fats = mealEntries.sumOf { it.fats.toDouble() }.toFloat()
          )
        }
      return DailyNutrition(
        date = date,
        totalCalories = entries.sumOf { it.calories },
        totalProteins = entries.sumOf { it.proteins.toDouble() }.toFloat(),
        totalCarbs = entries.sumOf { it.carbs.toDouble() }.toFloat(),
        totalFats = entries.sumOf { it.fats.toDouble() }.toFloat(),
        meals = meals
      )
    }

    fun empty(date: LocalDate): DailyNutrition = compute(date, emptyList())
  }
}
