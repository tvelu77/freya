package io.tvelu77.freya.database.config

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.tvelu77.freya.domain.models.UserProfile
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences>
        by preferencesDataStore(name = "user_profile")

@Singleton
class UserProfileDataStore @Inject constructor(
  private val context: Context
) {
  companion object {
    val KEY_FIRST_NAME  = stringPreferencesKey("first_name")
    val KEY_BASE_CALORIES = intPreferencesKey("base_calories")
    val KEY_CYCLE_LENGTH = intPreferencesKey("cycle_length")
    val KEY_SHOW_CALORIES = booleanPreferencesKey("show_calories")
    val KEY_TCA_FRIENDLY = booleanPreferencesKey("tca_friendly")
    val KEY_NOTIFICATIONS = booleanPreferencesKey("notifications")
    val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
  }

  val userProfile: Flow<UserProfile> = context.dataStore.data
    .catch { emit(emptyPreferences()) }
    .map { prefs ->
      UserProfile(
        firstName            = prefs[KEY_FIRST_NAME] ?: "",
        baseCalories         = prefs[KEY_BASE_CALORIES] ?: 2000,
        cycleLengthDays      = prefs[KEY_CYCLE_LENGTH] ?: 28,
        showCalories         = prefs[KEY_SHOW_CALORIES] ?: true,
        tcaFriendlyMode      = prefs[KEY_TCA_FRIENDLY] ?: false,
        notificationsEnabled = prefs[KEY_NOTIFICATIONS] ?: true,
        onboardingCompleted  = prefs[KEY_ONBOARDING_COMPLETED] ?: false
      )
    }

  suspend fun update(block: suspend MutablePreferences.() -> Unit) {
    context.dataStore.edit { block(it) }
  }
}