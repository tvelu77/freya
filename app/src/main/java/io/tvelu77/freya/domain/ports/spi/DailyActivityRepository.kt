package io.tvelu77.freya.domain.ports.spi

import io.tvelu77.freya.domain.models.DailyActivity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface DailyActivityRepository {
  fun getDailyActivity(date: LocalDate): Flow<DailyActivity?>
  suspend fun saveDailyActivity(activity: DailyActivity)
}
