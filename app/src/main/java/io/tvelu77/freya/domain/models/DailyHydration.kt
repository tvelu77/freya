package io.tvelu77.freya.domain.models

import java.time.LocalDate

data class DailyHydration(
  val date: LocalDate,
  val waterMl: Int
)
