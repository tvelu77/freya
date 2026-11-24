package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.ActivityEntry
import io.tvelu77.freya.domain.models.DailyActivity
import io.tvelu77.freya.domain.models.DailyHydration
import io.tvelu77.freya.domain.models.FatigueLevel
import io.tvelu77.freya.domain.models.FoodEntryFactory
import io.tvelu77.freya.domain.models.ScoreLevel
import io.tvelu77.freya.domain.models.SportType
import io.tvelu77.freya.domain.ports.spi.FakeActivityRepository
import io.tvelu77.freya.domain.ports.spi.FakeDailyActivityRepository
import io.tvelu77.freya.domain.ports.spi.FakeFoodRepository
import io.tvelu77.freya.domain.ports.spi.FakeHydrationRepository
import io.tvelu77.freya.domain.ports.spi.FakeUserProfileRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class GetHealthScoreUseCaseAdapterTest {

  private val foodRepository = FakeFoodRepository()
  private val hydrationRepository = FakeHydrationRepository()
  private val dailyActivityRepository = FakeDailyActivityRepository()
  private val activityRepository = FakeActivityRepository()
  private val profileRepository = FakeUserProfileRepository()
  private val adapter = GetHealthScoreUseCaseAdapter(
    foodRepository,
    hydrationRepository,
    dailyActivityRepository,
    activityRepository,
    profileRepository
  )

  private val date = LocalDate.of(2024, 6, 10)

  @Test
  fun `score is neutral when nothing was logged for the day`() = runTest {
    val score = adapter.getScoreForDay(date).first()

    assertEquals(50, score.value)
    assertEquals(ScoreLevel.MEDIUM, score.level)
  }

  @Test
  fun `score is GOOD when nutrition, hydration, movement and signals are all reported`() = runTest {
    foodRepository.saveFoodEntry(FoodEntryFactory.foodEntry(date = date, calories = 2000))
    hydrationRepository.saveHydration(DailyHydration(date = date, waterMl = 2300))
    dailyActivityRepository.saveDailyActivity(
      DailyActivity(date = date, steps = 10000, sleepHours = 8f, fatigue = FatigueLevel.LOW)
    )

    val score = adapter.getScoreForDay(date).first()

    assertEquals(100, score.nutritionScore)
    assertEquals(100, score.hydrationScore)
    assertEquals(100, score.activityScore)
    assertEquals(100, score.signalsScore)
    assertEquals(ScoreLevel.GOOD, score.level)
  }

  @Test
  fun `score sums the calories of all entries of the day`() = runTest {
    foodRepository.saveFoodEntry(FoodEntryFactory.foodEntry(date = date, calories = 1000))
    foodRepository.saveFoodEntry(FoodEntryFactory.foodEntry(date = date, calories = 1000))

    val score = adapter.getScoreForDay(date).first()

    assertEquals(100, score.nutritionScore)
  }

  @Test
  fun `sport minutes count as movement`() = runTest {
    activityRepository.saveActivity(
      ActivityEntry(date = date, sportType = SportType.STRENGTH, durationMin = 45, kcalBurned = 270)
    )

    val score = adapter.getScoreForDay(date).first()

    assertEquals(45, score.activityScore)
  }

  @Test
  fun `score is LOW with an encouraging message when the day was very light`() = runTest {
    foodRepository.saveFoodEntry(FoodEntryFactory.foodEntry(date = date, calories = 400))
    hydrationRepository.saveHydration(DailyHydration(date = date, waterMl = 200))
    dailyActivityRepository.saveDailyActivity(
      DailyActivity(date = date, steps = 0, sleepHours = 8f, fatigue = FatigueLevel.HIGH)
    )

    val score = adapter.getScoreForDay(date).first()

    assertEquals(ScoreLevel.LOW, score.level)
  }

  @Test
  fun `water goal follows the dynamic hydration goal of the day`() = runTest {
    dailyActivityRepository.saveDailyActivity(
      DailyActivity(date = date, steps = 10000, sleepHours = 8f)
    )
    hydrationRepository.saveHydration(DailyHydration(date = date, waterMl = 2300))

    val score = adapter.getScoreForDay(date).first()

    assertEquals(100, score.hydrationScore)
  }
}
