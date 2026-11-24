package io.tvelu77.freya.presentation.activity

import io.tvelu77.freya.domain.models.ActivityAdvice
import io.tvelu77.freya.domain.models.ActivityEntry
import io.tvelu77.freya.domain.models.DailyActivity
import io.tvelu77.freya.domain.models.DailyHydration
import io.tvelu77.freya.domain.models.HydrationGoal

data class ActivityUiState(
  val isLoading: Boolean = true,
  val activities: List<ActivityEntry> = emptyList(),
  val totalBurned: Int = 0,
  val dailyActivity: DailyActivity? = null,
  val activityAdvice: ActivityAdvice? = null,
  val hydration: DailyHydration? = null,
  val hydrationGoal: HydrationGoal = HydrationGoal.compute(null, 0, null),
  val showAddDialog: Boolean = false,
  val showCalories: Boolean = true,
  val error: String? = null
)
