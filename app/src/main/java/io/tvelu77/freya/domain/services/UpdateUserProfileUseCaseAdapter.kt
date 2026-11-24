package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.Goal
import io.tvelu77.freya.domain.ports.api.UpdateUserProfileUseCase
import io.tvelu77.freya.domain.ports.spi.UserProfileRepository
import jakarta.inject.Inject

class UpdateUserProfileUseCaseAdapter @Inject constructor(
  private val repository: UserProfileRepository
): UpdateUserProfileUseCase {

  override suspend fun updateFirstName(name: String) =
    repository.updateFirstName(name.trim())

  override suspend fun updateBaseCalories(calories: Int) {
    val safe = calories.coerceAtLeast(1200)
    repository.updateBaseCalories(safe)
  }

  override suspend fun updateGoal(goal: Goal) =
    repository.updateGoal(goal)

  override suspend fun updateWeight(weightKg: Float?) =
    repository.updateWeight(weightKg?.takeIf { it > 0f })

  override suspend fun updateAgeYears(ageYears: Int?) =
    repository.updateAgeYears(ageYears?.coerceIn(MIN_AGE_YEARS, MAX_AGE_YEARS))

  override suspend fun updateHeightCm(heightCm: Int?) =
    repository.updateHeightCm(heightCm?.coerceIn(MIN_HEIGHT_CM, MAX_HEIGHT_CM))

  override suspend fun updateCycleLength(days: Int) {
    val safe = days.coerceIn(21, 45)
    repository.updateCycleLength(safe)
  }

  override suspend fun updateTcaFriendlyMode(enabled: Boolean) =
    repository.updateTcaFriendlyMode(enabled)

  override suspend fun updateNotifications(enabled: Boolean) =
    repository.updateNotifications(enabled)

  override suspend fun completeOnboarding() =
    repository.completeOnboarding()

  companion object {
    const val MIN_AGE_YEARS = 10
    const val MAX_AGE_YEARS = 120
    const val MIN_HEIGHT_CM = 100
    const val MAX_HEIGHT_CM = 250
  }
}