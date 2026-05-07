package io.tvelu77.freya.domain.services

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.tvelu77.freya.domain.models.CycleEntry
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
    coEvery { repository.saveCycle(any()) } returns 1L

    val id = useCase.startCycle(date)

    assertEquals(1L, id)
    coVerify { repository.saveCycle(match { it.startDate == date }) }
  }

  @Test
  fun `getCurrentPhase returns menstrual on day 2`() = runTest {
    val start = LocalDate.now().minusDays(1)
    val cycle = CycleEntry(startDate = start, endDate = null)
    every { repository.getLatestCycle() } returns flowOf(cycle)

    val phase = useCase.getCurrentPhase().first()

    assertEquals(PhaseType.MENSTRUAL, phase?.phase)
  }

  @Test
  fun `getCurrentPhase returns null when no cycle exists`() = runTest {
    every { repository.getLatestCycle() } returns flowOf(null)

    val phase = useCase.getCurrentPhase().first()

    assertEquals(null, phase)
  }
}