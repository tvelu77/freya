package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.CycleEntry
import io.tvelu77.freya.domain.models.PhaseType
import io.tvelu77.freya.domain.ports.spi.FakeCycleRepository
import io.tvelu77.freya.domain.ports.spi.FakeDailyActivityRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class TrackDailyActivityUseCaseAdapterTest {

  private val dailyRepository = FakeDailyActivityRepository()
  private val cycleRepository = FakeCycleRepository()
  private val adapter = TrackDailyActivityUseCaseAdapter(
    dailyRepository,
    cycleRepository,
    PhaseCalculatorAdapter()
  )

  private val today = LocalDate.of(2024, 6, 10)

  @Test
  fun `getDailyActivity returns null when nothing was saved`() = runTest {
    assertNull(adapter.getDailyActivity(today).first())
  }

  @Test
  fun `updateSteps persists the step count for the day`() = runTest {
    adapter.updateSteps(8500, today)

    val saved = adapter.getDailyActivity(today).first()
    assertEquals(8500, saved?.steps)
    assertEquals(0f, saved?.sleepHours)
    assertEquals(today, saved?.date)
  }

  @Test
  fun `updateSleep persists the sleep hours for the day`() = runTest {
    adapter.updateSleep(7.5f, today)

    val saved = adapter.getDailyActivity(today).first()
    assertEquals(7.5f, saved?.sleepHours)
    assertEquals(0, saved?.steps)
  }

  @Test
  fun `updateSteps then updateSleep keeps both values in a single entry`() = runTest {
    adapter.updateSteps(8500, today)
    adapter.updateSleep(7.5f, today)

    val saved = adapter.getDailyActivity(today).first()
    assertEquals(8500, saved?.steps)
    assertEquals(7.5f, saved?.sleepHours)
    assertEquals(1, dailyRepository.entries.size)
  }

  @Test
  fun `updates on another day do not leak into today`() = runTest {
    adapter.updateSteps(3000, today.plusDays(1))
    adapter.updateSteps(8500, today)

    assertEquals(8500, adapter.getDailyActivity(today).first()?.steps)
    assertEquals(3000, adapter.getDailyActivity(today.plusDays(1)).first()?.steps)
  }

  @Test
  fun `negative steps are coerced to zero`() = runTest {
    adapter.updateSteps(-50, today)

    assertEquals(0, adapter.getDailyActivity(today).first()?.steps)
  }

  @Test
  fun `sleep hours are coerced between zero and twenty four`() = runTest {
    adapter.updateSleep(30f, today)
    assertEquals(24f, adapter.getDailyActivity(today).first()?.sleepHours)

    adapter.updateSleep(-2f, today)
    assertEquals(0f, adapter.getDailyActivity(today).first()?.sleepHours)
  }

  @Test
  fun `advice is null when there is no cycle`() = runTest {
    assertNull(adapter.getActivityAdviceForToday(today).first())
  }

  @Test
  fun `advice matches the current phase for every phase`() = runTest {
    val cases = listOf(
      2 to PhaseType.MENSTRUAL,
      8 to PhaseType.FOLLICULAR,
      13 to PhaseType.OVULATORY,
      25 to PhaseType.LUTEAL
    )

    cases.forEach { (daysAgo, expectedPhase) ->
      cycleRepository.cycles.clear()
      cycleRepository.cycles.add(
        CycleEntry(startDate = today.minusDays(daysAgo.toLong()), endDate = null, cycleLengthDays = 28)
      )

      val advice = adapter.getActivityAdviceForToday(today).first()

      assertEquals(expectedPhase, advice?.phaseType)
      assertNotNull(advice?.title)
      assertTrue(advice?.title?.isNotBlank() == true)
      assertTrue(advice?.message?.isNotBlank() == true)
    }
  }

  @Test
  fun `every phase has a non prescriptive advice`() {
    PhaseType.entries.forEach { phase ->
      val advice = io.tvelu77.freya.domain.models.ActivityAdvice.forPhase(phase)
      assertEquals(phase, advice.phaseType)
      assertTrue(advice.title.isNotBlank())
      assertTrue(advice.message.isNotBlank())
    }
  }
}
