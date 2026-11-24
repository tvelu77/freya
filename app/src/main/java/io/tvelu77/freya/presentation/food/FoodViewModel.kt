package io.tvelu77.freya.presentation.food

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.tvelu77.freya.domain.models.FoodEntry
import io.tvelu77.freya.domain.models.FoodItem
import io.tvelu77.freya.domain.models.MealType
import io.tvelu77.freya.domain.ports.api.GetDailyNutritionUseCase
import io.tvelu77.freya.domain.ports.api.GetNutritionAdviceUseCase
import io.tvelu77.freya.domain.ports.api.GetUserProfileUseCase
import io.tvelu77.freya.domain.ports.api.LogFoodUseCase
import io.tvelu77.freya.domain.ports.api.SearchFoodItemsUseCase
import io.tvelu77.freya.domain.ports.api.TrackCycleUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.collections.emptyList

@OptIn(FlowPreview::class)
@HiltViewModel
class FoodViewModel @Inject constructor(
  private val logFoodUseCase: LogFoodUseCase,
  private val getDailyNutritionUseCase: GetDailyNutritionUseCase,
  private val searchFoodItemsUseCase: SearchFoodItemsUseCase,
  private val getNutritionAdviceUseCase: GetNutritionAdviceUseCase,
  private val getUserProfileUseCase: GetUserProfileUseCase,
  private val trackCycleUseCase: TrackCycleUseCase
) : ViewModel() {

  private val _uiState = MutableStateFlow(FoodUiState())
  val uiState: StateFlow<FoodUiState> = _uiState.asStateFlow()

  private val today = LocalDate.now()

  init {
    loadFoodData()
    observeSearch()
    loadQuickFoods()
  }

  private fun loadQuickFoods() {
    viewModelScope.launch {
      _uiState.update { it.copy(quickFoods = searchFoodItemsUseCase.quickFoods()) }
    }
  }

  private fun loadFoodData() {
      combine(
        logFoodUseCase.getFoodForDay(today),
        getDailyNutritionUseCase.getForDay(today),
        getNutritionAdviceUseCase.getAdviceForToday(today),
        getUserProfileUseCase.execute(),
        trackCycleUseCase.getCurrentPhase()
      ) { entries, nutrition, advice, profile, phase ->
        _uiState.update {
          it.copy(
            isLoading = false,
            todayEntries = entries,
            nutrition = nutrition,
            advice = advice,
            energyTarget = getNutritionAdviceUseCase
              .getEnergyTarget(profile.baseCalories, profile.goal, phase),
            showCalories = !profile.tcaFriendlyMode,
            hasActiveCycle = phase != null
          )
        }
      }.launchIn(viewModelScope)
    }

  private fun observeSearch() {
    viewModelScope.launch {
      _uiState
        .map { it.searchQuery }
        .distinctUntilChanged()
        .debounce(200)
        .collect { query ->
          val results = searchFoodItemsUseCase.search(query)
          _uiState.update { it.copy(searchResults = results) }
        }
    }
  }

  fun onSearchQueryChanged(query: String) {
    _uiState.update { it.copy(searchQuery = query) }
  }

  fun onMealTypeSelected(mealType: MealType) {
    _uiState.update { it.copy(selectedMealType = mealType) }
  }

  fun onShowAddSheet(mealType: MealType) {
    _uiState.update { it.copy(showAddSheet = true, selectedMealType = mealType, searchQuery = "", searchResults = emptyList()) }
  }

  fun onDismissSheet() {
    _uiState.update { it.copy(showAddSheet = false, searchQuery = "", searchResults = emptyList()) }
  }

  fun onToggleCaloriesVisibility() {
    _uiState.update { it.copy(showCalories = !it.showCalories) }
  }

  fun logFood(food: FoodItem, quantity: Float, mealType: MealType) {
    viewModelScope.launch {
      logFoodUseCase.logFood(food = food, quantityG = quantity, mealType = mealType)
      _uiState.update { it.copy(showAddSheet = false, searchQuery = "") }
    }
  }

  private var lastDeletedFood: FoodEntry? = null

  fun deleteFood(id: Long) {
    viewModelScope.launch {
      lastDeletedFood = _uiState.value.todayEntries.firstOrNull { it.id == id }
      logFoodUseCase.deleteFood(id)
    }
  }

  fun undoDeleteFood() {
    viewModelScope.launch {
      lastDeletedFood?.let { logFoodUseCase.updateFoodEntry(it) }
      lastDeletedFood = null
    }
  }

  fun updateFoodQuantity(id: Long, quantityG: Float) {
    viewModelScope.launch { logFoodUseCase.updateFoodQuantity(id, quantityG) }
  }
}