package io.tvelu77.freya.domain.models

import java.time.LocalDate

data class DailyActivity(
  val date: LocalDate,
  val steps: Int,
  val sleepHours: Float,
  val fatigue: FatigueLevel? = null
) {

  fun estimateStepsKcal(weightKg: Float?): Int = estimateStepsKcal(steps, weightKg)

  companion object {
    private const val METERS_PER_STEP = 0.762f
    private const val WALKING_SPEED_KMH = 5f
    private const val METERS_PER_KM = 1000f

    fun estimateStepsKcal(steps: Int, weightKg: Float?): Int {
      val weight = weightKg ?: ActivityEntry.DEFAULT_WEIGHT_KG
      val hours = steps * METERS_PER_STEP / METERS_PER_KM / WALKING_SPEED_KMH
      return (SportType.WALKING.met * weight * hours).toInt()
    }
  }
}
