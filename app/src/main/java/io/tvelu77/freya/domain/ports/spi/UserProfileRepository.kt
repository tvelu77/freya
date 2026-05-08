package io.tvelu77.freya.domain.ports.spi

import io.tvelu77.freya.domain.models.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
  fun getProfile(): Flow<UserProfile>
  suspend fun saveProfile(profile: UserProfile)
  suspend fun updateFirstName(name: String)
  suspend fun updateBaseCalories(calories: Int)
  suspend fun updateCycleLength(days: Int)
  suspend fun updateTcaFriendlyMode(enabled: Boolean)
  suspend fun updateNotifications(enabled: Boolean)
  suspend fun completeOnboarding()
}