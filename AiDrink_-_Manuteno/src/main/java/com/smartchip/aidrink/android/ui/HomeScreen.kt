package com.smartchip.aidrink.android.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import com.smartchip.aidrink.android.R
import com.smartchip.aidrink.android.mqtt.CommandFactory
import com.smartchip.aidrink.android.ui.mqtt.CouplerSelector

@Composable
fun HomeScreen(viewModel: MqttViewModel, navController: NavController) {

    val scrollState = rememberScrollState()
    val topic = viewModel.currentTopic
    val lastMessage = viewModel.lastMessage
    var showEditModal by remember { mutableStateOf(false) }
    var editTopicInput by remember { mutableStateOf("") }
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

            if (topic == null) {
                // Estado para o input manual de tópico
                var manualTopicInput by remember { mutableStateOf("") }

                Text(
                    text = "Configurar Dispenser",
                    style = MaterialTheme.typography.headlineSmall
                )

                Text("Conecte-se escaneando o QR Code ou digitando o tópico manualmente.")

                // --- OPÇÃO 1: ESCANEAR ---
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { navController.navigate("scanner") }
                ) {
                    Icon(painterResource(id = R.drawable.qr_code_2_24px), contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text("Escanear QR Code")
                }

                Text(
                    text = "ou digite o tópico",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                // --- OPÇÃO 2: DIGITAR ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = manualTopicInput,
                        onValueChange = { manualTopicInput = it },
                        label = { Text("Tópico") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            if (manualTopicInput.isNotBlank()) {
                                viewModel.subscribe(manualTopicInput)
                            }
                        },
                        modifier = Modifier.size(56.dp).padding(top = 4.dp),
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        // Ícone de seta ou "OK"
                        Text("Ir")
                    }
                }

            } else {

                var selectedCoupler by remember { mutableStateOf(0) }

                var pressureInput by remember { mutableStateOf("") }
                var pulseInput by remember { mutableStateOf("") }

                var freezerHour by remember { mutableStateOf("") }
                var freezerDuration by remember { mutableStateOf("") }

                // 🔘 coupler selector
                Text("Coupler", style = MaterialTheme.typography.labelLarge)
                CouplerSelector(
                    selectedCoupler = selectedCoupler,
                    onSelect = { selectedCoupler = it }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // ==========================
                // 🔧 PRESSURE
                // ==========================
                Text("Pressure", style = MaterialTheme.typography.titleMedium)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically // Alinha botões e input no meio
                ) {
                    // Botão GET Redondo
                    Button(
                        onClick = {
                            viewModel.sendCommand(CommandFactory.getPressure(selectedCoupler))
                        },
                        modifier = Modifier.size(56.dp).padding(top = 4.dp),// Define um tamanho igual para ser circular
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp) // Remove padding interno para o texto caber
                    ) {
                        Text("Get", style = MaterialTheme.typography.labelSmall)
                    }

                    // Botão SET Redondo
                    Button(
                        onClick = {
                            pressureInput.toFloatOrNull()?.let { pressure ->
                                viewModel.sendCommand(
                                    CommandFactory.setPressure(
                                        coupler = selectedCoupler,
                                        pressureCoupler = pressure
                                    )
                                )
                            }
                        },
                        modifier = Modifier.size(56.dp).padding(top = 4.dp),
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Set", style = MaterialTheme.typography.labelSmall)
                    }

                    // Campo de entrada que ocupa o resto do espaço
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = pressureInput,
                        onValueChange = { pressureInput = it },
                        label = { Text("Pressure") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp) // Bordas arredondadas para combinar
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // ==========================
                // 🔧 CALIBRATE PULSE
                // ==========================
                Text("Calibrate Pulse", style = MaterialTheme.typography.titleMedium)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botão Get (Redondo)
                    Button(
                        onClick = {
                            viewModel.sendCommand(CommandFactory.getCalibratePulse(selectedCoupler))
                        },
                        modifier = Modifier.size(52.dp).padding(top = 4.dp), // Tamanho fixo para ser circular
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Get", style = MaterialTheme.typography.labelSmall)
                    }

                    // Botão Set (Redondo)
                    Button(
                        onClick = {
                            pulseInput.toFloatOrNull()?.let { pulse ->
                                viewModel.sendCommand(
                                    CommandFactory.setCalibratePulse(
                                        coupler = selectedCoupler,
                                        pulse = pulse
                                    )
                                )
                            }
                        },
                        modifier = Modifier.size(52.dp).padding(top = 4.dp),
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Set", style = MaterialTheme.typography.labelSmall)
                    }

                    // Input (Ocupa o resto)
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = pulseInput,
                        onValueChange = { pulseInput = it },
                        label = { Text("Pulse") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // ==========================
                // ❄️ FREEZER DOWN
                // ==========================
                Text("Freezer Down", style = MaterialTheme.typography.titleMedium)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Input Hora
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = freezerHour,
                        onValueChange = { freezerHour = it },
                        label = { Text("Hour") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Input Duração
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = freezerDuration,
                        onValueChange = { freezerDuration = it },
                        label = { Text("Dur.") }, // Nome curto para caber melhor
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Botão SET Redondo
                    Button(
                        onClick = {
                            val hour = freezerHour.toIntOrNull()
                            val duration = freezerDuration.toIntOrNull()

                            if (hour != null && duration != null) {
                                viewModel.sendCommand(
                                    CommandFactory.setFreezerDown(hour = hour, duration = duration)
                                )
                            }
                        },
                        modifier = Modifier.size(52.dp).padding(top = 4.dp),
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Set", style = MaterialTheme.typography.labelSmall)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // ==========================
                // 📩 ÚLTIMA MENSAGEM
                // ==========================
                Text("Última resposta:", style = MaterialTheme.typography.labelLarge)
                Text(
                    text = lastMessage ?: "Aguardando dados...",
                    style = MaterialTheme.typography.bodyMedium
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // ==========================
                // 🔌 COMANDOS GERAIS
                // ==========================
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.sendCommand(CommandFactory.unlock())}
                ) {
                    Text("Unlock (Abrir)")
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.sendCommand(CommandFactory.dispenserCup())}
                ) {
                    Text("Dispenser Cup (Copo)")
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    onClick = { viewModel.sendCommand(CommandFactory.reboot())}
                ) {
                    Text("Reboot (Reiniciar)")
                }
            }
        }
    }
    if (showEditModal) {
        AlertDialog(
            onDismissRequest = { showEditModal = false },
            title = { Text("Editar Tópico") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Digite o novo tópico para conexão:")
                    OutlinedTextField(
                        value = editTopicInput,
                        onValueChange = { editTopicInput = it },
                        label = { Text("Tópico") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editTopicInput.isNotBlank()) {
                            viewModel.subscribe(editTopicInput)
                            showEditModal = false
                        }
                    }
                ) {
                    Text("Conectar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showEditModal = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
