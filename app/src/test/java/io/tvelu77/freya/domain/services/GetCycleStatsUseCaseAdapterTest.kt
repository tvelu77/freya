package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.CycleEntry
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

  private fun cycle(id: Long, start: LocalDate, end: LocalDate? = null, length: Int = 28) =
    CycleEntry(id = id, startDate = start, endDate = end, cycleLengthDays = length)

  @Test
  fun `stats are null without history`() = runTest {
    assertNull(useCase.execute().first())
  }

  @Test
  fun `stats average cycle gaps, period lengths and variability`() = runTest {
    repository.cycles.add(cycle(1, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 5)))
    repository.cycles.add(cycle(2, LocalDate.of(2024, 1, 29), LocalDate.of(2024, 2, 4)))
    repository.cycles.add(cycle(3, LocalDate.of(2024, 2, 28), null))

    val stats = useCase.execute().first()

    assertEquals(3, stats?.cyclesCounted)
    assertEquals(29, stats?.averageCycleLengthDays)
    assertEquals(6, stats?.averagePeriodLengthDays)
    assertEquals(1.0, stats?.variabilityDays ?: -1.0, 0.01)
  }

  @Test
  fun `stats fall back to the entry length with a single cycle`() = runTest {
    repository.cycles.add(cycle(1, LocalDate.of(2024, 1, 1), null, length = 32))

    val stats = useCase.execute().first()

    assertEquals(32, stats?.averageCycleLengthDays)
    assertEquals(0, stats?.averagePeriodLengthDays)
    assertEquals(0.0, stats?.variabilityDays ?: -1.0, 0.001)
  }

  @Test
  fun `stats only count the last N cycles`() = runTest {
    (1..8).forEach { i ->
      repository.cycles.add(
        cycle(i.toLong(), LocalDate.of(2024, 1, 1).plusDays(28L * i), null)
      )
    }

    val stats = useCase.execute(take = 6).first()

    assertEquals(6, stats?.cyclesCounted)
    assertEquals(28, stats?.averageCycleLengthDays)
  }

  @Test
  fun `stats handle irregular cycles without crashing`() = runTest {
    repository.cycles.add(cycle(1, LocalDate.of(2024, 1, 1), null))
    repository.cycles.add(cycle(2, LocalDate.of(2024, 1, 20), null))
    repository.cycles.add(cycle(3, LocalDate.of(2024, 3, 15), null))

    val stats = useCase.execute().first()

    assertEquals(3, stats?.cyclesCounted)
    assertEquals(37, stats?.averageCycleLengthDays)
    assertEquals(18.0, stats?.variabilityDays ?: -1.0, 0.01)
  }
}
