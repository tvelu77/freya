package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.SportType
import io.tvelu77.freya.domain.ports.spi.FakeActivityRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class LogActivityUseCaseAdapterTest {

  private val repository = FakeActivityRepository()
  private val adapter = LogActivityUseCaseAdapter(repository)

  private val date = LocalDate.of(2024, 6, 10)

  @Test
  fun `logActivity computes kcal in the domain and persists the entry`() = runTest {
    val id = adapter.logActivity(
      sport = SportType.RUNNING,
      durationMin = 30,
      weightKg = 60f,
      date = date
    )

    assertEquals(1L, id)
    val saved = repository.entries.single()
    assertEquals(SportType.RUNNING, saved.sportType)
    assertEquals(30, saved.durationMin)
    assertEquals(294, saved.kcalBurned)
    assertEquals(date, saved.date)
  }

  @Test
  fun `logActivity uses the neutral default weight when absent`() = runTest {
    adapter.logActivity(SportType.WALKING, 60, weightKg = null, date = date)

    assertEquals(210, repository.entries.single().kcalBurned)
  }

  @Test
  fun `getActivitiesForDay returns only that day entries`() = runTest {
    adapter.logActivity(SportType.YOGA, 45, 60f, date = date)
    adapter.logActivity(SportType.CYCLING, 40, 60f, date = date.plusDays(1))

    val entries = adapter.getActivitiesForDay(date).first()

    assertEquals(1, entries.size)
    assertEquals(SportType.YOGA, entries.single().sportType)
  }

  @Test
  fun `getTotalKcalBurned sums the day`() = runTest {
    adapter.logActivity(SportType.RUNNING, 30, 60f, date = date)
    adapter.logActivity(SportType.WALKING, 60, 60f, date = date)

    assertEquals(294 + 210, adapter.getTotalKcalBurned(date))
  }

  @Test
  fun `deleteActivity removes the entry`() = runTest {
    val id = adapter.logActivity(SportType.YOGA, 45, 60f, date = date)

    adapter.deleteActivity(id)

    assertTrue(repository.entries.isEmpty())
  }

  @Test
  fun `updateActivity re-saves the entry under the same id with new values`() = runTest {
    val id = adapter.logActivity(SportType.YOGA, 45, 60f, date = date)
    val original = repository.entries.single()

    adapter.updateActivity(original.copy(durationMin = 60, kcalBurned = 180))

    assertEquals(1, repository.entries.size)
    assertEquals(id, repository.entries.single().id)
    assertEquals(60, repository.entries.single().durationMin)
    assertEquals(180, repository.entries.single().kcalBurned)
  }
}
