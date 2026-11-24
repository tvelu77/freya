package io.tvelu77.freya.domain.ports.spi

import io.tvelu77.freya.domain.models.DailyActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate

class FakeDailyActivityRepository : DailyActivityRepository {

  val entries = mutableMapOf<LocalDate, DailyActivity>()

  override fun getDailyActivity(date: LocalDate): Flow<DailyActivity?> =
    flowOf(entries[date])

  override suspend fun saveDailyActivity(activity: DailyActivity) {
    entries[activity.date] = activity
  }
}
