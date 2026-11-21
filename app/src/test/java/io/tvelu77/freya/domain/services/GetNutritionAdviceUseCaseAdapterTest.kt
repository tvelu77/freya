package io.tvelu77.freya.domain.services

import io.mockk.every
import io.mockk.mockk
import io.tvelu77.freya.domain.models.CycleEntry
import io.tvelu77.freya.domain.models.NutritionAdvice
import io.tvelu77.freya.domain.models.PhaseInfo
import io.tvelu77.freya.domain.models.PhaseType
import io.tvelu77.freya.domain.ports.spi.CycleRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class GetNutritionAdviceUseCaseAdapterTest {

  private lateinit var repository: CycleRepository
  private lateinit var adapter: GetNutritionAdviceUseCaseAdapter

  private val today = LocalDate.of(2024, 6, 10)

  @Before
  fun setup() {
    repository = mockk()
    adapter = GetNutritionAdviceUseCaseAdapter(repository, PhaseCalculatorAdapter())
  }

  @Test
  fun `advice is null when there is no cycle`() = runTest {
    every { repository.getLatestCycle() } returns flowOf(null)

    assertNull(adapter.getAdviceForToday(today).first())
  }

  @Test
  fun `advice matches the current phase`() = runTest {
    val cycle = CycleEntry(startDate = today.minusDays(2), endDate = null, cycleLengthDays = 28)
    every { repository.getLatestCycle() } returns flowOf(cycle)

    val advice = adapter.getAdviceForToday(today).first()

    assertEquals(PhaseType.MENSTRUAL, advice?.phaseType)
    assertTrue(advice?.keyNutrients?.contains("Fer") == true)
  }

  @Test
  fun `luteal phase advice focuses on complex carbs`() = runTest {
    val cycle = CycleEntry(startDate = today.minusDays(25), endDate = null, cycleLengthDays = 28)
    every { repository.getLatestCycle() } returns flowOf(cycle)

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
