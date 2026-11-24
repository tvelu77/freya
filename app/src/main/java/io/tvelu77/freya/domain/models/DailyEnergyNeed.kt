package io.tvelu77.freya.domain.models

data class DailyEnergyNeed(
  val bmrKcal: Int,
  val baseKcal: Int,
  val activityAdjustmentKcal: Int,
  val sleepAdjustmentKcal: Int,
  val phaseAdjustmentKcal: Int,
  val fatigueAdjustmentKcal: Int,
  val totalKcal: Int
) {

  companion object {
    const val SEDENTARY_FACTOR = 1.2f
    const val DEFAULT_HEIGHT_CM = 165
    const val DEFAULT_AGE_YEARS = 30
    const val DEFAULT_SLEEP_HOURS = 8f
    const val SHORT_SLEEP_HOURS = 6f
    const val SHORT_SLEEP_BONUS_KCAL = 100

    fun mifflinStJeor(weightKg: Float, heightCm: Int, ageYears: Int): Int =
      (10f * weightKg + 6.25f * heightCm - 5f * ageYears - 161f).toInt()

    fun compute(
      weightKg: Float?,
      heightCm: Int?,
      ageYears: Int?,
      activityKcal: Int,
      sleepHours: Float,
      phase: PhaseType?,
      fatigue: FatigueLevel?
    ): DailyEnergyNeed {
      val bmr = mifflinStJeor(
        weightKg ?: ActivityEntry.DEFAULT_WEIGHT_KG,
        heightCm ?: DEFAULT_HEIGHT_CM,
        ageYears ?: DEFAULT_AGE_YEARS
      )
      val base = (bmr * SEDENTARY_FACTOR).toInt()
      val activity = activityKcal.coerceAtLeast(0)
      val sleep = if (sleepHours < SHORT_SLEEP_HOURS) SHORT_SLEEP_BONUS_KCAL else 0
      val phaseAdjustment = phase?.let { NutritionAdvice.forPhase(it).calorieSurplus } ?: 0
      val fatigueAdjustment = fatigue?.kcalAdjustment ?: 0
      val total = (base + activity + sleep + phaseAdjustment + fatigueAdjustment)
        .coerceAtLeast(EnergyTarget.SAFETY_FLOOR)
      return DailyEnergyNeed(
        bmrKcal = bmr,
        baseKcal = base,
        activityAdjustmentKcal = activity,
        sleepAdjustmentKcal = sleep,
        phaseAdjustmentKcal = phaseAdjustment,
        fatigueAdjustmentKcal = fatigueAdjustment,
        totalKcal = total
      )
    }
  }
}
