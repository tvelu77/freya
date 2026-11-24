package io.tvelu77.freya.domain.models

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class ActivityEntryTest {

  private val date = LocalDate.of(2024, 6, 10)

  @Test
  fun `kcal burned equals MET x weight x duration in hours`() {
    val cases = listOf(
      Triple(SportType.RUNNING, 30, 60f) to 294,
      Triple(SportType.WALKING, 60, 60f) to 210,
      Triple(SportType.CYCLING, 40, 70f) to 350,
      Triple(SportType.YOGA, 45, 60f) to 135,
      Triple(SportType.SWIMMING, 30, 60f) to 210,
      Triple(SportType.STRENGTH, 45, 60f) to 270
    )

    cases.forEach { (input, expected) ->
      val (sport, durationMin, weight) = input
      assertEquals(
        "$sport ${durationMin}min @${weight}kg",
        expected,
        ActivityEntry.computeKcal(sport, durationMin, weight)
      )
    }
  }

  @Test
  fun `weight falls back to a neutral default when absent`() {
    assertEquals(294, ActivityEntry.computeKcal(SportType.RUNNING, 30, null))
    assertEquals(
      ActivityEntry.computeKcal(SportType.RUNNING, 30, ActivityEntry.DEFAULT_WEIGHT_KG),
      ActivityEntry.computeKcal(SportType.RUNNING, 30, null)
    )
  }

  @Test
  fun `zero duration burns zero kcal`() {
    assertEquals(0, ActivityEntry.computeKcal(SportType.RUNNING, 0, 60f))
  }

  @Test
  fun `sports have a MET value and a french label`() {
    SportType.entries.forEach { sport ->
      assertEquals(true, sport.met > 0)
      assertEquals(true, sport.displayName.isNotBlank())
    }
    assertEquals("Course à pied", SportType.RUNNING.displayName)
    assertEquals("Natation", SportType.SWIMMING.displayName)
  }
}
