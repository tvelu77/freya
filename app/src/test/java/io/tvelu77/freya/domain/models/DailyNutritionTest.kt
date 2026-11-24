package io.tvelu77.freya.domain.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class DailyNutritionTest {

  private val date = LocalDate.of(2024, 6, 10)

  @Test
  fun `compute with no entries returns zero totals and no meals`() {
    val summary = DailyNutrition.compute(date, emptyList())

    assertEquals(date, summary.date)
    assertEquals(0, summary.totalCalories)
    assertEquals(0f, summary.totalProteins, 0.001f)
    assertEquals(0f, summary.totalCarbs, 0.001f)
    assertEquals(0f, summary.totalFats, 0.001f)
    assertTrue(summary.meals.isEmpty())
  }

  @Test
  fun `compute sums totals across all entries of the day`() {
    val entries = listOf(
      FoodEntryFactory.foodEntry(date = date, calories = 300, proteins = 10f, carbs = 30f, fats = 5f),
      FoodEntryFactory.foodEntry(date = date, calories = 200, proteins = 5f, carbs = 20f, fats = 2.5f, mealType = MealType.DINNER)
    )

    val summary = DailyNutrition.compute(date, entries)

    assertEquals(500, summary.totalCalories)
    assertEquals(15f, summary.totalProteins, 0.001f)
    assertEquals(50f, summary.totalCarbs, 0.001f)
    assertEquals(7.5f, summary.totalFats, 0.001f)
  }

  @Test
  fun `compute groups entries per meal type`() {
    val entries = listOf(
      FoodEntryFactory.foodEntry(date = date, calories = 300, proteins = 10f, carbs = 30f, fats = 5f, mealType = MealType.BREAKFAST),
      FoodEntryFactory.foodEntry(date = date, calories = 100, proteins = 2f, carbs = 10f, fats = 1f, mealType = MealType.BREAKFAST),
      FoodEntryFactory.foodEntry(date = date, calories = 450, proteins = 20f, carbs = 40f, fats = 15f, mealType = MealType.LUNCH)
    )

    val summary = DailyNutrition.compute(date, entries)

    assertEquals(2, summary.meals.size)
    val breakfast = summary.meals.getValue(MealType.BREAKFAST)
    assertEquals(MealType.BREAKFAST, breakfast.mealType)
    assertEquals(400, breakfast.calories)
    assertEquals(12f, breakfast.proteins, 0.001f)
    assertEquals(40f, breakfast.carbs, 0.001f)
    assertEquals(6f, breakfast.fats, 0.001f)
    val lunch = summary.meals.getValue(MealType.LUNCH)
    assertEquals(450, lunch.calories)
    assertEquals(20f, lunch.proteins, 0.001f)
  }

  @Test
  fun `compute keeps every meal type present in the entries`() {
    val entries = MealType.entries.map { meal ->
      FoodEntryFactory.foodEntry(date = date, calories = 100, mealType = meal)
    }

    val summary = DailyNutrition.compute(date, entries)

    assertEquals(MealType.entries.toSet(), summary.meals.keys)
    assertEquals(400, summary.totalCalories)
  }
}
