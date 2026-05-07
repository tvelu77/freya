package io.tvelu77.freya.domain.models

private const val MAX_SCORE = 100
private const val MID_SCORE = 50

data class HealthScore(
  val value: Int,
  val nutritionScore: Int,
  val hydrationScore: Int,
  val activityScore: Int,
  val message: String,
  val level: ScoreLevel
) {

  companion object {
    fun compute(
      caloriesConsumed: Int,
      caloriesTarget: Int,
      waterMl: Int,
      waterTargetMl: Int,
      stepsCount: Int
    ): HealthScore {
      val nutritionScore = computeNutritionScore(caloriesConsumed, caloriesTarget)
      val hydrationScore = ((waterMl.toFloat() / waterTargetMl) * 100).toInt().coerceIn(0, 100)
      val activityScore  = (stepsCount / 100).coerceIn(0, 100)
      val globalScore = ((nutritionScore * 0.5) + (hydrationScore * 0.3) + (activityScore * 0.2)).toInt()
      val (level, message) = when {
        globalScore >= 70 -> ScoreLevel.GOOD to "Tu as bien nourri ton corps aujourd'hui"
        globalScore >= 40 -> ScoreLevel.MEDIUM to "Tu peux essayer de boire un peu plus demain"
        else -> ScoreLevel.LOW to "Ton corps a peut-être besoin de plus d'énergie"
      }
      return HealthScore(globalScore, nutritionScore, hydrationScore, activityScore, message, level)
    }

    private fun computeNutritionScore(consumed: Int, target: Int): Int {
      if (target == 0) {
        return 0;
      }
      val ratio = consumed.toFloat() / target;
      return when {
        ratio < 0.6f -> (ratio * 80).toInt()
        ratio <= 1.1f -> MAX_SCORE
        ratio <= 1.3f -> (MAX_SCORE - (ratio - 1.1f) * MAX_SCORE).toInt()
        else -> MID_SCORE
      }.coerceIn(0, MAX_SCORE)
    }
  }

}
