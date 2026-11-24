package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.FoodEntryFactory
import io.tvelu77.freya.domain.models.MealType
import io.tvelu77.freya.domain.ports.spi.FakeFoodRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class GetDailyNutritionUseCaseAdapterTest {

  private val repository = FakeFoodRepository()
  private val adapter = GetDailyNutritionUseCaseAdapter(repository)

  private val date = LocalDate.of(2024, 6, 10)

  @Test
  fun `getForDay emits the domain-computed summary of that day only`() = runTest {
    repository.saveFoodEntry(
      FoodEntryFactory.foodEntry(date = date, calories = 300, proteins = 10f, carbs = 30f, fats = 5f, mealType = MealType.LUNCH)
    )
    repository.saveFoodEntry(
      FoodEntryFactory.foodEntry(date = date, calories = 200, mealType = MealType.SNACK)
    )
    repository.saveFoodEntry(
      FoodEntryFactory.foodEntry(date = date.plusDays(1), calories = 999, foodName = "Autre jour")
    )

    val summary = adapter.getForDay(date).first()

    assertEquals(date, summary.date)
    assertEquals(500, summary.totalCalories)
    assertEquals(2, summary.meals.size)
    assertEquals(300, summary.meals.getValue(MealType.LUNCH).calories)
  }

  @Test
  fun `getForDay emits zero totals when the day has no entry`() = runTest {
    val summary = adapter.getForDay(date).first()

    assertEquals(0, summary.totalCalories)
    assertEquals(0f, summary.totalProteins, 0.001f)
  }
}
