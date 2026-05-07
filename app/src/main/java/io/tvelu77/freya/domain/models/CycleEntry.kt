package io.tvelu77.freya.domain.models

import java.time.LocalDate

data class CycleEntry(
  val id: Long = 0,
  val startDate: LocalDate,
  val endDate: LocalDate?,
  val cycleLengthDays: Int = 28,
  val notes: String = ""
) {
  val isOngoing: Boolean get() = endDate == null
}
