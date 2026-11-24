package io.tvelu77.freya.domain.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HealthScoreTest {

  @Test
  fun `nutrition score follows sufficiency bands and never punishes missing data`() {
    val cases = listOf(
      null to 2000 to 50,
      400 to 2000 to 40,
      1000 to 2000 to 40,
      1600 to 2000 to 70,
      1700 to 2000 to 100,
      2000 to 2000 to 100,
      2400 to 2000 to 100,
      2600 to 2000 to 70,
      3000 to 2000 to 50,
      2000 to 0 to 50
    )

    cases.forEach { (input, expected) ->
      val (consumed, target) = input
      val score = HealthScore.compute(
        caloriesConsumed = consumed, caloriesTarget = target,
        waterMl = null, waterTargetMl = 0,
        stepsCount = null, sportMinutes = 0, fatigue = null
      )
      assertEquals("consumed=$consumed target=$target", expected, score.nutritionScore)
    }
  }

  @Test
  fun `hydration score follows ratio bands and is neutral when missing`() {
    val cases = listOf(
      null to 2000 to 50,
      2000 to 2000 to 100,
      1400 to 2000 to 80,
      800 to 2000 to 60,
      200 to 2000 to 40,
      1800 to 0 to 50
    )

    cases.forEach { (input, expected) ->
      val (water, target) = input
      val score = HealthScore.compute(
        caloriesConsumed = null, caloriesTarget = 2000,
        waterMl = water, waterTargetMl = target,
        stepsCount = null, sportMinutes = 0, fatigue = null
      )
      assertEquals("water=$water target=$target", expected, score.hydrationScore)
    }
  }

  @Test
  fun `activity score combines steps and sport minutes and is neutral when missing`() {
    val cases = listOf(
      Triple(null, 0, 50),
      Triple(10000, 0, 100),
      Triple(5000, 30, 80),
      Triple(0, 0, 0),
      Triple(20000, 60, 100)
    )

    cases.forEach { (steps, sport, expectedScore) ->
      val score = HealthScore.compute(
        caloriesConsumed = null, caloriesTarget = 2000,
        waterMl = null, waterTargetMl = 0,
        stepsCount = steps, sportMinutes = sport, fatigue = null
      )
      assertEquals("steps=$steps sport=$sport", expectedScore, score.activityScore)
    }
  }

  @Test
  fun `signals score reflects self reported fatigue, neutral when absent`() {
    assertEquals(50, signalsOf(null))
    assertEquals(100, signalsOf(FatigueLevel.LOW))
    assertEquals(75, signalsOf(FatigueLevel.MEDIUM))
    assertEquals(50, signalsOf(FatigueLevel.HIGH))
  }

  private fun signalsOf(fatigue: FatigueLevel?): Int = HealthScore.compute(
    caloriesConsumed = null, caloriesTarget = 2000,
    waterMl = null, waterTargetMl = 0,
    stepsCount = null, sportMinutes = 0, fatigue = fatigue
  ).signalsScore

  @Test
  fun `perfect day with all signals reported reaches 100 and GOOD`() {
    val score = HealthScore.compute(
      caloriesConsumed = 2000, caloriesTarget = 2000,
      waterMl = 2000, waterTargetMl = 2000,
      stepsCount = 10000, sportMinutes = 30, fatigue = FatigueLevel.LOW
    )

    assertEquals(100, score.value)
    assertEquals(ScoreLevel.GOOD, score.level)
  }

  @Test
  fun `missing data everywhere stays neutral and is never a failure`() {
    val score = HealthScore.compute(
      caloriesConsumed = null, caloriesTarget = 2000,
      waterMl = null, waterTargetMl = 0,
      stepsCount = null, sportMinutes = 0, fatigue = null
    )

    assertEquals(50, score.value)
    assertEquals(ScoreLevel.MEDIUM, score.level)
  }

  @Test
  fun `very low day maps to LOW with the encouraging message`() {
    val score = HealthScore.compute(
      caloriesConsumed = 400, caloriesTarget = 2000,
      waterMl = 200, waterTargetMl = 2000,
      stepsCount = 0, sportMinutes = 0, fatigue = FatigueLevel.HIGH
    )

    assertEquals(34, score.value)
    assertEquals(ScoreLevel.LOW, score.level)
  }

  @Test
  fun `value is always clamped between 0 and 100`() {
    val perfect = HealthScore.compute(2000, 2000, 2000, 2000, 20000, 60, FatigueLevel.LOW)
    val empty = HealthScore.compute(null, 2000, null, 0, null, 0, null)

    assertTrue(perfect.value in 0..100)
    assertEquals(100, perfect.value)
    assertTrue(empty.value in 0..100)
  }
}
