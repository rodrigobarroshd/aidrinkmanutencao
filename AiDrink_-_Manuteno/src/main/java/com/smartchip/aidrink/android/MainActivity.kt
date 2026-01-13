package com.smartchip.aidrink.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.smartchip.aidrink.android.mqtt.MqttViewModel
import com.smartchip.aidrink.android.qrcode.QrScannerScreen
import com.smartchip.aidrink.android.ui.HomeScreen
import com.smartchip.aidrink.android.ui.navigation.BottomBar

@Composable
fun MainApp(viewModel: MqttViewModel) {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            // Esconde BottomBar no scanner
            if (currentRoute != "scanner") {
                BottomBar(navController = navController)
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") {
                    HomeScreen(viewModel, navController)
                }

                composable("home2") {
                    // Exemplo de nova tela
                }

                composable("home3") {
                    // Exemplo de nova tela
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
class MainActivity : ComponentActivity() {

    private val viewModel: MqttViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Conecta no MQTT
        viewModel.connect {
            println("Conectado!")
        }

        setContent {
            MainApp(viewModel)
        }
    }
}