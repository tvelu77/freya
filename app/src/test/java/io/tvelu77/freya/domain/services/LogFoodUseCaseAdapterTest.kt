package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.FoodCategory
import io.tvelu77.freya.domain.models.FoodEntryFactory
import io.tvelu77.freya.domain.models.FoodItem
import io.tvelu77.freya.domain.models.MealType
import io.tvelu77.freya.domain.ports.spi.FakeFoodRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class LogFoodUseCaseAdapterTest {

  private val repository = FakeFoodRepository()
  private val adapter = LogFoodUseCaseAdapter(repository)

  private val date = LocalDate.of(2024, 6, 10)
  private val time = LocalTime.of(12, 30)

  private val rice = FoodItem(
    id = 1,
    name = "Riz blanc cuit",
    caloriesPer100g = 130,
    proteinsPer100g = 2.7f,
    carbsPer100g = 28f,
    fatsPer100g = 0.3f,
    category = FoodCategory.CEREAL
  )

  @Test
  fun `logFood derives kcal and macros in the domain from per-100g values and grams`() = runTest {
    val id = adapter.logFood(rice, quantityG = 150f, mealType = MealType.LUNCH, date = date, time = time)

    assertEquals(1L, id)
    val saved = repository.entries.single()
    assertEquals("Riz blanc cuit", saved.foodName)
    assertEquals(150f, saved.quantity)
    assertEquals(195, saved.calories)
    assertEquals(4.05f, saved.proteins, 0.001f)
    assertEquals(42f, saved.carbs, 0.001f)
    assertEquals(0.45f, saved.fats, 0.001f)
    assertEquals(MealType.LUNCH, saved.mealType)
    assertEquals(date, saved.date)
    assertEquals(time, saved.time)
  }

  @Test
  fun `logFood with a preset-sized portion stores the matching grams`() = runTest {
    val egg = FoodItem(
      id = 2,
      name = "Œuf entier",
      caloriesPer100g = 155,
      proteinsPer100g = 13f,
      carbsPer100g = 1.1f,
      fatsPer100g = 11f,
      category = FoodCategory.PROTEIN,
      defaultServingG = 50f
    )

    adapter.logFood(egg, quantityG = 50f, mealType = MealType.BREAKFAST)

    val saved = repository.entries.single()
    assertEquals(50f, saved.quantity)
    assertEquals(77, saved.calories)
    assertEquals(MealType.BREAKFAST, saved.mealType)
  }

  @Test
  fun `getFoodForDay returns the entries of that day only`() = runTest {
    repository.saveFoodEntry(
      FoodEntryFactory.foodEntry(date = date, calories = 500, foodName = "Poulet")
    )
    repository.saveFoodEntry(
      FoodEntryFactory.foodEntry(date = date.plusDays(1), calories = 999, foodName = "Autre jour")
    )

    val entries = adapter.getFoodForDay(date).first()

    assertEquals(1, entries.size)
    assertEquals("Poulet", entries.single().foodName)
  }

  @Test
  fun `getTotalCalories sums the entries of the day`() = runTest {
    repository.saveFoodEntry(FoodEntryFactory.foodEntry(date = date, calories = 1000))
    repository.saveFoodEntry(FoodEntryFactory.foodEntry(date = date, calories = 450))

    assertEquals(1450, adapter.getTotalCalories(date))
  }

  @Test
  fun `deleteFood removes the entry`() = runTest {
    val id = repository.saveFoodEntry(FoodEntryFactory.foodEntry(date = date, calories = 300))

    adapter.deleteFood(id)

    assertTrue(repository.entries.isEmpty())
  }
}
