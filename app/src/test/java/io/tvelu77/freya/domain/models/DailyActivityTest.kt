package io.tvelu77.freya.domain.models

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class DailyActivityTest {

  private val date = LocalDate.of(2024, 6, 10)

  @Test
  fun `steps kcal estimate equals walking MET x weight x walking time`() {
    val cases = listOf(
      Triple(10000, 60f, 320),
      Triple(5000, 70f, 186),
      Triple(8000, 50f, 213),
      Triple(0, 60f, 0)
    )

    cases.forEach { (steps, weight, expected) ->
      assertEquals(
        "$steps steps @${weight}kg",
        expected,
        DailyActivity.estimateStepsKcal(steps, weight)
      )
    }
  }

  @Test
  fun `steps kcal estimate falls back to the neutral default weight when absent`() {
    assertEquals(
      DailyActivity.estimateStepsKcal(10000, ActivityEntry.DEFAULT_WEIGHT_KG),
      DailyActivity.estimateStepsKcal(10000, null)
    )
  }

  @Test
  fun `instance estimate delegates to the companion with its own steps`() {
    val activity = DailyActivity(date = date, steps = 10000, sleepHours = 7.5f)

    assertEquals(320, activity.estimateStepsKcal(60f))
  }
}
