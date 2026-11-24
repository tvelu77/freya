package io.tvelu77.freya.presentation.onboarding

import io.tvelu77.freya.domain.models.Goal
import io.tvelu77.freya.domain.ports.spi.FakeUserProfileRepository
import io.tvelu77.freya.domain.services.UpdateUserProfileUseCaseAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

  private val dispatcher = StandardTestDispatcher()
  private val repository = FakeUserProfileRepository()
  private val viewModel = OnboardingViewModel(UpdateUserProfileUseCaseAdapter(repository))

  @Before
  fun setUp() {
    Dispatchers.setMain(dispatcher)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun `onFinish persists every onboarding field and completes onboarding`() = runTest {
    viewModel.onFirstNameChanged("Léa")
    viewModel.onCaloriesChanged(1800)
    viewModel.onGoalChanged(Goal.LOSS)
    viewModel.onCycleLengthInputChanged("30")
    viewModel.onWeightInputChanged("65")
    viewModel.onAgeInputChanged("28")
    viewModel.onHeightInputChanged("170")
    viewModel.onFriendlyModeToggled(true)
    viewModel.onNotificationsToggled(false)

    var finished = false
    viewModel.onFinish { finished = true }
    advanceUntilIdle()

    val profile = repository.profile
    assertEquals("Léa", profile.firstName)
    assertEquals(1800, profile.baseCalories)
    assertEquals(Goal.LOSS, profile.goal)
    assertEquals(30, profile.cycleLengthDays)
    assertEquals(65f, profile.weightKg)
    assertEquals(28, profile.ageYears)
    assertEquals(170, profile.heightCm)
    assertTrue(profile.tcaFriendlyMode)
    assertEquals(false, profile.notificationsEnabled)
    assertTrue(profile.onboardingCompleted)
    assertTrue(finished)
  }

  @Test
  fun `onFinish with only defaults keeps neutral optional fields and does not crash`() = runTest {
    var finished = false
    viewModel.onFinish { finished = true }
    advanceUntilIdle()

    val profile = repository.profile
    assertEquals(2000, profile.baseCalories)
    assertEquals(Goal.MAINTENANCE, profile.goal)
    assertEquals(28, profile.cycleLengthDays)
    assertNull(profile.weightKg)
    assertNull(profile.ageYears)
    assertNull(profile.heightCm)
    assertTrue(profile.onboardingCompleted)
    assertTrue(finished)
  }
}
