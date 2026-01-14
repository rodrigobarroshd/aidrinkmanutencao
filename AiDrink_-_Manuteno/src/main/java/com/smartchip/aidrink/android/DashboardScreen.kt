package com.smartchip.aidrink.android


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.smartchip.aidrink.android.mqtt.CommandFactory
import com.smartchip.aidrink.android.mqtt.MqttManager
import com.smartchip.aidrink.android.mqtt.MqttViewModel

@Composable
fun DashboardScreen(viewModel: MqttViewModel) {
    val gson = Gson()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

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