package io.tvelu77.freya.presentation.food

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.tvelu77.freya.R
import io.tvelu77.freya.domain.models.FoodItem
import io.tvelu77.freya.domain.models.MealType
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodSheet(
  mealType: MealType,
  searchQuery: String,
  searchResults: List<FoodItem>,
  quickFoods: List<FoodItem>,
  onQueryChanged: (String) -> Unit,
  onFoodSelected: (FoodItem, Float) -> Unit,
  onDismiss: () -> Unit
) {
  var selectedFood by remember { mutableStateOf<FoodItem?>(null) }
  var quantity by remember { mutableStateOf(FoodItem.DEFAULT_SERVING_G.toInt().toString()) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    containerColor = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .padding(bottom = 32.dp)
    ) {
      Text(
        text = stringResource(
          R.string.food_add_to_meal,
          mealType.displayName.lowercase(Locale.FRENCH)
        ),
        style = MaterialTheme.typography.titleLarge
      )

      Spacer(Modifier.height(16.dp))

      OutlinedTextField(
        value = searchQuery,
        onValueChange = onQueryChanged,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
          Text(
            text = stringResource(R.string.food_search_placeholder),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
          )
        },
        leadingIcon = {
          Icon(Icons.Rounded.Search, contentDescription = null)
        },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { onQueryChanged("") }) {
              Icon(
                Icons.Rounded.Clear,
                contentDescription = stringResource(R.string.food_clear_cd)
              )
            }
          }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = MaterialTheme.colorScheme.primary,
          unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
      )

      Spacer(Modifier.height(12.dp))

      AnimatedVisibility(
        visible = selectedFood != null,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
      ) {
        selectedFood?.let { food ->
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = MaterialTheme.shapes.large
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(food.name, style = MaterialTheme.typography.titleMedium)
                IconButton(
                  onClick = { selectedFood = null },
                  modifier = Modifier.size(24.dp)
                ) {
                  Icon(
                    Icons.Rounded.Close,
                    contentDescription = stringResource(R.string.food_deselect_cd),
                    modifier = Modifier.size(16.dp)
                  )
                }
              }

              Spacer(Modifier.height(10.dp))

              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                OutlinedTextField(
                  value = quantity,
                  onValueChange = { quantity = it.filter { c -> c.isDigit() } },
                  modifier = Modifier.width(100.dp),
                  label = { Text(stringResource(R.string.food_quantity_label)) },
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                  singleLine = true,
                  shape = RoundedCornerShape(12.dp)
                )

                val q = quantity.toFloatOrNull() ?: FoodItem.DEFAULT_SERVING_G
                Column {
                  Text(
                    text = stringResource(R.string.food_kcal, food.caloriesFor(q)),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                  )
                  Text(
                    text = stringResource(
                      R.string.food_macros_short,
                      food.proteinsFor(q).toInt(),
                      food.carbsFor(q).toInt(),
                      food.fatsFor(q).toInt()
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                  )
                }
              }

              Spacer(Modifier.height(14.dp))

              Button(
                onClick = {
                  val q = quantity.toFloatOrNull() ?: FoodItem.DEFAULT_SERVING_G
                  onFoodSelected(food, q)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
              ) {
                Icon(Icons.Rounded.Add, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text(stringResource(R.string.food_add_button))
              }
            }
          }

          Spacer(Modifier.height(12.dp))
        }
      }

      if (searchResults.isNotEmpty()) {
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 280.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          items(searchResults, key = { it.id }) { food ->
            FoodResultRow(
              food = food,
              onClick = {
                selectedFood = food
                quantity = food.defaultServingG.toInt().toString()
              }
            )
          }
        }
      } else if (searchQuery.length >= 2) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text("🌱", style = MaterialTheme.typography.headlineMedium)
          Text(
            text = stringResource(R.string.food_no_results, searchQuery),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
          )
        }
      } else if (searchQuery.isEmpty()) {
        Text(
          text = stringResource(R.string.food_quick_suggestions),
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(6.dp))
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
          quickFoods.forEach { food ->
            SuggestionChip(
              onClick = {
                selectedFood = food
                quantity = food.defaultServingG.toInt().toString()
              },
              label = {
                Text(food.name, style = MaterialTheme.typography.labelSmall)
              }
            )
          }
        }
      }
    }
  }
}