package com.smartchip.aidrink.android.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.OutlinedButton

@Composable
fun HomeScreen(viewModel: MqttViewModel, navController: NavController) {

    val topic = viewModel.currentTopic
    val lastMessage = viewModel.lastMessage

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (topic == null) {
            // Estado: Sem conexão / Esperando Scan
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
            // Estado: Conectado ao Tópico
            Text("Tópico Ativo:", style = MaterialTheme.typography.labelLarge)
            Text(text = topic, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)

            Text("Última resposta:", style = MaterialTheme.typography.labelLarge)
            Text(text = lastMessage ?: "Aguardando dados...", style = MaterialTheme.typography.bodyMedium)

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Comandos do MQTT
            Button(modifier = Modifier.fillMaxWidth(), onClick = { viewModel.sendCommand("unlock") }) {
                Text("Unlock (Abrir)")
            }

            Button(modifier = Modifier.fillMaxWidth(), onClick = { viewModel.sendCommand("dispenserCup") }) {
                Text("Dispenser Cup (Copo)")
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                onClick = { viewModel.sendCommand("reboot") }
            ) {
                Text("Reboot (Reiniciar)")
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // --- NOVO BOTÃO PARA VOLTAR À CÂMERA ---
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { navController.navigate("scanner") }
            ) {
                Text("Escanear Outro QR Code")
            }
        }
    }
}