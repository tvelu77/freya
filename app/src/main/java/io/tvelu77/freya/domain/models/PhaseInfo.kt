package io.tvelu77.freya.domain.models

import java.time.LocalDate

data class PhaseInfo(
  val phase: PhaseType,
  val startDate: LocalDate,
  val endDate: LocalDate,
  val daysInCycle: Int,
  val nutritionAdvice: NutritionAdvice
)
