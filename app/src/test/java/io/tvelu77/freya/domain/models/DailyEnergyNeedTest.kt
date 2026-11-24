package io.tvelu77.freya.domain.models

import org.junit.Assert.assertEquals
import org.junit.Test

class DailyEnergyNeedTest {

  @Test
  fun `mifflin st jeor for women is 10w + 6_25h - 5a - 161`() {
    val cases = listOf(
      Triple(60f, 165, 30) to 1320,
      Triple(70f, 175, 25) to 1507,
      Triple(50f, 160, 40) to 1139
    )

    cases.forEach { (input, expected) ->
      val (weight, height, age) = input
      assertEquals(
        "$weight kg, $height cm, $age y",
        expected,
        DailyEnergyNeed.mifflinStJeor(weight, height, age)
      )
    }
  }

  @Test
  fun `missing profile fields fall back to neutral defaults`() {
    val need = DailyEnergyNeed.compute(
      weightKg = null, heightCm = null, ageYears = null,
      activityKcal = 0, sleepHours = 8f, phase = null, fatigue = null
    )

    assertEquals(1320, need.bmrKcal)
    assertEquals((1320 * DailyEnergyNeed.SEDENTARY_FACTOR).toInt(), need.baseKcal)
    assertEquals(need.baseKcal, need.totalKcal)
  }

  @Test
  fun `activity kcal are added on top of the sedentary base`() {
    val need = DailyEnergyNeed.compute(
      weightKg = 60f, heightCm = 165, ageYears = 30,
      activityKcal = 300, sleepHours = 8f, phase = null, fatigue = null
    )

    assertEquals(300, need.activityAdjustmentKcal)
    assertEquals(1584 + 300, need.totalKcal)
  }

  @Test
  fun `short sleep raises the estimate a little`() {
    val rested = DailyEnergyNeed.compute(60f, 165, 30, 0, 8f, null, null)
    val shortSleep = DailyEnergyNeed.compute(60f, 165, 30, 0, 5f, null, null)

    assertEquals(0, rested.sleepAdjustmentKcal)
    assertEquals(DailyEnergyNeed.SHORT_SLEEP_BONUS_KCAL, shortSleep.sleepAdjustmentKcal)
    assertEquals(
      rested.totalKcal + DailyEnergyNeed.SHORT_SLEEP_BONUS_KCAL,
      shortSleep.totalKcal
    )
  }

  @Test
  fun `phase adjustment reuses the nutrition advice surplus`() {
    val luteal = DailyEnergyNeed.compute(60f, 165, 30, 0, 8f, PhaseType.LUTEAL, null)
    val follicular = DailyEnergyNeed.compute(60f, 165, 30, 0, 8f, PhaseType.FOLLICULAR, null)
    val none = DailyEnergyNeed.compute(60f, 165, 30, 0, 8f, null, null)

    assertEquals(150, luteal.phaseAdjustmentKcal)
    assertEquals(-50, follicular.phaseAdjustmentKcal)
    assertEquals(0, none.phaseAdjustmentKcal)
  }

  @Test
  fun `fatigue raises the estimate, higher fatigue more`() {
    val base = DailyEnergyNeed.compute(60f, 165, 30, 0, 8f, null, null)
    val low = DailyEnergyNeed.compute(60f, 165, 30, 0, 8f, null, FatigueLevel.LOW)
    val medium = DailyEnergyNeed.compute(60f, 165, 30, 0, 8f, null, FatigueLevel.MEDIUM)
    val high = DailyEnergyNeed.compute(60f, 165, 30, 0, 8f, null, FatigueLevel.HIGH)

    assertEquals(0, low.fatigueAdjustmentKcal)
    assertEquals(100, medium.fatigueAdjustmentKcal)
    assertEquals(200, high.fatigueAdjustmentKcal)
    assertEquals(base.totalKcal + 200, high.totalKcal)
  }

  @Test
  fun `total never goes below the safety floor`() {
    val need = DailyEnergyNeed.compute(
      weightKg = 40f, heightCm = 150, ageYears = 80,
      activityKcal = 0, sleepHours = 8f, phase = PhaseType.FOLLICULAR, fatigue = null
    )

    assertEquals(EnergyTarget.SAFETY_FLOOR, need.totalKcal)
  }

  @Test
  fun `fatigue levels have a french label and an adjustment`() {
    FatigueLevel.entries.forEach { level ->
      assertEquals(true, level.displayName.isNotBlank())
      assertEquals(true, level.kcalAdjustment >= 0)
    }
    assertEquals("Faible", FatigueLevel.LOW.displayName)
    assertEquals("Modérée", FatigueLevel.MEDIUM.displayName)
    assertEquals("Élevée", FatigueLevel.HIGH.displayName)
  }
}
