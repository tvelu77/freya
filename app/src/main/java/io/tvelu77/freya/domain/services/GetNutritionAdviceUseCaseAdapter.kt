package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.NutritionAdvice
import io.tvelu77.freya.domain.models.PhaseInfo
import io.tvelu77.freya.domain.ports.api.GetNutritionAdviceUseCase
import io.tvelu77.freya.domain.ports.api.PhaseCalculator
import io.tvelu77.freya.domain.ports.spi.CycleRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class GetNutritionAdviceUseCaseAdapter @Inject constructor(
  private val repository: CycleRepository,
  private val calculator: PhaseCalculator
): GetNutritionAdviceUseCase {

  override fun getAdviceForToday(today: LocalDate): Flow<NutritionAdvice?> =
    repository.getLatestCycle().map { cycleEntry ->
      cycleEntry?.let {
        val phaseInfo = calculator.getCurrentPhase(it, today)
        NutritionAdvice.forPhase(phaseInfo.phase)
      }
    }

  override fun getCalorieTarget(
    baseCalories: Int,
    phaseInfo: PhaseInfo
  ): Int =
    (baseCalories + phaseInfo.nutritionAdvice.calorieSurplus).coerceAtLeast(1200)
}