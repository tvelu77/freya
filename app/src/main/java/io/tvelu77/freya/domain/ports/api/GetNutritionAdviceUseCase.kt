package io.tvelu77.freya.domain.ports.api

import io.tvelu77.freya.domain.models.NutritionAdvice
import io.tvelu77.freya.domain.models.PhaseInfo
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface GetNutritionAdviceUseCase {

  fun getAdviceForToday(today: LocalDate = LocalDate.now()): Flow<NutritionAdvice?>

  fun getCalorieTarget(baseCalories: Int, phaseInfo: PhaseInfo): Int

}