package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.ports.spi.FakeUserProfileRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateUserProfileUseCaseAdapterTest {

  private val repository = FakeUserProfileRepository()
  private val adapter = UpdateUserProfileUseCaseAdapter(repository)

  @Test
  fun `default profile shows calories and has tca friendly mode off`() = runTest {
    val profile = GetUserProfileUseCaseAdapter(repository).execute().first()

    assertTrue(profile.showCalories)
    assertFalse(profile.tcaFriendlyMode)
  }

  @Test
  fun `tca friendly mode toggle is persisted in the repository`() = runTest {
    adapter.updateTcaFriendlyMode(true)

    assertTrue(repository.profile.tcaFriendlyMode)

    adapter.updateTcaFriendlyMode(false)

    assertFalse(repository.profile.tcaFriendlyMode)
  }

  @Test
  fun `base calories never go below the safety floor`() = runTest {
    adapter.updateBaseCalories(900)
    assertEquals(1200, repository.profile.baseCalories)

    adapter.updateBaseCalories(2200)
    assertEquals(2200, repository.profile.baseCalories)
  }

  @Test
  fun `cycle length stays within 21 to 45 days`() = runTest {
    adapter.updateCycleLength(10)
    assertEquals(21, repository.profile.cycleLengthDays)

    adapter.updateCycleLength(60)
    assertEquals(45, repository.profile.cycleLengthDays)
  }

  @Test
  fun `first name is trimmed before saving`() = runTest {
    adapter.updateFirstName("  Léa  ")

    assertEquals("Léa", repository.profile.firstName)
  }
}
