package io.tvelu77.freya.domain.services

import io.mockk.every
import io.mockk.mockk
import io.tvelu77.freya.domain.models.FoodEntry
import io.tvelu77.freya.domain.models.MealType
import io.tvelu77.freya.domain.models.ScoreLevel
import io.tvelu77.freya.domain.ports.spi.FoodRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class GetHealthScoreUseCaseAdapterTest {

  private lateinit var repository: FoodRepository
  private lateinit var adapter: GetHealthScoreUseCaseAdapter

  private val date = LocalDate.of(2024, 6, 10)

  @Before
  fun setup() {
    repository = mockk()
    adapter = GetHealthScoreUseCaseAdapter(repository)
  }

  private fun foodEntry(calories: Int) = FoodEntry(
    date = date,
    time = LocalTime.of(12, 0),
    foodName = "Test",
    quantity = 100f,
    calories = calories,
    proteins = 10f,
    carbs = 20f,
    fats = 5f,
    mealType = MealType.LUNCH
  )

  @Test
  fun `score is GOOD when nutrition, hydration and activity are on target`() = runTest {
    every { repository.getFoodEntriesForDate(date) } returns flowOf(listOf(foodEntry(2000)))

    val score = adapter.getScoreForDay(
      date = date,
      caloriesTarget = 2000,
      waterMl = 2000,
      waterTargetMl = 2000,
      stepsCount = 10000
    ).first()

    assertEquals(100, score.value)
    assertEquals(ScoreLevel.GOOD, score.level)
    assertEquals("Tu as bien nourri ton corps aujourd'hui", score.message)
  }

  @Test
  fun `score is MEDIUM for a partially complete day`() = runTest {
    every { repository.getFoodEntriesForDate(date) } returns flowOf(listOf(foodEntry(1100)))

    val score = adapter.getScoreForDay(
      date = date,
      caloriesTarget = 2000,
      waterMl = 1500,
      waterTargetMl = 2000,
      stepsCount = 5000
    ).first()

    assertEquals(ScoreLevel.MEDIUM, score.level)
    assertEquals("Tu peux essayer de boire un peu plus demain", score.message)
  }

  @Test
  fun `score is LOW when intake and activity are very low`() = runTest {
    every { repository.getFoodEntriesForDate(date) } returns flowOf(listOf(foodEntry(400)))

    val score = adapter.getScoreForDay(
      date = date,
      caloriesTarget = 2000,
      waterMl = 0,
      waterTargetMl = 2000,
      stepsCount = 0
    ).first()

    assertEquals(ScoreLevel.LOW, score.level)
    assertEquals("Ton corps a peut-être besoin de plus d'énergie", score.message)
  }

  @Test
  fun `score sums calories of all entries of the day`() = runTest {
    every { repository.getFoodEntriesForDate(date) } returns
      flowOf(listOf(foodEntry(1000), foodEntry(1000)))

    val score = adapter.getScoreForDay(
      date = date,
      caloriesTarget = 2000,
      waterMl = 2000,
      waterTargetMl = 2000,
      stepsCount = 10000
    ).first()

    assertEquals(100, score.nutritionScore)
  }
}
