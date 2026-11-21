package io.tvelu77.freya.domain.ports.api

import io.tvelu77.freya.domain.models.CycleStats
import kotlinx.coroutines.flow.Flow

interface GetCycleStatsUseCase {

  fun execute(take: Int = 6): Flow<CycleStats?>

}
