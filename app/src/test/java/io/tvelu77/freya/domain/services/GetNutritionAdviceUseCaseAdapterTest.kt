package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.CycleEntry
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
  fun `calorie target adds the phase surplus`() {
    val lutealInfo = PhaseInfo(
      phase = PhaseType.LUTEAL,
      startDate = today.minusDays(5),
      endDate = today.plusDays(2),
      daysInCycle = 26,
      nutritionAdvice = NutritionAdvice.forPhase(PhaseType.LUTEAL)
    )

    assertEquals(2150, adapter.getCalorieTarget(2000, lutealInfo))
  }

  @Test
  fun `calorie target never goes below the safety floor`() {
    val menstrualInfo = PhaseInfo(
      phase = PhaseType.MENSTRUAL,
      startDate = today.minusDays(2),
      endDate = today.plusDays(2),
      daysInCycle = 3,
      nutritionAdvice = NutritionAdvice.forPhase(PhaseType.MENSTRUAL)
    )

    assertEquals(1200, adapter.getCalorieTarget(1000, menstrualInfo))
  }

  @Test
  fun `calorie target is the base when there is no phase`() {
    assertEquals(2000, adapter.getCalorieTarget(2000, null))
  }
}
