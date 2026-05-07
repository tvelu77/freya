package io.tvelu77.freya.presentation.profile

import io.tvelu77.freya.domain.models.UserProfile

data class ProfileUiState(
  val isLoading: Boolean = true,
  val profile: UserProfile = UserProfile(),
  val firstNameInput: String = "",
  val caloriesInput: String = "2000",
  val cycleLengthInput: String = "28",
  val isSaving: Boolean = false,
  val savedFeedback: Boolean = false
)
