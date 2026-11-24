package io.tvelu77.freya.domain.models

data class EnergyTarget(
  val low: Int,
  val high: Int
) {

  val center: Int get() = (low + high) / 2

  companion object {
    const val RANGE_HALF_WIDTH = 100
    const val SAFETY_FLOOR = 1200

    fun compute(baseCalories: Int, goal: Goal, calorieSurplus: Int): EnergyTarget {
      val center = (baseCalories + goal.calorieDelta + calorieSurplus)
        .coerceAtLeast(SAFETY_FLOOR)
      val low = (center - RANGE_HALF_WIDTH).coerceAtLeast(SAFETY_FLOOR)
      return EnergyTarget(low, center + RANGE_HALF_WIDTH)
    }
  }
}
