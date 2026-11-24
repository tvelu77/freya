package io.tvelu77.freya.presentation.activity

import io.tvelu77.freya.domain.models.ActivityEntry

data class ActivityUiState(
  val isLoading: Boolean = true,
  val activities: List<ActivityEntry> = emptyList(),
  val totalBurned: Int = 0,
  val showAddDialog: Boolean = false,
  val showCalories: Boolean = true,
  val error: String? = null
)
