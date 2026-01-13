package com.smartchip.aidrink.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smartchip.aidrink.android.mqtt.MqttViewModel
import com.smartchip.aidrink.android.qrcode.QrScannerScreen
import com.smartchip.aidrink.android.ui.HomeScreen


class MainActivity : ComponentActivity() {

    // Instancia ViewModel corretamente
    private val viewModel: MqttViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Conecta no MQTT (CloudAMQP)
        viewModel.connect {
            println("Conectado!")
        }


        setContent {
            val navController = rememberNavController()

            Surface(color = MaterialTheme.colorScheme.background) {
                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable("home") {
                        HomeScreen(viewModel, navController)
                    }
                    composable("scanner") {
                        QrScannerScreen(
                            viewModel = viewModel,
                            onScan = { scannedTopic ->
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}