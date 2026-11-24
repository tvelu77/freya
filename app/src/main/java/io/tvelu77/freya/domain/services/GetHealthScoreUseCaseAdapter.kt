package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.EnergyTarget
import io.tvelu77.freya.domain.models.HealthScore
import io.tvelu77.freya.domain.models.HydrationGoal
import io.tvelu77.freya.domain.ports.api.GetHealthScoreUseCase
import io.tvelu77.freya.domain.ports.spi.ActivityRepository
import io.tvelu77.freya.domain.ports.spi.DailyActivityRepository
import io.tvelu77.freya.domain.ports.spi.FoodRepository
import io.tvelu77.freya.domain.ports.spi.HydrationRepository
import io.tvelu77.freya.domain.ports.spi.UserProfileRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate

class GetHealthScoreUseCaseAdapter @Inject constructor(
  private val foodRepository: FoodRepository,
  private val hydrationRepository: HydrationRepository,
  private val dailyActivityRepository: DailyActivityRepository,
  private val activityRepository: ActivityRepository,
  private val profileRepository: UserProfileRepository
) : GetHealthScoreUseCase {

  override fun getScoreForDay(date: LocalDate): Flow<HealthScore> = combine(
    foodRepository.getFoodEntriesForDate(date),
    hydrationRepository.getHydration(date),
    dailyActivityRepository.getDailyActivity(date),
    activityRepository.getActivitiesForDate(date),
    profileRepository.getProfile()
  ) { foods, hydration, daily, activities, profile ->
    val activityKcal = activities.sumOf { it.kcalBurned } +
      (daily?.estimateStepsKcal(profile.weightKg) ?: 0)
    HealthScore.compute(
      caloriesConsumed = foods.takeIf { it.isNotEmpty() }?.sumOf { it.calories },
      caloriesTarget = EnergyTarget.compute(profile.baseCalories, profile.goal, 0).center,
      waterMl = hydration?.waterMl,
      waterTargetMl = HydrationGoal.compute(profile.weightKg, activityKcal, null).targetMl,
      stepsCount = daily?.steps,
      sportMinutes = activities.sumOf { it.durationMin },
      fatigue = daily?.fatigue
    )
  }
}
