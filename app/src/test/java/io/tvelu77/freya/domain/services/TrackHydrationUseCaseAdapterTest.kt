package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.ActivityEntry
import io.tvelu77.freya.domain.models.CycleEntry
import io.tvelu77.freya.domain.models.DailyActivity
import io.tvelu77.freya.domain.models.HydrationGoal
import io.tvelu77.freya.domain.models.SportType
import io.tvelu77.freya.domain.ports.spi.FakeActivityRepository
import io.tvelu77.freya.domain.ports.spi.FakeCycleRepository
import io.tvelu77.freya.domain.ports.spi.FakeDailyActivityRepository
import io.tvelu77.freya.domain.ports.spi.FakeHydrationRepository
import io.tvelu77.freya.domain.ports.spi.FakeNotificationScheduler
import io.tvelu77.freya.domain.ports.spi.FakeUserProfileRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class TrackHydrationUseCaseAdapterTest {

  private val hydrationRepository = FakeHydrationRepository()
  private val profileRepository = FakeUserProfileRepository()
  private val activityRepository = FakeActivityRepository()
  private val dailyActivityRepository = FakeDailyActivityRepository()
  private val cycleRepository = FakeCycleRepository()
  private val scheduler = FakeNotificationScheduler()
  private val adapter = TrackHydrationUseCaseAdapter(
    hydrationRepository,
    profileRepository,
    activityRepository,
    dailyActivityRepository,
    cycleRepository,
    PhaseCalculatorAdapter(),
    HydrationReminderPlannerAdapter(),
    scheduler
  )

  private val today = LocalDate.of(2024, 6, 10)

  @Test
  fun `getHydrationForDay returns null when nothing was logged`() = runTest {
    assertNull(adapter.getHydrationForDay(today).first())
  }

  @Test
  fun `addWater persists and accumulates the intake for the day`() = runTest {
    adapter.addWater(250, today)
    adapter.addWater(500, today)

    val saved = adapter.getHydrationForDay(today).first()
    assertEquals(750, saved?.waterMl)
    assertEquals(today, saved?.date)
    assertEquals(1, hydrationRepository.entries.size)
  }

  @Test
  fun `addWater on another day does not leak into today`() = runTest {
    adapter.addWater(500, today.plusDays(1))
    adapter.addWater(250, today)

    assertEquals(250, adapter.getHydrationForDay(today).first()?.waterMl)
    assertEquals(500, adapter.getHydrationForDay(today.plusDays(1)).first()?.waterMl)
  }

  @Test
  fun `negative amounts are coerced to zero`() = runTest {
    adapter.addWater(-300, today)

    assertEquals(0, adapter.getHydrationForDay(today).first()?.waterMl)
  }

  @Test
  fun `goal is the base 30ml per kg when there is no activity and no cycle`() = runTest {
    profileRepository.profile = profileRepository.profile.copy(weightKg = 60f)

    val goal = adapter.getGoalForDay(today).first()

    assertEquals(1800, goal.targetMl)
    assertEquals(0, goal.activityBonusMl)
    assertEquals(0, goal.phaseBonusMl)
  }

  @Test
  fun `goal adds the activity bonus on sport activity days`() = runTest {
    profileRepository.profile = profileRepository.profile.copy(weightKg = 60f)
    activityRepository.saveActivity(
      ActivityEntry(date = today, sportType = SportType.RUNNING, durationMin = 30, kcalBurned = 294)
    )

    val goal = adapter.getGoalForDay(today).first()

    assertEquals(HydrationGoal.ACTIVITY_BONUS_ML, goal.activityBonusMl)
    assertEquals(1800 + HydrationGoal.ACTIVITY_BONUS_ML, goal.targetMl)
  }

  @Test
  fun `goal counts steps as activity through the domain estimate`() = runTest {
    profileRepository.profile = profileRepository.profile.copy(weightKg = 60f)
    dailyActivityRepository.saveDailyActivity(
      DailyActivity(date = today, steps = 10000, sleepHours = 8f)
    )

    val goal = adapter.getGoalForDay(today).first()

    assertEquals(HydrationGoal.ACTIVITY_BONUS_ML, goal.activityBonusMl)
  }

  @Test
  fun `goal adds the phase bonus in the luteal phase`() = runTest {
    profileRepository.profile = profileRepository.profile.copy(weightKg = 60f)
    cycleRepository.cycles.add(
      CycleEntry(startDate = today.minusDays(25), endDate = null, cycleLengthDays = 28)
    )

    val goal = adapter.getGoalForDay(today).first()

    assertEquals(HydrationGoal.LUTEAL_PHASE_BONUS_ML, goal.phaseBonusMl)
    assertEquals(1800 + HydrationGoal.LUTEAL_PHASE_BONUS_ML, goal.targetMl)
  }

  @Test
  fun `reminders are cancelled when notifications are disabled`() = runTest {
    profileRepository.profile = profileRepository.profile.copy(notificationsEnabled = false)

    adapter.rescheduleHydrationReminders()

    assertEquals(1, scheduler.cancelHydrationCount)
    assertTrue(scheduler.hydrationScheduled.isEmpty())
  }

  @Test
  fun `reminders are scheduled when notifications are enabled`() = runTest {
    profileRepository.profile = profileRepository.profile.copy(notificationsEnabled = true)

    adapter.rescheduleHydrationReminders()

    assertEquals(1, scheduler.hydrationScheduled.size)
    assertTrue(scheduler.hydrationScheduled.single().size <= 2)
    assertEquals(0, scheduler.cancelHydrationCount)
  }
}
