package io.tvelu77.freya.domain.ports.api

interface UpdateUserProfileUseCase {

  suspend fun updateFirstName(name: String)
  suspend fun updateBaseCalories(calories: Int)
  suspend fun updateCycleLength(days: Int)
  suspend fun updateShowCalories(show: Boolean)
  suspend fun updateTcaFriendlyMode(enabled: Boolean)
  suspend fun updateNotifications(enabled: Boolean)
  suspend fun completeOnboarding()

}