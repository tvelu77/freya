package io.tvelu77.freya.presentation.food

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.tvelu77.freya.domain.models.MealType
import io.tvelu77.freya.domain.models.QuickFood
import io.tvelu77.freya.domain.ports.api.GetNutritionAdviceUseCase
import io.tvelu77.freya.domain.ports.api.LogFoodUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.collections.emptyList

@OptIn(FlowPreview::class)
@HiltViewModel
class FoodViewModel @Inject constructor(
  private val logFoodUseCase: LogFoodUseCase,
  private val getNutritionAdviceUseCase: GetNutritionAdviceUseCase
) : ViewModel() {

  private val _uiState = MutableStateFlow(FoodUiState())
  val uiState: StateFlow<FoodUiState> = _uiState.asStateFlow()

  private val today = LocalDate.now()

  init {
    loadFoodData()
    observeSearch()
  }

  private fun loadFoodData() {
    viewModelScope.launch {
      combine(
        logFoodUseCase.getFoodForDay(today),
        getNutritionAdviceUseCase.getAdviceForToday(today)
      ) { entries, advice ->
        Pair(entries, advice)
      }.catch { e ->
        _uiState.update { it.copy(isLoading = false, error = e.message) }
      }.collect { (entries, advice) ->
        val totalKcal = entries.sumOf { it.calories }
        _uiState.update {
          it.copy(
            isLoading = false,
            todayEntries = entries,
            totalCalories = totalKcal,
            proteins = entries.sumOf { e -> e.proteins.toDouble() }.toFloat(),
            carbs = entries.sumOf { e -> e.carbs.toDouble() }.toFloat(),
            fats = entries.sumOf { e -> e.fats.toDouble() }.toFloat(),
            advice = advice
          )
        }
      }
    }
  }

  private fun observeSearch() {
    viewModelScope.launch {
      _uiState
        .map { it.searchQuery }
        .distinctUntilChanged()
        .debounce(200)
        .collect { query ->
          val results = if (query.length >= 2) searchFoods(query) else emptyList()
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

  fun logFood(food: QuickFood, quantity: Float, mealType: MealType) {
    viewModelScope.launch {
      val ratio = quantity / food.defaultQuantity
      logFoodUseCase.logFood(
        foodName = food.name,
        quantity = quantity,
        calories = (food.calories * ratio).toInt(),
        proteins = food.proteins * ratio,
        carbs = food.carbs * ratio,
        fats = food.fats * ratio,
        mealType = mealType
      )
      _uiState.update { it.copy(showAddSheet = false, searchQuery = "") }
    }
  }

  fun deleteFood(id: Long) {
    viewModelScope.launch { logFoodUseCase.deleteFood(id) }
  }

  private fun searchFoods(query: String): List<QuickFood> {
    return FOOD_DATABASE.filter {
      it.name.contains(query, ignoreCase = true)
    }.take(8)
  }

  companion object {
    val FOOD_DATABASE = listOf(
      QuickFood("Poulet rôti",       165, 31f, 0f,   3.6f),
      QuickFood("Riz blanc cuit",    130, 2.7f, 28f, 0.3f),
      QuickFood("Riz complet cuit",  112, 2.6f, 23f, 0.9f),
      QuickFood("Pâtes cuites",      158, 5.8f, 31f, 0.9f),
      QuickFood("Saumon grillé",     208, 20f,  0f,  13f),
      QuickFood("Œuf entier",        155, 13f,  1.1f,11f,  defaultQuantity = 50f),
      QuickFood("Pain complet",      247, 9f,   41f, 3.4f),
      QuickFood("Avocat",            160, 2f,   9f,  15f),
      QuickFood("Banane",             89, 1.1f, 23f, 0.3f),
      QuickFood("Pomme",              52, 0.3f, 14f, 0.2f),
      QuickFood("Épinards cuits",     23, 2.9f, 3.8f,0.4f),
      QuickFood("Brocoli cuit",       35, 2.4f, 7f,  0.4f),
      QuickFood("Patate douce cuite", 86, 1.6f, 20f, 0.1f),
      QuickFood("Lentilles cuites",  116, 9f,   20f, 0.4f),
      QuickFood("Chocolat noir 70%", 598, 7.8f, 46f, 43f),
      QuickFood("Yaourt nature",      61, 3.5f, 4.7f,3.3f),
      QuickFood("Lait demi-écrémé",   46, 3.2f, 4.8f,1.5f),
      QuickFood("Fromage blanc 0%",   45, 8f,   4f,  0.2f),
      QuickFood("Amandes",           579, 21f,  22f, 50f),
      QuickFood("Noix",              654, 15f,  14f, 65f),
      QuickFood("Quinoa cuit",       120, 4.4f, 22f, 1.9f),
      QuickFood("Myrtilles",          57, 0.7f, 14f, 0.3f),
      QuickFood("Tomate",             18, 0.9f, 3.9f,0.2f),
      QuickFood("Concombre",          16, 0.7f, 3.6f,0.1f),
      QuickFood("Carotte",            41, 0.9f, 10f, 0.2f),
      QuickFood("Thon en boîte",     116, 26f,  0f,  1f),
      QuickFood("Huile d'olive",     884, 0f,   0f,  100f, defaultQuantity = 10f),
      QuickFood("Beurre",            717, 0.8f, 0.6f,81f,  defaultQuantity = 10f),
    )
  }
}