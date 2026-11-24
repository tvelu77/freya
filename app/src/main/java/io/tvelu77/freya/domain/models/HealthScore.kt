package io.tvelu77.freya.domain.models

data class HealthScore(
  val value: Int,
  val nutritionScore: Int,
  val hydrationScore: Int,
  val activityScore: Int,
  val signalsScore: Int,
  val level: ScoreLevel
) {

  companion object {
    const val MAX_SCORE = 100
    const val NEUTRAL_SCORE = 50
    private const val W_NUTRITION = 0.4f
    private const val W_HYDRATION = 0.2f
    private const val W_ACTIVITY = 0.2f
    private const val W_SIGNALS = 0.2f

    fun compute(
      caloriesConsumed: Int?,
      caloriesTarget: Int,
      waterMl: Int?,
      waterTargetMl: Int,
      stepsCount: Int?,
      sportMinutes: Int,
      fatigue: FatigueLevel?
    ): HealthScore {
      val nutrition = nutritionScore(caloriesConsumed, caloriesTarget)
      val hydration = hydrationScore(waterMl, waterTargetMl)
      val activity = activityScore(stepsCount, sportMinutes)
      val signals = signalsScore(fatigue)
      val value = (
        nutrition * W_NUTRITION +
        hydration * W_HYDRATION +
        activity * W_ACTIVITY +
        signals * W_SIGNALS
      ).toInt().coerceIn(0, MAX_SCORE)
      val level = when {
        value >= 70 -> ScoreLevel.GOOD
        value >= 40 -> ScoreLevel.MEDIUM
        else -> ScoreLevel.LOW
      }
      return HealthScore(value, nutrition, hydration, activity, signals, level)
    }

    private fun nutritionScore(consumed: Int?, target: Int): Int {
      if (consumed == null || target <= 0) return NEUTRAL_SCORE
      val ratio = consumed.toFloat() / target
      return when {
        ratio < 0.6f -> 40
        ratio < 0.85f -> 70
        ratio <= 1.2f -> MAX_SCORE
        ratio <= 1.4f -> 70
        else -> NEUTRAL_SCORE
      }
    }

    private fun hydrationScore(waterMl: Int?, targetMl: Int): Int {
      if (waterMl == null || targetMl <= 0) return NEUTRAL_SCORE
      val ratio = waterMl.toFloat() / targetMl
      return when {
        ratio >= 1f -> MAX_SCORE
        ratio >= 0.7f -> 80
        ratio >= 0.4f -> 60
        else -> 40
      }
    }

    private fun activityScore(steps: Int?, sportMinutes: Int): Int {
      if (steps == null && sportMinutes == 0) return NEUTRAL_SCORE
      return ((steps ?: 0) / 100 + sportMinutes).coerceIn(0, MAX_SCORE)
    }

    private fun signalsScore(fatigue: FatigueLevel?): Int = when (fatigue) {
      null -> NEUTRAL_SCORE
      FatigueLevel.LOW -> MAX_SCORE
      FatigueLevel.MEDIUM -> 75
      FatigueLevel.HIGH -> NEUTRAL_SCORE
    }
  }

}
