package io.tvelu77.freya.presentation.tca

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.tvelu77.freya.R

private data class ResourceItem(
  @StringRes val titleRes: Int,
  @StringRes val descRes: Int
)

private val professionals = listOf(
  ResourceItem(R.string.tca_res_nutritionist_title, R.string.tca_res_nutritionist_desc),
  ResourceItem(R.string.tca_res_psy_title, R.string.tca_res_psy_desc)
)

private val helplines = listOf(
  ResourceItem(R.string.tca_res_3114_title, R.string.tca_res_3114_desc),
  ResourceItem(R.string.tca_res_ffab_title, R.string.tca_res_ffab_desc),
  ResourceItem(R.string.tca_res_emergency_title, R.string.tca_res_emergency_desc)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourcesScreen() {
  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(stringResource(R.string.tca_resources_title)) },
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
        .padding(20.dp)
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Text("💛", style = MaterialTheme.typography.headlineMedium)
      Text(
        text = stringResource(R.string.tca_resources_intro),
        style = MaterialTheme.typography.bodyLarge
      )

      ResourceSection(titleRes = R.string.tca_resources_section_pro, items = professionals)
      ResourceSection(titleRes = R.string.tca_resources_section_help, items = helplines)

      Text(
        text = stringResource(R.string.tca_resources_footer),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

@Composable
private fun ResourceSection(
  @StringRes titleRes: Int,
  items: List<ResourceItem>
) {
  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    Text(
      text = stringResource(titleRes),
      style = MaterialTheme.typography.titleMedium
    )
    items.forEach { item ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = MaterialTheme.shapes.large
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = stringResource(item.titleRes),
            style = MaterialTheme.typography.titleSmall
          )
          Spacer(Modifier.height(4.dp))
          Text(
            text = stringResource(item.descRes),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
          )
        }
      }
    }
  }
}
