package io.tvelu77.freya.database.repositories

import io.tvelu77.freya.database.config.UserProfileDataStore
import io.tvelu77.freya.database.config.UserProfileDataStore.Companion.KEY_AGE_YEARS
import io.tvelu77.freya.database.config.UserProfileDataStore.Companion.KEY_BASE_CALORIES
import io.tvelu77.freya.database.config.UserProfileDataStore.Companion.KEY_CYCLE_LENGTH
import io.tvelu77.freya.database.config.UserProfileDataStore.Companion.KEY_HEIGHT_CM
import io.tvelu77.freya.database.config.UserProfileDataStore.Companion.KEY_HYDRATION_REMINDERS
import io.tvelu77.freya.database.config.UserProfileDataStore.Companion.KEY_GOAL
import io.tvelu77.freya.database.config.UserProfileDataStore.Companion.KEY_WEIGHT_KG
import io.tvelu77.freya.database.config.UserProfileDataStore.Companion.KEY_NOTIFICATIONS
import io.tvelu77.freya.database.config.UserProfileDataStore.Companion.KEY_ONBOARDING_COMPLETED
import io.tvelu77.freya.database.config.UserProfileDataStore.Companion.KEY_SHOW_SCORE_NUMBER
import io.tvelu77.freya.database.config.UserProfileDataStore.Companion.KEY_TCA_FRIENDLY
import io.tvelu77.freya.domain.models.Goal
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
      this[KEY_GOAL] = profile.goal.name
      profile.weightKg?.let { this[KEY_WEIGHT_KG] = it.toDouble() }
      profile.ageYears?.let { this[KEY_AGE_YEARS] = it }
      profile.heightCm?.let { this[KEY_HEIGHT_CM] = it }
      this[KEY_CYCLE_LENGTH] = profile.cycleLengthDays
      this[KEY_TCA_FRIENDLY] = profile.tcaFriendlyMode
      this[KEY_NOTIFICATIONS] = profile.notificationsEnabled
      this[KEY_HYDRATION_REMINDERS] = profile.hydrationRemindersEnabled
      this[KEY_SHOW_SCORE_NUMBER] = profile.showHealthScoreNumber
      this[KEY_ONBOARDING_COMPLETED] = profile.onboardingCompleted
    }
  }

  override suspend fun updateFirstName(name: String) =
    dataStore.update { this[UserProfileDataStore.KEY_FIRST_NAME] = name }

  override suspend fun updateBaseCalories(calories: Int) =
    dataStore.update { this[KEY_BASE_CALORIES] = calories }

  override suspend fun updateGoal(goal: Goal) =
    dataStore.update { this[KEY_GOAL] = goal.name }

  override suspend fun updateWeight(weightKg: Float?) =
    dataStore.update {
      if (weightKg == null) remove(KEY_WEIGHT_KG) else this[KEY_WEIGHT_KG] = weightKg.toDouble()
    }

  override suspend fun updateAgeYears(ageYears: Int?) =
    dataStore.update {
      if (ageYears == null) remove(KEY_AGE_YEARS) else this[KEY_AGE_YEARS] = ageYears
    }

  override suspend fun updateHeightCm(heightCm: Int?) =
    dataStore.update {
      if (heightCm == null) remove(KEY_HEIGHT_CM) else this[KEY_HEIGHT_CM] = heightCm
    }

  override suspend fun updateCycleLength(days: Int) =
    dataStore.update { this[KEY_CYCLE_LENGTH] = days }

  override suspend fun updateTcaFriendlyMode(enabled: Boolean) =
    dataStore.update { this[KEY_TCA_FRIENDLY] = enabled }

  override suspend fun updateNotifications(enabled: Boolean) =
    dataStore.update { this[KEY_NOTIFICATIONS] = enabled }

  override suspend fun updateHydrationReminders(enabled: Boolean) =
    dataStore.update { this[KEY_HYDRATION_REMINDERS] = enabled }

  override suspend fun updateShowScoreNumber(enabled: Boolean) =
    dataStore.update { this[KEY_SHOW_SCORE_NUMBER] = enabled }

  override suspend fun completeOnboarding() =
    dataStore.update { this[KEY_ONBOARDING_COMPLETED] = true }
}