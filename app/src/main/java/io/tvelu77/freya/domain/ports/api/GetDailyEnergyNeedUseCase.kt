package io.tvelu77.freya.domain.ports.api

import io.tvelu77.freya.domain.models.DailyEnergyNeed
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface GetDailyEnergyNeedUseCase {

  fun getNeedForDay(date: LocalDate = LocalDate.now()): Flow<DailyEnergyNeed>

}
