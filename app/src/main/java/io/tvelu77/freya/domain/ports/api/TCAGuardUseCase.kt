package io.tvelu77.freya.domain.ports.api

import io.tvelu77.freya.domain.models.TCAAlert
import java.time.LocalDate

interface TCAGuardUseCase {

  suspend fun checkLastDays(days: Int = 3, today: LocalDate = LocalDate.now()): TCAAlert

}