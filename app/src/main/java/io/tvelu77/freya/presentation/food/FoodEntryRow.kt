package io.tvelu77.freya.presentation.food

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.tvelu77.freya.R
import io.tvelu77.freya.domain.models.FoodEntry

@Composable
fun FoodEntryRow(
  entry: FoodEntry,
  showCalories: Boolean,
  onEdit: (Long, Float) -> Unit,
  onDelete: () -> Unit
) {
  var showDeleteConfirm by remember { mutableStateOf(false) }
  var showEditDialog by remember { mutableStateOf(false) }

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { showEditDialog = true }
      .padding(horizontal = 16.dp, vertical = 10.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(entry.foodName, style = MaterialTheme.typography.bodyMedium)
      Text(
        text = stringResource(R.string.food_quantity_g, entry.quantity.toInt()),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      if (showCalories) {
        Text(
          text = stringResource(
            R.string.food_macros_short,
            entry.proteins.toInt(),
            entry.carbs.toInt(),
            entry.fats.toInt()
          ),
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      if (showCalories) {
        Text(
          text = stringResource(R.string.food_kcal, entry.calories),
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
      IconButton(
        onClick = { showDeleteConfirm = true },
        modifier = Modifier.size(28.dp)
      ) {
        Icon(
          Icons.Rounded.Delete,
          contentDescription = stringResource(R.string.action_delete),
          modifier = Modifier.size(16.dp),
          tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
      }
    }

    if (showEditDialog) {
      var quantity by remember { mutableStateOf(entry.quantity.toInt().toString()) }
      AlertDialog(
        onDismissRequest = { showEditDialog = false },
        title = { Text(stringResource(R.string.food_edit_title)) },
        text = {
          OutlinedTextField(
            value = quantity,
            onValueChange = {
              quantity = it.filter { c -> c.isDigit() || c == '.' || c == ',' }
            },
            label = { Text(stringResource(R.string.food_quantity_label)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
          )
        },
        confirmButton = {
          TextButton(
            onClick = {
              onEdit(entry.id, quantity.replace(',', '.').toFloatOrNull() ?: entry.quantity)
              showEditDialog = false
            },
            enabled = (quantity.replace(',', '.').toFloatOrNull() ?: 0f) > 0f
          ) {
            Text(stringResource(R.string.dialog_save))
          }
        },
        dismissButton = {
          TextButton(onClick = { showEditDialog = false }) {
            Text(stringResource(R.string.action_cancel))
          }
        }
      )
    }

    if (showDeleteConfirm) {
      AlertDialog(
        onDismissRequest = { showDeleteConfirm = false },
        title = { Text(stringResource(R.string.food_delete_title)) },
        confirmButton = {
          TextButton(onClick = { onDelete(); showDeleteConfirm = false }) {
            Text(
              text = stringResource(R.string.action_delete),
              color = MaterialTheme.colorScheme.error
            )
          }
        },
        dismissButton = {
          TextButton(onClick = { showDeleteConfirm = false }) {
            Text(stringResource(R.string.action_cancel))
          }
        }
      )
    }
  }
}