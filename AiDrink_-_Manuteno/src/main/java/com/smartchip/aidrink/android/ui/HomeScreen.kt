package com.smartchip.aidrink.android.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smartchip.aidrink.android.mqtt.MqttViewModel
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import com.smartchip.aidrink.android.R

@Composable
fun HomeScreen(viewModel: MqttViewModel, navController: NavController) {

    val topic = viewModel.currentTopic
    val lastMessage = viewModel.lastMessage

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // 🔝 BOTÃO ICON EXPRESSIVE NO CANTO SUPERIOR DIREITO
        if (topic != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // ⬅️ TEXTO NO CANTO SUPERIOR ESQUERDO
                Column {
                    Text(
                        text = topic,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // ➡️ BOTÃO NO CANTO SUPERIOR DIREITO
                IconButton(
                    onClick = { navController.navigate("scanner") },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.qr_code_2_24px),
                        contentDescription = "Trocar QR Code",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // 📄 CONTEÚDO PRINCIPAL
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp), // espaço para não ficar atrás do botão
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            if (topic == null) {
                Text(
                    text = "Nenhum dispenser conectado",
                    style = MaterialTheme.typography.headlineSmall
                )

                Text("Escaneie o QR Code localizado no dispenser para começar.")

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { navController.navigate("scanner") }
                ) {
                    Text("Escanear QR Code")
                }

            } else {



                Text("Última resposta:", style = MaterialTheme.typography.labelLarge)
                Text(
                    text = lastMessage ?: "Aguardando dados...",
                    style = MaterialTheme.typography.bodyMedium
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.sendCommand("unlock") }
                ) {
                    Text("Unlock (Abrir)")
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.sendCommand("dispenserCup") }
                ) {
                    Text("Dispenser Cup (Copo)")
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    onClick = { viewModel.sendCommand("reboot") }
                ) {
                    Text("Reboot (Reiniciar)")
                }
            }
        }
    }
}