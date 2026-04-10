package io.tvelu77.freya.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import io.tvelu77.freya.models.Phase
import io.tvelu77.freya.viewModels.CycleViewModel
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CycleScreen(viewModel: CycleViewModel = hiltViewModel()) {
    val phase by viewModel.currentPhase.collectAsState()
    val avgLength by viewModel.averageCycleLength.collectAsState()
    val nextPeriod by viewModel.nextPeriod.collectAsState()
    val periods by viewModel.periods.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }
    var showAdviceDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mon cycle 🌸", style = MaterialTheme.typography.headlineMedium) }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))
            
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
                    .clickable { showAdviceDialog = true }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(20.dp)) {
                    Text(phase.emoji, fontSize = 48.sp)
                    if (phase == Phase.UNKNOWN) {
                        Text("Bienvenue", style = MaterialTheme.typography.headlineMedium)
                    } else {
                        Text(phase.title, style = MaterialTheme.typography.headlineMedium)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            CycleCalendar(
                periods = periods,
                nextPeriodDate = nextPeriod,
                avgCycleLength = avgLength,
                onDayClick = { _ -> showAddDialog = true }
            )

            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                StatCard("Cycle moyen", "$avgLength j")
                StatCard("Prochaines règles", nextPeriod?.format(DateTimeFormatter.ofPattern("dd MMM")) ?: "—")
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("📅 Entrer mes règles")
            }
            
            Spacer(Modifier.height(24.dp))
            
            if (showAddDialog) {
                AddPeriodDialog(
                    onDismiss = { showAddDialog = false },
                    onSave = { start, end, notes ->
                        viewModel.addNewPeriod(start, end, notes)
                    }
                )
            }
            if (showAdviceDialog) {
                AdviceDialog(phase, onDismiss = { showAdviceDialog = false })
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String) {
    Card(modifier = Modifier.width(160.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall)
            Text(value, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        }
    }
}
