package io.tvelu77.freya.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.tvelu77.freya.models.Phase

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdviceDialog(
    phase: Phase,
    onDismiss: () -> Unit
) {

    val adviceText = when (phase) {
        Phase.MENSTRUAL -> "Prenez soin de vous, reposez-vous et hydratez-vous bien. Évitez les efforts intenses."
        Phase.FOLLICULAR -> "C’est une période d’énergie ! Profitez-en pour être active et planifier des projets."
        Phase.OVULATION -> "Votre énergie et votre libido sont au maximum. Idéal pour les activités sociales et créatives."
        Phase.LUTEAL -> "Écoutez votre corps, gérez le stress et privilégiez une alimentation équilibrée."
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "🌸 Conseils pour la phase : ${phase.name.lowercase().replaceFirstChar { it.uppercase() }}",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = adviceText,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )

}