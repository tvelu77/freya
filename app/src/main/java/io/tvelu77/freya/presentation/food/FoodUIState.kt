package io.tvelu77.freya.presentation.food

import io.tvelu77.freya.domain.models.DailyNutrition
import io.tvelu77.freya.domain.models.FoodEntry
import io.tvelu77.freya.domain.models.FoodItem
import io.tvelu77.freya.domain.models.MealType
import io.tvelu77.freya.domain.models.NutritionAdvice
import java.time.LocalDate

data class FoodUiState(
  val isLoading: Boolean = true,
  val todayEntries: List<FoodEntry> = emptyList(),
  val nutrition: DailyNutrition = DailyNutrition.empty(LocalDate.now()),
  val caloriesTarget: Int = 2000,
  val advice: NutritionAdvice? = null,
  val showAddSheet: Boolean = false,
  val selectedMealType: MealType = MealType.LUNCH,
  val searchQuery: String = "",
  val searchResults: List<FoodItem> = emptyList(),
  val quickFoods: List<FoodItem> = emptyList(),
  val showCalories: Boolean = true,
  val error: String? = null,
  val hasActiveCycle: Boolean = false
)
