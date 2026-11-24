package io.tvelu77.freya.domain.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EnergyTargetTest {

  @Test
  fun `maintenance keeps the base calories as the center of the range`() {
    val target = EnergyTarget.compute(2000, Goal.MAINTENANCE, calorieSurplus = 0)

    assertEquals(1900, target.low)
    assertEquals(2100, target.high)
  }

  @Test
  fun `goal shifts the center of the range`() {
    assertEquals(2100, EnergyTarget.compute(2000, Goal.GAIN, 0).center)
    assertEquals(1800, EnergyTarget.compute(2000, Goal.LOSS, 0).center)
  }

  @Test
  fun `phase surplus shifts the range too`() {
    val luteal = EnergyTarget.compute(2000, Goal.MAINTENANCE, calorieSurplus = 150)

    assertEquals(2050, luteal.low)
    assertEquals(2250, luteal.high)
  }

  @Test
  fun `every phase x goal combination stays a soft 200 kcal range above the safety floor`() {
    val phaseSurpluses = mapOf(
      PhaseType.MENSTRUAL to 0,
      PhaseType.FOLLICULAR to -50,
      PhaseType.OVULATORY to -50,
      PhaseType.LUTEAL to 150
    )

    phaseSurpluses.forEach { (phase, surplus) ->
      Goal.entries.forEach { goal ->
        val target = EnergyTarget.compute(2000, goal, surplus)

        assertTrue("$phase/$goal low>=1200", target.low >= EnergyTarget.SAFETY_FLOOR)
        assertEquals("$phase/$goal width", 2 * EnergyTarget.RANGE_HALF_WIDTH, target.high - target.low)
      }
    }
  }

  @Test
  fun `the range never drops below the 1200 kcal safety floor`() {
    val target = EnergyTarget.compute(1000, Goal.LOSS, calorieSurplus = -50)

    assertEquals(1200, target.low)
    assertEquals(1300, target.high)
  }

  @Test
  fun `goals have a french label and a soft delta`() {
    assertEquals("Maintien", Goal.MAINTENANCE.displayName)
    assertEquals(0, Goal.MAINTENANCE.calorieDelta)
    assertTrue(Goal.GAIN.calorieDelta > 0)
    assertTrue(Goal.LOSS.calorieDelta < 0)
  }
}
