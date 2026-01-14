package com.smartchip.aidrink.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
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
import com.smartchip.aidrink.android.ui.ServeScreen
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
            val hideBottomBar =
                currentRoute == Routes.SPLASH ||
                        currentRoute == Routes.SCANNER ||
                        viewModel.currentTopic == null   // 👈 AQUI

            if (!hideBottomBar) {
                BottomBar(navController = navController)
            }
        }
    )  { innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(
                navController = navController,
                startDestination = Routes.SPLASH,enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(400)
                    )
                },
                // A tela atual sai para a DIREITA
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(400)
                    )
                },

                // --- AO VOLTAR (Ex: Serve -> Home) ---
                // A tela anterior volta vindo da direita para a ESQUERDA
                popEnterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(400)
                    )
                },
                // A tela atual sai para a ESQUERDA
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(400)
                    )
                }
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
                composable(Routes.SERVE) {
                    ServeScreen(viewModel, navController)
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