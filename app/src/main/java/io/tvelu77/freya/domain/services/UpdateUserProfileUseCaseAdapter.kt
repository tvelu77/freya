package io.tvelu77.freya.domain.services

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

  override suspend fun updateCycleLength(days: Int) {
    val safe = days.coerceIn(21, 45)
    repository.updateCycleLength(safe)
  }

  override suspend fun updateShowCalories(show: Boolean) =
    repository.updateShowCalories(show)

  override suspend fun updateTcaFriendlyMode(enabled: Boolean) =
    repository.updateTcaFriendlyMode(enabled)

  override suspend fun updateNotifications(enabled: Boolean) =
    repository.updateNotifications(enabled)

  override suspend fun completeOnboarding() =
    repository.completeOnboarding()
}