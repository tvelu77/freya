package io.tvelu77.freya.domain.ports.api

import io.tvelu77.freya.domain.models.HealthScore
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface GetHealthScoreUseCase {

  fun getScoreForDay(date: LocalDate = LocalDate.now()): Flow<HealthScore>

}
