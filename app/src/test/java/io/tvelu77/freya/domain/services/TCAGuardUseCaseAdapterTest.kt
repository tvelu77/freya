package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.FoodEntryFactory
import io.tvelu77.freya.domain.ports.spi.FakeFoodRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class TCAGuardUseCaseAdapterTest {

  private val repository = FakeFoodRepository()
  private val adapter = TCAGuardUseCaseAdapter(repository)

  private val today = LocalDate.of(2024, 6, 10)

  private suspend fun logCalories(date: LocalDate, calories: Int) {
    repository.saveFoodEntry(FoodEntryFactory.foodEntry(date = date, calories = calories))
  }

  @Test
  fun `alert triggers after 2 consecutive low-intake days`() = runTest {
    logCalories(today, 800)
    logCalories(today.minusDays(1), 900)
    logCalories(today.minusDays(2), 2000)

    val alert = adapter.checkLastDays(days = 3, today = today)

    assertTrue(alert.triggered)
    assertEquals(2, alert.consecutiveLowDays)
    assertTrue(alert.message.isNotEmpty())
  }

  @Test
  fun `no alert when only one day is low`() = runTest {
    logCalories(today, 800)
    logCalories(today.minusDays(1), 2000)
    logCalories(today.minusDays(2), 2000)

    val alert = adapter.checkLastDays(days = 3, today = today)

    assertFalse(alert.triggered)
    assertEquals(1, alert.consecutiveLowDays)
  }

  @Test
  fun `a day with zero logged calories breaks the streak`() = runTest {
    logCalories(today.minusDays(1), 800)
    logCalories(today.minusDays(2), 800)

    val alert = adapter.checkLastDays(days = 3, today = today)

    assertFalse(alert.triggered)
    assertEquals(0, alert.consecutiveLowDays)
  }

  @Test
  fun `alert triggers when all checked days are low`() = runTest {
    logCalories(today, 500)
    logCalories(today.minusDays(1), 500)
    logCalories(today.minusDays(2), 500)

    val alert = adapter.checkLastDays(days = 3, today = today)

    assertTrue(alert.triggered)
    assertEquals(3, alert.consecutiveLowDays)
  }
}
