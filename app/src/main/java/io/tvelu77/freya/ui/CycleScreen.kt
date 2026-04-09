package io.tvelu77.freya.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import io.tvelu77.freya.viewModels.CycleViewModel
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CycleScreen(viewModel: CycleViewModel = hiltViewModel()) {
    val phase by viewModel.currentPhase.collectAsState()
    val avgLength by viewModel.averageCycleLength.collectAsState()
    val nextPeriod by viewModel.nextPeriod.collectAsState()
    val history by viewModel.history.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mon cycle 🌸", style = MaterialTheme.typography.headlineMedium) }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                    Text(phase?.emoji ?: "🌸", fontSize = 64.sp)
                    Text(phase?.title ?: "Chargement...", style = MaterialTheme.typography.headlineLarge)
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                StatCard("Cycle moyen", "$avgLength jours")
                StatCard("Prochaines règles", nextPeriod?.format(DateTimeFormatter.ofPattern("dd MMM")) ?: "—")
            }

            Spacer(Modifier.height(24.dp))
            Text("Historique des cycles", style = MaterialTheme.typography.titleMedium)
            LazyColumn {
                items(history) { entry ->
                    CycleHistoryItem(entry)
                }
            }

            Spacer(Modifier.weight(1f))

            // Bouton entrée règles
            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("📅 Entrer mes règles")
            }
            if (showAddDialog) {
                AddPeriodDialog(
                    onDismiss = { showAddDialog = false },
                    onSave = { start, end, notes ->
                        viewModel.addNewPeriod(start, end, notes)
                    }
                )
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String) {
    Card(modifier = Modifier.width(160.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        }
    }
}
