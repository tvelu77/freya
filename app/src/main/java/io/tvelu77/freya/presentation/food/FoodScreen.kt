package io.tvelu77.freya.presentation.food

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.tvelu77.freya.domain.models.MealType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodScreen(
  onNavigateBack: () -> Unit,
  viewModel: FoodViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Mes repas", style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "Retour")
          }
        },
        actions = {
          IconButton(onClick = viewModel::onToggleCaloriesVisibility) {
            Icon(
              imageVector = if (uiState.showCalories)
                Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
              contentDescription = if (uiState.showCalories)
                "Masquer les calories" else "Afficher les calories",
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
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding),
      contentPadding = PaddingValues(bottom = 24.dp)
    ) {

      item {
        NutritionSummaryCard(
          totalCalories = uiState.totalCalories,
          caloriesTarget = uiState.caloriesTarget,
          proteins = uiState.proteins,
          carbs = uiState.carbs,
          fats = uiState.fats,
          showCalories = uiState.showCalories,
          adviceMessage = uiState.advice?.tipMessage
        )
      }

      MealType.entries.forEach { mealType ->
        item {
          MealSection(
            mealType = mealType,
            entries = uiState.todayEntries.filter { it.mealType == mealType },
            showCalories = uiState.showCalories,
            onAddFood = { viewModel.onShowAddSheet(mealType) },
            onDeleteFood = viewModel::deleteFood
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
      onQueryChanged = viewModel::onSearchQueryChanged,
      onFoodSelected = { food, quantity ->
        viewModel.logFood(food, quantity, uiState.selectedMealType)
      },
      onDismiss = viewModel::onDismissSheet
    )
  }
}