package io.tvelu77.freya.domain.ports.api

import io.tvelu77.freya.domain.models.ActivityAdvice
import io.tvelu77.freya.domain.models.DailyActivity
import io.tvelu77.freya.domain.models.FatigueLevel
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TrackDailyActivityUseCase {

  fun getDailyActivity(date: LocalDate = LocalDate.now()): Flow<DailyActivity?>

  suspend fun updateSteps(steps: Int, date: LocalDate = LocalDate.now())

  suspend fun updateSleep(sleepHours: Float, date: LocalDate = LocalDate.now())

  suspend fun updateFatigue(fatigue: FatigueLevel?, date: LocalDate = LocalDate.now())

  fun getActivityAdviceForToday(today: LocalDate = LocalDate.now()): Flow<ActivityAdvice?>

  companion object {
    const val MAX_SLEEP_HOURS = 24f
  }
}
