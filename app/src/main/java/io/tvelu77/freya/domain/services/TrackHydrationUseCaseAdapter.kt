package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.DailyHydration
import io.tvelu77.freya.domain.models.HydrationGoal
import io.tvelu77.freya.domain.ports.api.HydrationReminderPlanner
import io.tvelu77.freya.domain.ports.api.PhaseCalculator
import io.tvelu77.freya.domain.ports.api.TrackHydrationUseCase
import io.tvelu77.freya.domain.ports.spi.ActivityRepository
import io.tvelu77.freya.domain.ports.spi.CycleRepository
import io.tvelu77.freya.domain.ports.spi.DailyActivityRepository
import io.tvelu77.freya.domain.ports.spi.HydrationRepository
import io.tvelu77.freya.domain.ports.spi.NotificationScheduler
import io.tvelu77.freya.domain.ports.spi.UserProfileRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class TrackHydrationUseCaseAdapter @Inject constructor(
  private val hydrationRepository: HydrationRepository,
  private val profileRepository: UserProfileRepository,
  private val activityRepository: ActivityRepository,
  private val dailyActivityRepository: DailyActivityRepository,
  private val cycleRepository: CycleRepository,
  private val phaseCalculator: PhaseCalculator,
  private val reminderPlanner: HydrationReminderPlanner,
  private val notificationScheduler: NotificationScheduler
) : TrackHydrationUseCase {

  override fun getHydrationForDay(date: LocalDate): Flow<DailyHydration?> =
    hydrationRepository.getHydration(date)

  override suspend fun addWater(ml: Int, date: LocalDate) {
    val current = hydrationRepository.getHydration(date).first()
    hydrationRepository.saveHydration(
      DailyHydration(
        date = date,
        waterMl = (current?.waterMl ?: 0) + ml.coerceAtLeast(0)
      )
    )
  }

  override fun getGoalForDay(date: LocalDate): Flow<HydrationGoal> = combine(
    profileRepository.getProfile(),
    activityRepository.getActivitiesForDate(date),
    dailyActivityRepository.getDailyActivity(date),
    cycleRepository.getLatestCycle()
  ) { profile, activities, daily, cycle ->
    val phase = cycle?.let { phaseCalculator.getCurrentPhase(it, date).phase }
    val activityKcal = activities.sumOf { it.kcalBurned } +
      (daily?.estimateStepsKcal(profile.weightKg) ?: 0)
    HydrationGoal.compute(profile.weightKg, activityKcal, phase)
  }

  override suspend fun rescheduleHydrationReminders() {
    val enabled = profileRepository.getProfile().first().notificationsEnabled
    if (!enabled) {
      notificationScheduler.cancelHydrationReminders()
      return
    }
    notificationScheduler.scheduleHydrationReminders(reminderPlanner.plan())
  }
}
