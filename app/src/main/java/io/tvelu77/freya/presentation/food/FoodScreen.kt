package io.tvelu77.freya.presentation.food

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import io.tvelu77.freya.R
import io.tvelu77.freya.domain.models.MealType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodScreen(
  viewModel: FoodViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()
  val context = LocalContext.current

  Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = stringResource(R.string.food_title),
            style = MaterialTheme.typography.titleLarge
          )
        },
        actions = {
          IconButton(onClick = viewModel::onToggleCaloriesVisibility) {
            Icon(
              imageVector = if (uiState.showCalories)
                Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
              contentDescription = if (uiState.showCalories)
                stringResource(R.string.food_hide_calories_cd)
              else
                stringResource(R.string.food_show_calories_cd),
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    }
  ) { padding ->
    if (!uiState.hasActiveCycle) {
      NoCycleOnFoodScreen(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding)
      )
      return@Scaffold
    }
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding),
      contentPadding = PaddingValues(bottom = 24.dp)
    ) {

      item {
        NutritionSummaryCard(
          nutrition = uiState.nutrition,
          energyTarget = uiState.energyTarget,
          showCalories = uiState.showCalories,
          advice = uiState.advice
        )
      }

      MealType.entries.forEach { mealType ->
        item {
          MealSection(
            mealType = mealType,
            entries = uiState.todayEntries.filter { it.mealType == mealType },
            mealNutrition = uiState.nutrition.meals[mealType],
            showCalories = uiState.showCalories,
            onAddFood = { viewModel.onShowAddSheet(mealType) },
            onDeleteFood = { id ->
              viewModel.deleteFood(id)
              scope.launch {
                val result = snackbarHostState.showSnackbar(
                  message = context.getString(R.string.food_deleted),
                  actionLabel = context.getString(R.string.action_cancel)
                )
                if (result == SnackbarResult.ActionPerformed) {
                  viewModel.undoDeleteFood()
                }
              }
            },
            onEditFood = viewModel::updateFoodQuantity
          )
        }
      }
    }
  }

  if (uiState.showAddSheet) {
    AddFoodSheet(
      mealType = uiState.selectedMealType,
      searchQuery = uiState.searchQuery,
      searchResults = uiState.searchResults,
      quickFoods = uiState.quickFoods,
      showCalories = uiState.showCalories,
      onQueryChanged = viewModel::onSearchQueryChanged,
      onFoodSelected = { food, quantity ->
        viewModel.logFood(food, quantity, uiState.selectedMealType)
      },
      onDismiss = viewModel::onDismissSheet
    )
  }
}