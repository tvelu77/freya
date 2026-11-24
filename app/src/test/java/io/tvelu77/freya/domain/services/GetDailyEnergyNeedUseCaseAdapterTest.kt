package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.ActivityEntry
import io.tvelu77.freya.domain.models.CycleEntry
import io.tvelu77.freya.domain.models.DailyActivity
import io.tvelu77.freya.domain.models.FatigueLevel
import io.tvelu77.freya.domain.models.SportType
import io.tvelu77.freya.domain.ports.spi.FakeActivityRepository
import io.tvelu77.freya.domain.ports.spi.FakeCycleRepository
import io.tvelu77.freya.domain.ports.spi.FakeDailyActivityRepository
import io.tvelu77.freya.domain.ports.spi.FakeUserProfileRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class GetDailyEnergyNeedUseCaseAdapterTest {

  private val profileRepository = FakeUserProfileRepository()
  private val activityRepository = FakeActivityRepository()
  private val dailyActivityRepository = FakeDailyActivityRepository()
  private val cycleRepository = FakeCycleRepository()
  private val adapter = GetDailyEnergyNeedUseCaseAdapter(
    profileRepository,
    activityRepository,
    dailyActivityRepository,
    cycleRepository,
    PhaseCalculatorAdapter()
  )

  private val today = LocalDate.of(2024, 6, 10)

  @Test
  fun `need uses neutral defaults when the profile is incomplete and nothing was logged`() = runTest {
    val need = adapter.getNeedForDay(today).first()

    assertEquals(1320, need.bmrKcal)
    assertEquals(1584, need.totalKcal)
  }

  @Test
  fun `need uses the profile age height and weight when present`() = runTest {
    profileRepository.profile = profileRepository.profile.copy(
      weightKg = 70f, heightCm = 175, ageYears = 25
    )

    val need = adapter.getNeedForDay(today).first()

    assertEquals(1507, need.bmrKcal)
    assertEquals((1507 * 1.2f).toInt(), need.baseKcal)
  }

  @Test
  fun `sport activity of the day raises the need`() = runTest {
    activityRepository.saveActivity(
      ActivityEntry(date = today, sportType = SportType.RUNNING, durationMin = 30, kcalBurned = 294)
    )

    val need = adapter.getNeedForDay(today).first()

    assertEquals(294, need.activityAdjustmentKcal)
    assertEquals(1584 + 294, need.totalKcal)
  }

  @Test
  fun `steps raise the need through the domain estimate`() = runTest {
    dailyActivityRepository.saveDailyActivity(
      DailyActivity(date = today, steps = 10000, sleepHours = 8f)
    )

    val need = adapter.getNeedForDay(today).first()

    assertEquals(320, need.activityAdjustmentKcal)
  }

  @Test
  fun `short sleep raises the need`() = runTest {
    dailyActivityRepository.saveDailyActivity(
      DailyActivity(date = today, steps = 0, sleepHours = 5f)
    )

    assertEquals(100, adapter.getNeedForDay(today).first().sleepAdjustmentKcal)
  }

  @Test
  fun `luteal phase raises the need by the phase surplus`() = runTest {
    cycleRepository.cycles.add(
      CycleEntry(startDate = today.minusDays(25), endDate = null, cycleLengthDays = 28)
    )

    assertEquals(150, adapter.getNeedForDay(today).first().phaseAdjustmentKcal)
  }

  @Test
  fun `self reported fatigue raises the need`() = runTest {
    dailyActivityRepository.saveDailyActivity(
      DailyActivity(date = today, steps = 0, sleepHours = 8f, fatigue = FatigueLevel.HIGH)
    )

    val need = adapter.getNeedForDay(today).first()

    assertEquals(200, need.fatigueAdjustmentKcal)
    assertEquals(1584 + 200, need.totalKcal)
  }

  @Test
  fun `adjustments stack on the same day`() = runTest {
    profileRepository.profile = profileRepository.profile.copy(weightKg = 60f)
    activityRepository.saveActivity(
      ActivityEntry(date = today, sportType = SportType.RUNNING, durationMin = 30, kcalBurned = 294)
    )
    dailyActivityRepository.saveDailyActivity(
      DailyActivity(date = today, steps = 0, sleepHours = 5f, fatigue = FatigueLevel.MEDIUM)
    )
    cycleRepository.cycles.add(
      CycleEntry(startDate = today.minusDays(25), endDate = null, cycleLengthDays = 28)
    )

    val need = adapter.getNeedForDay(today).first()

    assertEquals(1584 + 294 + 100 + 150 + 100, need.totalKcal)
  }
}
