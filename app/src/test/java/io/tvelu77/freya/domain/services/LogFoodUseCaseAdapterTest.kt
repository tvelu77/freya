package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.FoodEntryFactory
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

  @Test
  fun `logFood saves a mapped entry and returns its id`() = runTest {
    val id = adapter.logFood(
      foodName = "Riz blanc cuit",
      quantity = 150f,
      calories = 195,
      proteins = 4f,
      carbs = 42f,
      fats = 0.5f,
      mealType = MealType.LUNCH,
      date = date,
      time = time
    )

    assertEquals(1L, id)
    val saved = repository.entries.single()
    assertEquals("Riz blanc cuit", saved.foodName)
    assertEquals(150f, saved.quantity)
    assertEquals(195, saved.calories)
    assertEquals(MealType.LUNCH, saved.mealType)
    assertEquals(date, saved.date)
    assertEquals(time, saved.time)
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
