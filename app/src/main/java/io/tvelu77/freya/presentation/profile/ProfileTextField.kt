package io.tvelu77.freya.presentation.profile

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileTextField(
  label: String,
  value: String,
  onValueChange: (String) -> Unit,
  placeholder: String,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  keyboardActions: KeyboardActions = KeyboardActions.Default,
  supportingText: String? = null
) {
  OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    modifier = Modifier.fillMaxWidth(),
    label = { Text(label) },
    placeholder = {
      Text(
        placeholder,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
      )
    },
    supportingText = supportingText?.let {
      { Text(it, style = MaterialTheme.typography.labelSmall) }
    },
    keyboardOptions = keyboardOptions,
    keyboardActions = keyboardActions,
    singleLine = true,
    shape = RoundedCornerShape(12.dp),
    colors = OutlinedTextFieldDefaults.colors(
      focusedBorderColor = MaterialTheme.colorScheme.primary,
      unfocusedBorderColor = MaterialTheme.colorScheme.outline
    )
  )
}