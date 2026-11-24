package io.tvelu77.freya.domain.models

data class HydrationGoal(
  val baseMl: Int,
  val activityBonusMl: Int,
  val phaseBonusMl: Int
) {

  val targetMl: Int get() = baseMl + activityBonusMl + phaseBonusMl

  companion object {
    const val ML_PER_KG = 30
    const val ACTIVITY_BONUS_ML = 500
    const val ACTIVITY_THRESHOLD_KCAL = 200
    const val LUTEAL_PHASE_BONUS_ML = 250

    fun compute(weightKg: Float?, activityKcal: Int, phase: PhaseType?): HydrationGoal {
      val weight = weightKg ?: ActivityEntry.DEFAULT_WEIGHT_KG
      val base = (ML_PER_KG * weight).toInt()
      val activityBonus = if (activityKcal >= ACTIVITY_THRESHOLD_KCAL) ACTIVITY_BONUS_ML else 0
      val phaseBonus = if (phase == PhaseType.LUTEAL) LUTEAL_PHASE_BONUS_ML else 0
      return HydrationGoal(base, activityBonus, phaseBonus)
    }
  }
}
