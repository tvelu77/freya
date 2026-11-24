package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.DailyEnergyNeed
import io.tvelu77.freya.domain.ports.api.GetDailyEnergyNeedUseCase
import io.tvelu77.freya.domain.ports.api.PhaseCalculator
import io.tvelu77.freya.domain.ports.spi.ActivityRepository
import io.tvelu77.freya.domain.ports.spi.CycleRepository
import io.tvelu77.freya.domain.ports.spi.DailyActivityRepository
import io.tvelu77.freya.domain.ports.spi.UserProfileRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate

class GetDailyEnergyNeedUseCaseAdapter @Inject constructor(
  private val profileRepository: UserProfileRepository,
  private val activityRepository: ActivityRepository,
  private val dailyActivityRepository: DailyActivityRepository,
  private val cycleRepository: CycleRepository,
  private val phaseCalculator: PhaseCalculator
) : GetDailyEnergyNeedUseCase {

  override fun getNeedForDay(date: LocalDate): Flow<DailyEnergyNeed> = combine(
    profileRepository.getProfile(),
    activityRepository.getActivitiesForDate(date),
    dailyActivityRepository.getDailyActivity(date),
    cycleRepository.getLatestCycle()
  ) { profile, activities, daily, cycle ->
    val phase = cycle?.let { phaseCalculator.getCurrentPhase(it, date).phase }
    DailyEnergyNeed.compute(
      weightKg = profile.weightKg,
      heightCm = profile.heightCm,
      ageYears = profile.ageYears,
      activityKcal = activities.sumOf { it.kcalBurned } +
        (daily?.estimateStepsKcal(profile.weightKg) ?: 0),
      sleepHours = daily?.sleepHours ?: DailyEnergyNeed.DEFAULT_SLEEP_HOURS,
      phase = phase,
      fatigue = daily?.fatigue
    )
  }
}
