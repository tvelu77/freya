package io.tvelu77.freya.domain.ports.spi

import io.tvelu77.freya.domain.models.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeUserProfileRepository : UserProfileRepository {

  var profile = UserProfile()

  override fun getProfile(): Flow<UserProfile> = flowOf(profile)

  override suspend fun saveProfile(profile: UserProfile) {
    this.profile = profile
  }

  override suspend fun updateFirstName(name: String) {
    profile = profile.copy(firstName = name)
  }

  override suspend fun updateBaseCalories(calories: Int) {
    profile = profile.copy(baseCalories = calories)
  }

  override suspend fun updateCycleLength(days: Int) {
    profile = profile.copy(cycleLengthDays = days)
  }

  override suspend fun updateTcaFriendlyMode(enabled: Boolean) {
    profile = profile.copy(tcaFriendlyMode = enabled)
  }

  override suspend fun updateNotifications(enabled: Boolean) {
    profile = profile.copy(notificationsEnabled = enabled)
  }

  override suspend fun completeOnboarding() {
    profile = profile.copy(onboardingCompleted = true)
  }
}
