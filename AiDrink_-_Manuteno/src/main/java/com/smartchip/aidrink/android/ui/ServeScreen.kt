package com.smartchip.aidrink.android.ui


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smartchip.aidrink.android.R
import com.smartchip.aidrink.android.mqtt.CommandFactory
import com.smartchip.aidrink.android.mqtt.MqttViewModel
import com.smartchip.aidrink.android.ui.mqtt.CouplerSelector
import com.smartchip.aidrink.android.ui.mqtt.VolumeSelector

@Composable
fun ServeScreen(
    viewModel: MqttViewModel,
    navController: NavController
) {
    val scrollState = rememberScrollState()
    var volume by remember { mutableIntStateOf(50) }
    var foam by remember { mutableIntStateOf(0) }
    var selectedCoupler by remember { mutableStateOf(0) }
    var showEditModal by remember { mutableStateOf(false) }
    var editTopicInput by remember { mutableStateOf("") }
    val topic = viewModel.currentTopic ?: "unknown"
    val lastMessage = viewModel.lastMessage

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 🔝 CABEÇALHO
        if (topic != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // ⬅️ Tópico à esquerda
                Text(
                    text = topic ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                        .clickable {
                            editTopicInput = topic ?: "" // Preenche com o tópico atual ao abrir
                            showEditModal = true
                        }
                )

                // ➡️ Grupo de botões à direita
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botão QR Code (Scanner)
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

                    // Botão SAIR (Logout/Clear)
                    IconButton(
                        onClick = { viewModel.clearSubscription() }, // Chama a função que limpa o tópico
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.logout_24px), // Certifique-se de ter um ícone de logout ou close
                            contentDescription = "Sair da Subscrição",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

        Text("Coupler", style = MaterialTheme.typography.labelLarge)
        CouplerSelector(
            selectedCoupler = selectedCoupler,
            onSelect = { selectedCoupler = it }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        Text("Volume", style = MaterialTheme.typography.labelLarge)

        VolumeSelector(
            value = volume,
            onValueChange = { volume = it }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val payload = CommandFactory.serve(
                    volume = volume,
                    foam = foam,
                    beverator = topic,
                    orderId = "ID_ORDER",
                    kegId = "ID_KEG",
                    coupler = selectedCoupler,
                    customer = "customer_name"
                )
                viewModel.sendCommand(payload)
            },
        ) {
            Text("SERVE")
        }
            Text("Última resposta:", style = MaterialTheme.typography.labelLarge)
            Text(
                text = lastMessage ?: "Aguardando dados...",
                style = MaterialTheme.typography.bodyMedium
            )
        }
}
}