package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.FoodEntryFactory
import io.tvelu77.freya.domain.ports.spi.FakeFoodRepository
import io.tvelu77.freya.domain.ports.spi.FakeUserProfileRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class TCAGuardUseCaseAdapterTest {

  private val repository = FakeFoodRepository()
  private val profileRepository = FakeUserProfileRepository()
  private val adapter = TCAGuardUseCaseAdapter(repository, profileRepository)

  private val today = LocalDate.of(2024, 6, 10)

  private suspend fun logCalories(date: LocalDate, calories: Int) {
    repository.saveFoodEntry(FoodEntryFactory.foodEntry(date = date, calories = calories))
  }

  @Test
  fun `alert triggers after 3 consecutive low-intake days`() = runTest {
    logCalories(today, 800)
    logCalories(today.minusDays(1), 900)
    logCalories(today.minusDays(2), 1000)

    val alert = adapter.checkLastDays(days = 3, today = today)

    assertTrue(alert.triggered)
    assertEquals(3, alert.consecutiveLowDays)
    assertTrue(alert.message.isNotEmpty())
    assertTrue(alert.ctaLabel.isNotEmpty())
  }

  @Test
  fun `no alert after only 1 or 2 low days`() = runTest {
    logCalories(today, 800)
    logCalories(today.minusDays(1), 2000)
    logCalories(today.minusDays(2), 2000)
    assertFalse(adapter.checkLastDays(3, today).triggered)
    assertEquals(1, adapter.checkLastDays(3, today).consecutiveLowDays)

    repository.entries.clear()
    logCalories(today, 800)
    logCalories(today.minusDays(1), 900)
    logCalories(today.minusDays(2), 2000)
    val alert = adapter.checkLastDays(3, today)
    assertFalse(alert.triggered)
    assertEquals(2, alert.consecutiveLowDays)
  }

  @Test
  fun `a normal day resets the consecutive count`() = runTest {
    logCalories(today, 2000)
    logCalories(today.minusDays(1), 800)
    logCalories(today.minusDays(2), 800)

    val alert = adapter.checkLastDays(3, today)

    assertFalse(alert.triggered)
    assertEquals(0, alert.consecutiveLowDays)
  }

  @Test
  fun `a day with nothing logged breaks the streak in normal mode`() = runTest {
    logCalories(today.minusDays(1), 800)
    logCalories(today.minusDays(2), 800)

    val alert = adapter.checkLastDays(3, today)

    assertFalse(alert.triggered)
    assertEquals(0, alert.consecutiveLowDays)
  }

  @Test
  fun `intake exactly at the soft threshold is not low, just below is`() = runTest {
    logCalories(today, TCAGuardUseCaseAdapter.LOW_INTAKE_THRESHOLD_KCAL)
    logCalories(today.minusDays(1), 800)
    logCalories(today.minusDays(2), 800)
    assertFalse(adapter.checkLastDays(3, today).triggered)

    repository.entries.clear()
    logCalories(today, TCAGuardUseCaseAdapter.LOW_INTAKE_THRESHOLD_KCAL - 1)
    logCalories(today.minusDays(1), 800)
    logCalories(today.minusDays(2), 800)
    val alert = adapter.checkLastDays(3, today)
    assertTrue(alert.triggered)
  }

  @Test
  fun `alert message is supportive and never shows a kcal figure`() = runTest {
    logCalories(today, 500)
    logCalories(today.minusDays(1), 500)
    logCalories(today.minusDays(2), 500)

    val alert = adapter.checkLastDays(3, today)

    assertTrue(alert.triggered)
    assertTrue(alert.message.none { it.isDigit() })
    assertTrue(alert.message.isNotBlank())
  }

  @Test
  fun `in no-kcal mode days with nothing logged count as qualitative low-signal days`() = runTest {
    profileRepository.profile = profileRepository.profile.copy(tcaFriendlyMode = true)

    val alert = adapter.checkLastDays(3, today)

    assertTrue(alert.triggered)
    assertEquals(3, alert.consecutiveLowDays)
  }

  @Test
  fun `in no-kcal mode low calorie days still trigger the alert`() = runTest {
    profileRepository.profile = profileRepository.profile.copy(tcaFriendlyMode = true)
    logCalories(today, 700)
    logCalories(today.minusDays(1), 700)
    logCalories(today.minusDays(2), 700)

    assertTrue(adapter.checkLastDays(3, today).triggered)
  }

  @Test
  fun `in no-kcal mode a logged normal day resets the qualitative streak`() = runTest {
    profileRepository.profile = profileRepository.profile.copy(tcaFriendlyMode = true)
    logCalories(today, 2000)

    val alert = adapter.checkLastDays(3, today)

    assertFalse(alert.triggered)
    assertEquals(0, alert.consecutiveLowDays)
  }
}
