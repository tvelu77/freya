package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.FoodEntryFactory
import io.tvelu77.freya.domain.models.ScoreLevel
import io.tvelu77.freya.domain.ports.spi.FakeFoodRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class GetHealthScoreUseCaseAdapterTest {

  private val repository = FakeFoodRepository()
  private val adapter = GetHealthScoreUseCaseAdapter(repository)

  private val date = LocalDate.of(2024, 6, 10)

  @Test
  fun `score is GOOD when nutrition, hydration and activity are on target`() = runTest {
    repository.saveFoodEntry(FoodEntryFactory.foodEntry(date = date, calories = 2000))

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
    repository.saveFoodEntry(FoodEntryFactory.foodEntry(date = date, calories = 1100))

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
    repository.saveFoodEntry(FoodEntryFactory.foodEntry(date = date, calories = 400))

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
    repository.saveFoodEntry(FoodEntryFactory.foodEntry(date = date, calories = 1000))
    repository.saveFoodEntry(FoodEntryFactory.foodEntry(date = date, calories = 1000))

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
