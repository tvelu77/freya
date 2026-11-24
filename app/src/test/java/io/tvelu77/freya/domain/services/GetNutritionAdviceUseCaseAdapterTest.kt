package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.CycleEntry
import io.tvelu77.freya.domain.models.Goal
import io.tvelu77.freya.domain.models.NutritionAdvice
import io.tvelu77.freya.domain.models.PhaseInfo
import io.tvelu77.freya.domain.models.PhaseType
import io.tvelu77.freya.domain.ports.spi.FakeCycleRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class GetNutritionAdviceUseCaseAdapterTest {

  private val repository = FakeCycleRepository()
  private val adapter = GetNutritionAdviceUseCaseAdapter(repository, PhaseCalculatorAdapter())

  private val today = LocalDate.of(2024, 6, 10)

  @Test
  fun `advice is null when there is no cycle`() = runTest {
    assertNull(adapter.getAdviceForToday(today).first())
  }

  @Test
  fun `advice matches the current phase`() = runTest {
    repository.cycles.add(
      CycleEntry(startDate = today.minusDays(2), endDate = null, cycleLengthDays = 28)
    )

    val advice = adapter.getAdviceForToday(today).first()

    assertEquals(PhaseType.MENSTRUAL, advice?.phaseType)
    assertTrue(advice?.keyNutrients?.contains("Fer") == true)
  }

  @Test
  fun `luteal phase advice focuses on complex carbs`() = runTest {
    repository.cycles.add(
      CycleEntry(startDate = today.minusDays(25), endDate = null, cycleLengthDays = 28)
    )

    val advice = adapter.getAdviceForToday(today).first()

    assertEquals(PhaseType.LUTEAL, advice?.phaseType)
    assertTrue(advice?.keyNutrients?.contains("Glucides complexes") == true)
  }

  @Test
  fun `advice changes with the current phase and carries concrete food examples`() = runTest {
    val cases = listOf(
      Triple(PhaseType.MENSTRUAL, 2L, "Lentilles cuites"),
      Triple(PhaseType.FOLLICULAR, 8L, "Quinoa cuit"),
      Triple(PhaseType.OVULATORY, 13L, "Saumon grillé"),
      Triple(PhaseType.LUTEAL, 25L, "Riz complet cuit")
    )

    cases.forEach { (phase, daysAgo, expectedFood) ->
      repository.cycles.clear()
      repository.cycles.add(
        CycleEntry(startDate = today.minusDays(daysAgo), endDate = null, cycleLengthDays = 28)
      )

      val advice = adapter.getAdviceForToday(today).first()

      assertEquals(phase, advice?.phaseType)
      assertTrue(advice?.recommendedFoods?.contains(expectedFood) == true)
    }
  }

  @Test
  fun `energy target adds the phase surplus to the range`() {
    val lutealInfo = PhaseInfo(
      phase = PhaseType.LUTEAL,
      startDate = today.minusDays(5),
      endDate = today.plusDays(2),
      daysInCycle = 26,
      nutritionAdvice = NutritionAdvice.forPhase(PhaseType.LUTEAL)
    )

    val target = adapter.getEnergyTarget(2000, Goal.MAINTENANCE, lutealInfo)

    assertEquals(2050, target.low)
    assertEquals(2250, target.high)
  }

  @Test
  fun `energy target applies the goal and never goes below the safety floor`() {
    val menstrualInfo = PhaseInfo(
      phase = PhaseType.MENSTRUAL,
      startDate = today.minusDays(2),
      endDate = today.plusDays(2),
      daysInCycle = 3,
      nutritionAdvice = NutritionAdvice.forPhase(PhaseType.MENSTRUAL)
    )

    val target = adapter.getEnergyTarget(1000, Goal.LOSS, menstrualInfo)

    assertEquals(1200, target.low)
    assertEquals(1300, target.high)
  }

  @Test
  fun `energy target is goal-only when there is no phase`() {
    val target = adapter.getEnergyTarget(2000, Goal.GAIN, null)

    assertEquals(2100, target.low)
    assertEquals(2300, target.high)
  }
}
