package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.Goal
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

  @Test
  fun `goal defaults to maintenance and is persisted when changed`() = runTest {
    assertEquals(Goal.MAINTENANCE, repository.profile.goal)

    adapter.updateGoal(Goal.LOSS)

    assertEquals(Goal.LOSS, repository.profile.goal)
  }

  @Test
  fun `age and height stay within sane bounds and can be cleared`() = runTest {
    adapter.updateAgeYears(8)
    assertEquals(10, repository.profile.ageYears)

    adapter.updateAgeYears(130)
    assertEquals(120, repository.profile.ageYears)

    adapter.updateHeightCm(90)
    assertEquals(100, repository.profile.heightCm)

    adapter.updateHeightCm(300)
    assertEquals(250, repository.profile.heightCm)

    adapter.updateAgeYears(null)
    adapter.updateHeightCm(null)
    assertEquals(null, repository.profile.ageYears)
    assertEquals(null, repository.profile.heightCm)
  }
}
