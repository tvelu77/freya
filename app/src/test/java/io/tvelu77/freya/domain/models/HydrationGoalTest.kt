package io.tvelu77.freya.domain.models

import org.junit.Assert.assertEquals
import org.junit.Test

class HydrationGoalTest {

  @Test
  fun `base goal is 30 ml per kg of body weight`() {
    val cases = listOf(
      60f to 1800,
      70f to 2100,
      50f to 1500
    )

    cases.forEach { (weight, expectedBase) ->
      val goal = HydrationGoal.compute(weight, activityKcal = 0, phase = null)
      assertEquals("$weight kg", expectedBase, goal.baseMl)
    }
  }

  @Test
  fun `weight falls back to the neutral default when absent`() {
    val goal = HydrationGoal.compute(null, activityKcal = 0, phase = null)

    assertEquals((HydrationGoal.ML_PER_KG * ActivityEntry.DEFAULT_WEIGHT_KG).toInt(), goal.baseMl)
  }

  @Test
  fun `activity bonus applies only from the kcal threshold`() {
    val below = HydrationGoal.compute(60f, activityKcal = HydrationGoal.ACTIVITY_THRESHOLD_KCAL - 1, phase = null)
    val atThreshold = HydrationGoal.compute(60f, activityKcal = HydrationGoal.ACTIVITY_THRESHOLD_KCAL, phase = null)

    assertEquals(0, below.activityBonusMl)
    assertEquals(HydrationGoal.ACTIVITY_BONUS_ML, atThreshold.activityBonusMl)
  }

  @Test
  fun `phase bonus applies in the luteal phase only`() {
    val luteal = HydrationGoal.compute(60f, activityKcal = 0, phase = PhaseType.LUTEAL)
    val ovulatory = HydrationGoal.compute(60f, activityKcal = 0, phase = PhaseType.OVULATORY)
    val none = HydrationGoal.compute(60f, activityKcal = 0, phase = null)

    assertEquals(HydrationGoal.LUTEAL_PHASE_BONUS_ML, luteal.phaseBonusMl)
    assertEquals(0, ovulatory.phaseBonusMl)
    assertEquals(0, none.phaseBonusMl)
  }

  @Test
  fun `target is the sum of base and bonuses`() {
    val cases = listOf(
      Triple(60f, 0, null) to 1800,
      Triple(60f, 300, null) to 1800 + HydrationGoal.ACTIVITY_BONUS_ML,
      Triple(60f, 0, PhaseType.LUTEAL) to 1800 + HydrationGoal.LUTEAL_PHASE_BONUS_ML,
      Triple(50f, 250, PhaseType.LUTEAL) to
        1500 + HydrationGoal.ACTIVITY_BONUS_ML + HydrationGoal.LUTEAL_PHASE_BONUS_ML
    )

    cases.forEach { (input, expectedTarget) ->
      val (weight, kcal, phase) = input
      val goal = HydrationGoal.compute(weight, kcal, phase)
      assertEquals(expectedTarget, goal.targetMl)
    }
  }
}
