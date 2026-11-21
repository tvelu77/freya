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

  @Test
  fun `day in cycle wraps with the personalized cycle length`() {
    val cycle = CycleEntry(startDate = LocalDate.of(2024, 1, 1), endDate = null, cycleLengthDays = 35)
    val phaseInfo = calculator.getCurrentPhase(cycle, LocalDate.of(2024, 2, 5))
    assertEquals(1, phaseInfo.daysInCycle)
    assertEquals(PhaseType.MENSTRUAL, phaseInfo.phase)
  }

  @Test
  fun `menstrual phase lasts as long as the recorded period`() {
    val cycle = CycleEntry(
      startDate = LocalDate.of(2024, 1, 1),
      endDate = LocalDate.of(2024, 1, 7),
      cycleLengthDays = 28
    )
    val phaseInfo = calculator.getCurrentPhase(cycle, LocalDate.of(2024, 1, 7))
    assertEquals(PhaseType.MENSTRUAL, phaseInfo.phase)
  }

  @Test
  fun `averageCycleLength averages the gaps between cycle starts`() {
    val cycles = listOf(
      CycleEntry(startDate = LocalDate.of(2024, 1, 1), endDate = null),
      CycleEntry(startDate = LocalDate.of(2024, 1, 29), endDate = null),
      CycleEntry(startDate = LocalDate.of(2024, 2, 28), endDate = null)
    )
    assertEquals(29, calculator.averageCycleLength(cycles))
  }

  @Test
  fun `averageCycleLength clamps to the max of 45 days`() {
    val cycles = listOf(
      CycleEntry(startDate = LocalDate.of(2024, 1, 1), endDate = null),
      CycleEntry(startDate = LocalDate.of(2024, 2, 20), endDate = null),
      CycleEntry(startDate = LocalDate.of(2024, 4, 10), endDate = null)
    )
    assertEquals(45, calculator.averageCycleLength(cycles))
  }

  @Test
  fun `averageCycleLength clamps to the min of 21 days`() {
    val cycles = listOf(
      CycleEntry(startDate = LocalDate.of(2024, 1, 1), endDate = null),
      CycleEntry(startDate = LocalDate.of(2024, 1, 11), endDate = null),
      CycleEntry(startDate = LocalDate.of(2024, 1, 21), endDate = null)
    )
    assertEquals(21, calculator.averageCycleLength(cycles))
  }

  @Test
  fun `averageCycleLength uses the fallback without enough history`() {
    assertEquals(28, calculator.averageCycleLength(emptyList()))
    val single = listOf(CycleEntry(startDate = LocalDate.of(2024, 1, 1), endDate = null, cycleLengthDays = 32))
    assertEquals(32, calculator.averageCycleLength(single, fallback = 32))
  }
}