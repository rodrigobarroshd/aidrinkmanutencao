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
import com.smartchip.aidrink.android.ui.SplashScreen
import com.smartchip.aidrink.android.ui.navigation.BottomBar
import com.smartchip.aidrink.android.ui.navigation.MqttMessagesScreen
import com.smartchip.aidrink.android.ui.navigation.Routes

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
//            NavHost(
//                navController = navController,
//                startDestination = "splash"
//            ) {
//                composable("home") {
//                    HomeScreen(viewModel, navController)
//                }
//
//                composable("home2") {
//                    // Exemplo de nova tela
//                }
//
//                composable("messages") {
//                    MqttMessagesScreen(
//                        viewModel = viewModel,
//                        navController = navController
//                    )
//                }
//
//                composable("scanner") {
//                    QrScannerScreen(
//                        viewModel = viewModel,
//                        onScan = { scannedTopic ->
//                            navController.navigate("home") {
//                                popUpTo("home") { inclusive = true }
//                            }
//                        }
//                    )
            NavHost(
                navController = navController,
                startDestination = Routes.SPLASH
            ) {

                composable(Routes.SPLASH) {
                    SplashScreen(navController, viewModel)
                }

                composable(Routes.SCANNER) {
                    QrScannerScreen(
                        viewModel = viewModel,
                        onScan = { topic ->
                            viewModel.subscribe(topic)
                            navController.navigate(Routes.HOME) {
                                popUpTo(Routes.SCANNER) { inclusive = true }
                            }
                        }
                    )
                }



                composable(Routes.HOME) {
                    HomeScreen(
                        viewModel = viewModel,
                        navController = navController
                    )
                }
                composable(Routes.MESSAGES) {  // ⚠️ adicionado
                    MqttMessagesScreen(viewModel = viewModel, navController = navController)
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