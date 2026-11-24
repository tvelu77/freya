package io.tvelu77.freya.domain.ports.spi

import io.tvelu77.freya.domain.models.DailyHydration
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface HydrationRepository {
  fun getHydration(date: LocalDate): Flow<DailyHydration?>
  suspend fun saveHydration(hydration: DailyHydration)
}
