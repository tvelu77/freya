package io.tvelu77.freya.domain.services

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.tvelu77.freya.domain.models.FoodEntry
import io.tvelu77.freya.domain.models.MealType
import io.tvelu77.freya.domain.ports.spi.FoodRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class LogFoodUseCaseAdapterTest {

  private lateinit var repository: FoodRepository
  private lateinit var adapter: LogFoodUseCaseAdapter

  private val date = LocalDate.of(2024, 6, 10)
  private val time = LocalTime.of(12, 30)

  @Before
  fun setup() {
    repository = mockk()
    adapter = LogFoodUseCaseAdapter(repository)
  }

  @Test
  fun `logFood saves a mapped entry and returns its id`() = runTest {
    coEvery { repository.saveFoodEntry(any()) } returns 42L

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

    assertEquals(42L, id)
    coVerify {
      repository.saveFoodEntry(match {
        it.foodName == "Riz blanc cuit" &&
          it.quantity == 150f &&
          it.calories == 195 &&
          it.mealType == MealType.LUNCH &&
          it.date == date &&
          it.time == time
      })
    }
  }

  @Test
  fun `getFoodForDay delegates to the repository`() = runTest {
    val entries = listOf(
      FoodEntry(date = date, time = time, foodName = "Poulet", quantity = 100f,
        calories = 165, proteins = 31f, carbs = 0f, fats = 3.6f, mealType = MealType.DINNER)
    )
    every { repository.getFoodEntriesForDate(date) } returns flowOf(entries)

    assertEquals(entries, adapter.getFoodForDay(date).first())
  }

  @Test
  fun `getTotalCalories delegates to the repository`() = runTest {
    coEvery { repository.getTotalCaloriesForDate(date) } returns 1450

    assertEquals(1450, adapter.getTotalCalories(date))
  }

  @Test
  fun `deleteFood delegates to the repository`() = runTest {
    coEvery { repository.deleteFoodEntry(7L) } returns Unit

    adapter.deleteFood(7L)

    coVerify { repository.deleteFoodEntry(7L) }
  }
}
