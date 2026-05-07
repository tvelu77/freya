package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.CycleEntry
import io.tvelu77.freya.domain.models.PhaseType
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class PhaseCalculatorAdapterTest {

  private val calculator = PhaseCalculatorAdapter()

  @Test
  fun `getCurrentPhase returns correct phase for day 3`() {
    val startDate = LocalDate.now().minusDays(2)
    val cycle = CycleEntry(startDate = startDate, endDate = null)
    val phaseInfo = calculator.getCurrentPhase(cycle)
    assertEquals(PhaseType.MENSTRUAL, phaseInfo.phase)
    assertEquals(3, phaseInfo.daysInCycle)
  }

  @Test
  fun `predict 3 next cycles`() {
    val cycle = CycleEntry(startDate = LocalDate.of(2024, 1, 1), endDate = null, cycleLengthDays = 28)
    val predictions = calculator.predictNextCycles(cycle, 3)
    assertEquals(LocalDate.of(2024, 1, 29), predictions[0])
    assertEquals(LocalDate.of(2024, 2, 26), predictions[1])
    assertEquals(LocalDate.of(2024, 3, 25), predictions[2])
  }
}