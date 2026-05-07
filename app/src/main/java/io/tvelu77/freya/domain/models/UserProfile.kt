package io.tvelu77.freya.domain.models

data class UserProfile(
  val firstName: String = "",
  val baseCalories: Int = 2000,
  val cycleLengthDays: Int = 28,
  val showCalories: Boolean = true,
  val tcaFriendlyMode: Boolean = false,
  val notificationsEnabled: Boolean = true,
  val onboardingCompleted: Boolean = false
)
