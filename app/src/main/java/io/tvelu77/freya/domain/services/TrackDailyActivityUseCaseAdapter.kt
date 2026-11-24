package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.ActivityAdvice
import io.tvelu77.freya.domain.models.DailyActivity
import io.tvelu77.freya.domain.ports.api.PhaseCalculator
import io.tvelu77.freya.domain.ports.api.TrackDailyActivityUseCase
import io.tvelu77.freya.domain.ports.spi.CycleRepository
import io.tvelu77.freya.domain.ports.spi.DailyActivityRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class TrackDailyActivityUseCaseAdapter @Inject constructor(
  private val dailyActivityRepository: DailyActivityRepository,
  private val cycleRepository: CycleRepository,
  private val phaseCalculator: PhaseCalculator
) : TrackDailyActivityUseCase {

  override fun getDailyActivity(date: LocalDate): Flow<DailyActivity?> =
    dailyActivityRepository.getDailyActivity(date)

  override suspend fun updateSteps(steps: Int, date: LocalDate) {
    val current = dailyActivityRepository.getDailyActivity(date).first()
    dailyActivityRepository.saveDailyActivity(
      (current ?: DailyActivity(date = date, steps = 0, sleepHours = 0f))
        .copy(steps = steps.coerceAtLeast(0))
    )
  }

  override suspend fun updateSleep(sleepHours: Float, date: LocalDate) {
    val current = dailyActivityRepository.getDailyActivity(date).first()
    dailyActivityRepository.saveDailyActivity(
      (current ?: DailyActivity(date = date, steps = 0, sleepHours = 0f))
        .copy(sleepHours = sleepHours.coerceIn(0f, TrackDailyActivityUseCase.MAX_SLEEP_HOURS))
    )
  }

  override fun getActivityAdviceForToday(today: LocalDate): Flow<ActivityAdvice?> =
    cycleRepository.getLatestCycle().map { cycle ->
      cycle?.let {
        ActivityAdvice.forPhase(phaseCalculator.getCurrentPhase(it, today).phase)
      }
    }
}
