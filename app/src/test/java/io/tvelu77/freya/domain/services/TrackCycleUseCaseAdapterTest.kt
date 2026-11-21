package io.tvelu77.freya.domain.services

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.tvelu77.freya.domain.models.CycleEntry
import io.tvelu77.freya.domain.models.CycleEntryResult
import io.tvelu77.freya.domain.models.PhaseType
import io.tvelu77.freya.domain.ports.api.TrackCycleUseCase
import io.tvelu77.freya.domain.ports.spi.CycleRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class TrackCycleUseCaseAdapterTest {
  private lateinit var repository: CycleRepository
  private lateinit var useCase: TrackCycleUseCase

  @Before
  fun setup() {
    repository = mockk()
    useCase = TrackCycleUseCaseAdapter(repository, PhaseCalculatorAdapter())
  }

  @Test
  fun `startCycle saves a new cycle`() = runTest {
    val date = LocalDate.of(2024, 3, 1)
    coEvery { repository.getCycleByDate(date) } returns null
    coEvery { repository.getAllCyclesOnce() } returns emptyList()
    coEvery { repository.saveCycle(any()) } returns 1L

    val result = useCase.startCycle(date)

    assertEquals(CycleEntryResult.Success(1L), result)
    coVerify { repository.saveCycle(match { it.startDate == date }) }
  }

  @Test
  fun `getCurrentPhase returns menstrual on day 2`() = runTest {
    val start = LocalDate.now().minusDays(1)
    val cycle = CycleEntry(startDate = start, endDate = null)
    every { repository.getAllCycles() } returns flowOf(listOf(cycle))

    val phase = useCase.getCurrentPhase().first()

    assertEquals(PhaseType.MENSTRUAL, phase?.phase)
  }

  @Test
  fun `getCurrentPhase returns null when no cycle exists`() = runTest {
    every { repository.getAllCycles() } returns flowOf(emptyList())

    val phase = useCase.getCurrentPhase().first()

    assertEquals(null, phase)
  }
}