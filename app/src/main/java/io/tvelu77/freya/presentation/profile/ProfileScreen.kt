package io.tvelu77.freya.presentation.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.tvelu77.freya.ui.components.ToggleRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
  viewModel: ProfileViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val focusManager = LocalFocusManager.current

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Mon profil", style = MaterialTheme.typography.titleLarge) },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    }
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

      ProfileAvatarSection(firstName = uiState.firstNameInput)

      ProfileSection(title = "Informations personnelles") {
        ProfileTextField(
          label = "Prénom",
          value = uiState.firstNameInput,
          onValueChange = viewModel::onFirstNameChanged,
          placeholder = "Comment tu t'appelles ?",
          keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Next
          )
        )

        ProfileTextField(
          label = "Objectif calorique quotidien (kcal)",
          value = uiState.caloriesInput,
          onValueChange = viewModel::onCaloriesChanged,
          placeholder = "2000",
          keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
          ),
          supportingText = "Minimum recommandé : 1200 kcal"
        )

        ProfileTextField(
          label = "Durée habituelle de mon cycle (jours)",
          value = uiState.cycleLengthInput,
          onValueChange = viewModel::onCycleLengthChanged,
          placeholder = "28",
          keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
          ),
          supportingText = "Entre 21 et 45 jours",
          keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() }
          )
        )
      }

      ProfileSection(title = "Affichage & confidentialité") {

        ToggleRow(
          title = "Mode bienveillant",
          subtitle = "Masque les kcals — focus sur le ressenti",
          checked = uiState.profile.tcaFriendlyMode,
          onCheckedChange = viewModel::onTcaFriendlyToggled,
          icon = Icons.Rounded.Favorite
        )
      }

      ProfileSection(title = "Notifications") {
        ToggleRow(
          title = "Rappels & conseils",
          subtitle = "Changements de phase, hydratation, conseils doux",
          checked = uiState.profile.notificationsEnabled,
          onCheckedChange = viewModel::onNotificationsToggled,
          icon = Icons.Rounded.Notifications
        )
      }

      Spacer(Modifier.height(4.dp))

      AnimatedVisibility(
        visible = uiState.savedFeedback,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
      ) {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
          ),
          shape = MaterialTheme.shapes.large
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              Icons.Rounded.CheckCircle,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.tertiary
            )
            Text(
              "Profil sauvegardé !",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onTertiaryContainer
            )
          }
        }
      }

      Button(
        onClick = {
          focusManager.clearFocus()
          viewModel.onSaveProfile()
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp),
        shape = RoundedCornerShape(16.dp),
        enabled = !uiState.isSaving
      ) {
        if (uiState.isSaving) {
          CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            color = MaterialTheme.colorScheme.onPrimary,
            strokeWidth = 2.dp
          )
        } else {
          Icon(Icons.Rounded.Save, contentDescription = null)
          Spacer(Modifier.width(8.dp))
          Text("Sauvegarder", style = MaterialTheme.typography.titleMedium)
        }
      }

      Spacer(Modifier.height(8.dp))
    }
  }
}