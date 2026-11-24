package io.tvelu77.freya.domain.ports.api

import io.tvelu77.freya.domain.models.DailyNutrition
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface GetDailyNutritionUseCase {

  fun getForDay(date: LocalDate = LocalDate.now()): Flow<DailyNutrition>
}
