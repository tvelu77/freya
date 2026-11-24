package io.tvelu77.freya.domain.ports.api

import io.tvelu77.freya.domain.models.Goal

interface UpdateUserProfileUseCase {

  suspend fun updateFirstName(name: String)
  suspend fun updateBaseCalories(calories: Int)
  suspend fun updateGoal(goal: Goal)
  suspend fun updateWeight(weightKg: Float?)
  suspend fun updateCycleLength(days: Int)
  suspend fun updateTcaFriendlyMode(enabled: Boolean)
  suspend fun updateNotifications(enabled: Boolean)
  suspend fun completeOnboarding()

}