package io.tvelu77.freya.domain.ports.api

import io.tvelu77.freya.domain.models.DailyHydration
import io.tvelu77.freya.domain.models.HydrationGoal
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TrackHydrationUseCase {

  fun getHydrationForDay(date: LocalDate = LocalDate.now()): Flow<DailyHydration?>

  suspend fun addWater(ml: Int, date: LocalDate = LocalDate.now())

  fun getGoalForDay(date: LocalDate = LocalDate.now()): Flow<HydrationGoal>

  suspend fun rescheduleHydrationReminders()

}
