package io.tvelu77.freya.domain.services

import io.mockk.coEvery
import io.mockk.mockk
import io.tvelu77.freya.domain.ports.spi.FoodRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class TCAGuardUseCaseAdapterTest {

  private lateinit var repository: FoodRepository
  private lateinit var adapter: TCAGuardUseCaseAdapter

  private val today = LocalDate.of(2024, 6, 10)

  @Before
  fun setup() {
    repository = mockk()
    adapter = TCAGuardUseCaseAdapter(repository)
  }

  @Test
  fun `alert triggers after 2 consecutive low-intake days`() = runTest {
    coEvery { repository.getTotalCaloriesForDate(today) } returns 800
    coEvery { repository.getTotalCaloriesForDate(today.minusDays(1)) } returns 900
    coEvery { repository.getTotalCaloriesForDate(today.minusDays(2)) } returns 2000

    val alert = adapter.checkLastDays(days = 3, today = today)

    assertTrue(alert.triggered)
    assertEquals(2, alert.consecutiveLowDays)
    assertTrue(alert.message.isNotEmpty())
  }

  @Test
  fun `no alert when only one day is low`() = runTest {
    coEvery { repository.getTotalCaloriesForDate(today) } returns 800
    coEvery { repository.getTotalCaloriesForDate(today.minusDays(1)) } returns 2000
    coEvery { repository.getTotalCaloriesForDate(today.minusDays(2)) } returns 2000

    val alert = adapter.checkLastDays(days = 3, today = today)

    assertFalse(alert.triggered)
    assertEquals(1, alert.consecutiveLowDays)
  }

  @Test
  fun `a day with zero logged calories breaks the streak`() = runTest {
    coEvery { repository.getTotalCaloriesForDate(today) } returns 0
    coEvery { repository.getTotalCaloriesForDate(today.minusDays(1)) } returns 800
    coEvery { repository.getTotalCaloriesForDate(today.minusDays(2)) } returns 800

    val alert = adapter.checkLastDays(days = 3, today = today)

    assertFalse(alert.triggered)
    assertEquals(0, alert.consecutiveLowDays)
  }

  @Test
  fun `alert triggers when all checked days are low`() = runTest {
    coEvery { repository.getTotalCaloriesForDate(any()) } returns 500

    val alert = adapter.checkLastDays(days = 3, today = today)

    assertTrue(alert.triggered)
    assertEquals(3, alert.consecutiveLowDays)
  }
}
