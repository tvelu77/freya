package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.CycleEntry
import io.tvelu77.freya.domain.models.CycleEntryResult
import io.tvelu77.freya.domain.ports.spi.FakeCycleRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class TrackCycleValidationTest {

  private lateinit var repository: FakeCycleRepository
  private lateinit var useCase: TrackCycleUseCaseAdapter

  @Before
  fun setup() {
    repository = FakeCycleRepository()
    useCase = TrackCycleUseCaseAdapter(repository, PhaseCalculatorAdapter())
  }

  @Test
  fun `startCycle succeeds and saves when there is no overlap`() = runTest {
    val result = useCase.startCycle(LocalDate.of(2024, 3, 1))

    assertEquals(CycleEntryResult.Success(1L), result)
    assertEquals(1, repository.cycles.size)
  }

  @Test
  fun `startCycle returns error when the date overlaps a recorded period`() = runTest {
    repository.cycles.add(
      CycleEntry(id = 1, startDate = LocalDate.of(2024, 3, 1), endDate = LocalDate.of(2024, 3, 5), cycleLengthDays = 28)
    )

    val result = useCase.startCycle(LocalDate.of(2024, 3, 3))

    assertTrue(result is CycleEntryResult.Error)
    assertEquals(1, repository.cycles.size)
  }

  @Test
  fun `startCycle closes an ongoing cycle when starting inside it`() = runTest {
    repository.cycles.add(
      CycleEntry(id = 1, startDate = LocalDate.of(2024, 3, 1), endDate = null, cycleLengthDays = 28)
    )

    val result = useCase.startCycle(LocalDate.of(2024, 3, 3))

    assertTrue(result is CycleEntryResult.Success)
    assertEquals(LocalDate.of(2024, 3, 2), repository.cycles.first { it.id == 1L }.endDate)
  }

  @Test
  fun `endPeriod returns error when the end date is before the start date`() = runTest {
    val cycle = CycleEntry(id = 1, startDate = LocalDate.of(2024, 3, 10), endDate = null, cycleLengthDays = 28)

    val result = useCase.endPeriod(cycle, LocalDate.of(2024, 3, 5))

    assertTrue(result is CycleEntryResult.Error)
  }

  @Test
  fun `endPeriod saves the end date and computes the length from the previous cycle`() = runTest {
    repository.cycles.add(
      CycleEntry(id = 1, startDate = LocalDate.of(2024, 2, 1), endDate = LocalDate.of(2024, 2, 5), cycleLengthDays = 28)
    )
    repository.cycles.add(
      CycleEntry(id = 2, startDate = LocalDate.of(2024, 3, 1), endDate = null, cycleLengthDays = 28)
    )

    val result = useCase.endPeriod(repository.cycles.first { it.id == 2L }, LocalDate.of(2024, 3, 6))

    assertTrue(result is CycleEntryResult.Success)
    val updated = repository.cycles.first { it.id == 2L }
    assertEquals(LocalDate.of(2024, 3, 6), updated.endDate)
    assertEquals(29, updated.cycleLengthDays)
  }

  @Test
  fun `updateCycle returns error when the end date precedes the start date`() = runTest {
    repository.cycles.add(
      CycleEntry(id = 1, startDate = LocalDate.of(2024, 3, 1), endDate = LocalDate.of(2024, 3, 5), cycleLengthDays = 28)
    )

    val result = useCase.updateCycle(
      CycleEntry(id = 1, startDate = LocalDate.of(2024, 3, 10), endDate = LocalDate.of(2024, 3, 5), cycleLengthDays = 28)
    )

    assertTrue(result is CycleEntryResult.Error)
  }

  @Test
  fun `updateCycle returns error when it overlaps another recorded period`() = runTest {
    repository.cycles.add(
      CycleEntry(id = 1, startDate = LocalDate.of(2024, 2, 1), endDate = LocalDate.of(2024, 2, 6), cycleLengthDays = 28)
    )
    repository.cycles.add(
      CycleEntry(id = 2, startDate = LocalDate.of(2024, 3, 1), endDate = null, cycleLengthDays = 28)
    )

    val result = useCase.updateCycle(
      CycleEntry(id = 2, startDate = LocalDate.of(2024, 2, 3), endDate = null, cycleLengthDays = 28)
    )

    assertTrue(result is CycleEntryResult.Error)
  }

  @Test
  fun `updateCycle saves a valid edit`() = runTest {
    repository.cycles.add(
      CycleEntry(id = 1, startDate = LocalDate.of(2024, 3, 1), endDate = LocalDate.of(2024, 3, 5), cycleLengthDays = 28)
    )

    val result = useCase.updateCycle(
      CycleEntry(id = 1, startDate = LocalDate.of(2024, 3, 2), endDate = LocalDate.of(2024, 3, 6), cycleLengthDays = 28)
    )

    assertEquals(CycleEntryResult.Success(1L), result)
    assertEquals(LocalDate.of(2024, 3, 2), repository.cycles.single().startDate)
    assertEquals(LocalDate.of(2024, 3, 6), repository.cycles.single().endDate)
  }

  @Test
  fun `deleteCycle removes the cycle`() = runTest {
    repository.cycles.add(
      CycleEntry(id = 7, startDate = LocalDate.of(2024, 3, 1), endDate = null, cycleLengthDays = 28)
    )

    useCase.deleteCycle(7L)

    assertTrue(repository.cycles.isEmpty())
  }
}
