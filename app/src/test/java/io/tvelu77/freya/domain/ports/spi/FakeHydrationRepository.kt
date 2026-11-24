package io.tvelu77.freya.domain.ports.spi

import io.tvelu77.freya.domain.models.DailyHydration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate

class FakeHydrationRepository : HydrationRepository {

  val entries = mutableMapOf<LocalDate, DailyHydration>()

  override fun getHydration(date: LocalDate): Flow<DailyHydration?> =
    flowOf(entries[date])

  override suspend fun saveHydration(hydration: DailyHydration) {
    entries[hydration.date] = hydration
  }
}
