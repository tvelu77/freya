package io.tvelu77.freya.database.repositories

import io.tvelu77.freya.database.config.UserProfileDataStore
import io.tvelu77.freya.database.config.UserProfileDataStore.Companion.KEY_BASE_CALORIES
import io.tvelu77.freya.database.config.UserProfileDataStore.Companion.KEY_CYCLE_LENGTH
import io.tvelu77.freya.database.config.UserProfileDataStore.Companion.KEY_NOTIFICATIONS
import io.tvelu77.freya.database.config.UserProfileDataStore.Companion.KEY_ONBOARDING_COMPLETED
import io.tvelu77.freya.database.config.UserProfileDataStore.Companion.KEY_TCA_FRIENDLY
import io.tvelu77.freya.domain.models.UserProfile
import io.tvelu77.freya.domain.ports.spi.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataStoreUserProfileRepository @Inject constructor(
  private val dataStore: UserProfileDataStore
) : UserProfileRepository {

  override fun getProfile(): Flow<UserProfile> = dataStore.userProfile

  override suspend fun saveProfile(profile: UserProfile) {
    dataStore.update {
      this[UserProfileDataStore.KEY_FIRST_NAME] = profile.firstName
      this[KEY_BASE_CALORIES] = profile.baseCalories
      this[KEY_CYCLE_LENGTH] = profile.cycleLengthDays
      this[KEY_TCA_FRIENDLY] = profile.tcaFriendlyMode
      this[KEY_NOTIFICATIONS] = profile.notificationsEnabled
      this[KEY_ONBOARDING_COMPLETED] = profile.onboardingCompleted
    }
  }

  override suspend fun updateFirstName(name: String) =
    dataStore.update { this[UserProfileDataStore.KEY_FIRST_NAME] = name }

  override suspend fun updateBaseCalories(calories: Int) =
    dataStore.update { this[KEY_BASE_CALORIES] = calories }

  override suspend fun updateCycleLength(days: Int) =
    dataStore.update { this[KEY_CYCLE_LENGTH] = days }

  override suspend fun updateTcaFriendlyMode(enabled: Boolean) =
    dataStore.update { this[KEY_TCA_FRIENDLY] = enabled }

  override suspend fun updateNotifications(enabled: Boolean) =
    dataStore.update { this[KEY_NOTIFICATIONS] = enabled }

  override suspend fun completeOnboarding() =
    dataStore.update { this[KEY_ONBOARDING_COMPLETED] = true }
}