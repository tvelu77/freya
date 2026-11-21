package io.tvelu77.freya.domain.models

data class CycleStats(
  val averageCycleLengthDays: Int,
  val averagePeriodLengthDays: Int,
  val variabilityDays: Double,
  val cyclesCounted: Int
)
