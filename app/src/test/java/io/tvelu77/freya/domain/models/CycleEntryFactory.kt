package io.tvelu77.freya.domain.models

import java.time.LocalDate

object CycleEntryFactory {

  fun cycleEntry(
    startDate: LocalDate,
    endDate: LocalDate? = null,
    id: Long = 0,
    cycleLengthDays: Int = 28
  ): CycleEntry = CycleEntry(
    id = id,
    startDate = startDate,
    endDate = endDate,
    cycleLengthDays = cycleLengthDays
  )
}
