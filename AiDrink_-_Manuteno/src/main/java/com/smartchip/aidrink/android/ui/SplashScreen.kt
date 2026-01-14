package com.smartchip.aidrink.android.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.smartchip.aidrink.android.mqtt.MqttViewModel
import com.smartchip.aidrink.android.ui.navigation.Routes

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: MqttViewModel
) {
    LaunchedEffect(viewModel.isReady) {
        if (viewModel.isReady && !viewModel.hasNavigated) {
            viewModel.hasNavigated = true

            // Se existe tópico, faz o subscribe.
            // Mas independente de ter tópico ou não, vamos para HOME.
            viewModel.currentTopic?.let {
                viewModel.subscribe(it)
            }

            navController.navigate(Routes.HOME) {
                popUpTo(Routes.SPLASH) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

//
//@Composable
//fun SplashScreen(
//    navController: NavController,
//    viewModel: MqttViewModel
//) {
//    LaunchedEffect(viewModel.isReady) {
//        if (viewModel.isReady && !viewModel.hasNavigated) {
//            viewModel.hasNavigated = true
//
//            val route = if (viewModel.currentTopic != null) {
//                // subscribe antes de ir para Home
//                viewModel.subscribe(viewModel.currentTopic!!)
//                Routes.HOME
//            } else {
//                Routes.SCANNER
//            }
//
//            navController.navigate(route) {
//                popUpTo(Routes.SPLASH) { inclusive = true }
//                launchSingleTop = true
//            }
//        }
//    }
//
//    Box(
//        modifier = Modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        CircularProgressIndicator()
//    }
//}

//@Composable
//fun SplashScreen(
//    navController: NavController,
//    viewModel: MqttViewModel
//) {
//    LaunchedEffect(viewModel.isReady, viewModel.hasNavigated) {
//        if (viewModel.isReady && !viewModel.hasNavigated) {
//
//            viewModel.hasNavigated = true // trava navegação
//
//            val route = if (viewModel.currentTopic != null) {
//                Routes.HOME
//            } else {
//                Routes.SCANNER
//            }
//
//            navController.navigate(route) {
//                popUpTo(Routes.SPLASH) { inclusive = true }
//                launchSingleTop = true
//            }
//        }
//    }
//
//    Box(
//        modifier = Modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        CircularProgressIndicator()
//    }
//}