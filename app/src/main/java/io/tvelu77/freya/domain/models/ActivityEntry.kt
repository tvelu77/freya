package io.tvelu77.freya.domain.models

import java.time.LocalDate

data class ActivityEntry(
  val id: Long = 0,
  val date: LocalDate,
  val sportType: SportType,
  val durationMin: Int,
  val kcalBurned: Int
) {

  companion object {
    const val DEFAULT_WEIGHT_KG = 60f
    private const val MINUTES_PER_HOUR = 60f

    fun computeKcal(sport: SportType, durationMin: Int, weightKg: Float?): Int {
      val weight = weightKg ?: DEFAULT_WEIGHT_KG
      return (sport.met * weight * durationMin / MINUTES_PER_HOUR).toInt()
    }
  }
}
