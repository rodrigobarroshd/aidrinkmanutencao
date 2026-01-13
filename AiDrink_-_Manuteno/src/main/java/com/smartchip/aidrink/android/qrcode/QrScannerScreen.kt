package com.smartchip.aidrink.android.qrcode

import android.Manifest
import androidx.compose.runtime.Composable
import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.smartchip.aidrink.android.mqtt.MqttViewModel
import java.util.concurrent.Executors


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QrScannerScreen(viewModel: MqttViewModel, onScan: (String) -> Unit) {
    // 1. Estado da permissão de câmera
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    // Se a permissão estiver concedida, mostra a câmera
    if (cameraPermissionState.status.isGranted) {
        CameraPreviewContent(viewModel, onScan)
    } else {
        // Se não tiver permissão, mostra tela de solicitação
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val textToShow = if (cameraPermissionState.status.shouldShowRationale) {
                "A câmera é necessária para ler o QR Code do dispenser."
            } else {
                "Permita o uso da câmera para continuar."
            }
            Text(textToShow, modifier = Modifier.padding(16.dp))
            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                Text("Dar Permissão")
            }
        }
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
private fun CameraPreviewContent(viewModel: MqttViewModel, onScan: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = context as LifecycleOwner

    val scanner = remember { BarcodeScanning.getClient() }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    var lastScannedQr by remember { mutableStateOf("") }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = androidx.camera.core.Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                    processImageProxy(scanner, imageProxy) { qrValue ->
                        if (qrValue != lastScannedQr) {
                            lastScannedQr = qrValue
                            viewModel.subscribe(qrValue)

                            // Chama o callback para fechar a tela ou navegar
                            onScan(qrValue)

                            Log.d("QRScanner", "QR Detectado: $qrValue")
                        }
                    }
                }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    Log.e("QRScanner", "Erro ao abrir câmera", e)
                }
            }, ContextCompat.getMainExecutor(ctx))
            previewView
        },
        modifier = Modifier.fillMaxSize()
    )

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
            scanner.close()
        }
    }
}

@SuppressLint("UnsafeOptInUsageError")
private fun processImageProxy(
    scanner: BarcodeScanner,
    imageProxy: ImageProxy,
    onSuccess: (String) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    barcode.rawValue?.let { onSuccess(it) }
                }
            }
            .addOnCompleteListener { imageProxy.close() }
    } else {
        imageProxy.close()
    }
}

//
//@SuppressLint("UnsafeOptInUsageError")
//@Composable
//fun QrScannerScreen(viewModel: MqttViewModel, onScan: (String) -> Unit) {
//
//    val context = LocalContext.current
//    val lifecycleOwner = LocalContext.current as LifecycleOwner
//
//    // 1. Criar o Scanner e o Executor apenas uma vez
//    val scanner = remember { BarcodeScanning.getClient() }
//    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
//
//    // Estado para evitar múltiplas subscrições simultâneas do mesmo QR Code
//    var lastScannedQr by remember { mutableStateOf("") }
//
//    AndroidView(
//        factory = { ctx ->
//            val previewView = PreviewView(ctx)
//            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
//
//            cameraProviderFuture.addListener({
//                val cameraProvider = cameraProviderFuture.get()
//
//                // Configuração do Preview
//                val preview = androidx.camera.core.Preview.Builder().build().also {
//                    it.setSurfaceProvider(previewView.surfaceProvider)
//                }
//
//                // Configuração da Análise de Imagem
//                val imageAnalysis = ImageAnalysis.Builder()
//                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                    .build()
//
//                imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
//                    processImageProxy(scanner, imageProxy) { qrValue ->
//                        // Só processa se for um QR novo para não inundar o MQTT
//                        if (qrValue != lastScannedQr) {
//                            lastScannedQr = qrValue
//                            viewModel.subscribe(qrValue)
//                            Log.d("QRScanner", "Novo QR Scanned: $qrValue")
//                        }
//                    }
//                }
//
//                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//
//                try {
//                    cameraProvider.unbindAll()
//                    cameraProvider.bindToLifecycle(
//                        lifecycleOwner,
//                        cameraSelector,
//                        preview,
//                        imageAnalysis
//                    )
//                } catch (e: Exception) {
//                    Log.e("QRScanner", "Falha ao vincular câmera", e)
//                }
//            }, ContextCompat.getMainExecutor(ctx))
//
//            previewView
//        },
//        modifier = Modifier.fillMaxSize()
//    )
//
//    // Fechar o executor quando o Composable sair da tela
//    DisposableEffect(Unit) {
//        onDispose {
//            cameraExecutor.shutdown()
//            scanner.close()
//        }
//    }
//}
//
//@SuppressLint("UnsafeOptInUsageError")
//private fun processImageProxy(
//    scanner: BarcodeScanner,
//    imageProxy: ImageProxy,
//    onSuccess: (String) -> Unit
//) {
//    val mediaImage = imageProxy.image
//    if (mediaImage != null) {
//        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
//
//        scanner.process(image)
//            .addOnSuccessListener { barcodes ->
//                for (barcode in barcodes) {
//                    barcode.rawValue?.let { onSuccess(it) }
//                }
//            }
//            .addOnFailureListener { Log.e("QRScanner", "Erro no processamento", it) }
//            .addOnCompleteListener {
//                // MUITO IMPORTANTE: fechar o frame para liberar o próximo
//                imageProxy.close()
//            }
//    } else {
//        imageProxy.close()
//    }
//}