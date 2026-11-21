package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.CycleEntryFactory
import io.tvelu77.freya.domain.ports.spi.FakeCycleRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class GetCycleStatsUseCaseAdapterTest {

  private lateinit var repository: FakeCycleRepository
  private lateinit var useCase: GetCycleStatsUseCaseAdapter

  @Before
  fun setup() {
    repository = FakeCycleRepository()
    useCase = GetCycleStatsUseCaseAdapter(repository)
  }

  @Test
  fun `stats are null without history`() = runTest {
    assertNull(useCase.execute().first())
  }

  @Test
  fun `stats average cycle gaps, period lengths and variability`() = runTest {
    repository.cycles.add(
      CycleEntryFactory.cycleEntry(id = 1, startDate = LocalDate.of(2024, 1, 1), endDate = LocalDate.of(2024, 1, 5))
    )
    repository.cycles.add(
      CycleEntryFactory.cycleEntry(id = 2, startDate = LocalDate.of(2024, 1, 29), endDate = LocalDate.of(2024, 2, 4))
    )
    repository.cycles.add(
      CycleEntryFactory.cycleEntry(id = 3, startDate = LocalDate.of(2024, 2, 28))
    )

    val stats = useCase.execute().first()

    assertEquals(3, stats?.cyclesCounted)
    assertEquals(29, stats?.averageCycleLengthDays)
    assertEquals(6, stats?.averagePeriodLengthDays)
    assertEquals(1.0, stats?.variabilityDays ?: -1.0, 0.01)
  }

  @Test
  fun `stats fall back to the entry length with a single cycle`() = runTest {
    repository.cycles.add(
      CycleEntryFactory.cycleEntry(id = 1, startDate = LocalDate.of(2024, 1, 1), cycleLengthDays = 32)
    )

    val stats = useCase.execute().first()

    assertEquals(32, stats?.averageCycleLengthDays)
    assertEquals(0, stats?.averagePeriodLengthDays)
    assertEquals(0.0, stats?.variabilityDays ?: -1.0, 0.001)
  }

  @Test
  fun `stats only count the last N cycles`() = runTest {
    (1..8).forEach { i ->
      repository.cycles.add(
        CycleEntryFactory.cycleEntry(
          id = i.toLong(),
          startDate = LocalDate.of(2024, 1, 1).plusDays(28L * i)
        )
      )
    }

    val stats = useCase.execute(take = 6).first()

    assertEquals(6, stats?.cyclesCounted)
    assertEquals(28, stats?.averageCycleLengthDays)
  }

  @Test
  fun `stats handle irregular cycles without crashing`() = runTest {
    repository.cycles.add(CycleEntryFactory.cycleEntry(id = 1, startDate = LocalDate.of(2024, 1, 1)))
    repository.cycles.add(CycleEntryFactory.cycleEntry(id = 2, startDate = LocalDate.of(2024, 1, 20)))
    repository.cycles.add(CycleEntryFactory.cycleEntry(id = 3, startDate = LocalDate.of(2024, 3, 15)))

    val stats = useCase.execute().first()

    assertEquals(3, stats?.cyclesCounted)
    assertEquals(37, stats?.averageCycleLengthDays)
    assertEquals(18.0, stats?.variabilityDays ?: -1.0, 0.01)
  }
}
