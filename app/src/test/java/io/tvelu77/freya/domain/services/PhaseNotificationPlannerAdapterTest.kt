package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.NutritionAdvice
import io.tvelu77.freya.domain.models.PhaseInfo
import io.tvelu77.freya.domain.models.PhaseType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class PhaseNotificationPlannerAdapterTest {

  private val planner = PhaseNotificationPlannerAdapter()

  private fun phaseInfo(phase: PhaseType, start: LocalDate) = PhaseInfo(
    phase = phase,
    startDate = start,
    endDate = start.plusDays(13),
    daysInCycle = 20,
    nutritionAdvice = NutritionAdvice.forPhase(phase)
  )

  @Test
  fun `plans a phase notification when the phase starts today`() {
    val now = LocalDateTime.of(2024, 6, 10, 8, 0)

    val result = planner.plan(phaseInfo(PhaseType.LUTEAL, LocalDate.of(2024, 6, 10)), null, now)

    assertEquals(1, result.size)
    assertEquals(LocalDateTime.of(2024, 6, 10, 9, 0), result[0].triggerAt)
    assertTrue(result[0].title.contains("lutéale"))
    assertTrue(result[0].message.isNotEmpty())
  }

  @Test
  fun `plans a period reminder the day before the predicted period`() {
    val now = LocalDateTime.of(2024, 6, 10, 8, 0)

    val result = planner.plan(null, LocalDate.of(2024, 6, 12), now)

    assertEquals(1, result.size)
    assertEquals(LocalDateTime.of(2024, 6, 11, 18, 0), result[0].triggerAt)
  }

  @Test
  fun `plans both phase and period notifications when both are upcoming`() {
    val now = LocalDateTime.of(2024, 6, 10, 8, 0)

    val result = planner.plan(
      phaseInfo(PhaseType.LUTEAL, LocalDate.of(2024, 6, 10)),
      LocalDate.of(2024, 6, 25),
      now
    )

    assertEquals(2, result.size)
  }

  @Test
  fun `skips notifications scheduled in the past`() {
    val now = LocalDateTime.of(2024, 6, 10, 20, 0)

    val result = planner.plan(
      phaseInfo(PhaseType.MENSTRUAL, LocalDate.of(2024, 6, 8)),
      LocalDate.of(2024, 6, 9),
      now
    )

    assertTrue(result.isEmpty())
  }

  @Test
  fun `plans nothing without phase and period`() {
    assertTrue(planner.plan(null, null, LocalDateTime.of(2024, 6, 10, 8, 0)).isEmpty())
  }
}
