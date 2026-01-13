package com.smartchip.aidrink.android.mqtt

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MqttViewModel : ViewModel() {

    private val mqttManager = MqttManager()

    var lastMessage by mutableStateOf("")
        private set

    var currentTopic by mutableStateOf<String?>(null)
        private set

    // 🔥 HISTÓRICO DE MENSAGENS
    var messages = mutableStateListOf<String>()
        private set

    fun connect(onConnected: () -> Unit = {}) {
        mqttManager.connect(onConnected)
    }

    fun subscribe(topic: String) {
        currentTopic = topic
        messages.clear() // limpa histórico ao trocar de QR / tópico

        mqttManager.subscribe(topic) { message ->
            lastMessage = message
            messages.add(0, message) // adiciona no topo (mais recente primeiro)
        }
    }

    fun sendCommand(command: String) {
        currentTopic?.let { mqttManager.publish(it, command) }
    }
}
//class MqttViewModel : ViewModel() {
//
//    private val mqttManager = MqttManager()
//
//    var lastMessage by mutableStateOf("")
//        private set
//
//    var currentTopic by mutableStateOf<String?>(null)
//        private set
//
//    fun connect(onConnected: () -> Unit = {}) {
//        mqttManager.connect(onConnected)
//    }
//
//    fun subscribe(topic: String) {
//        currentTopic = topic
//        mqttManager.subscribe(topic) { message ->
//            lastMessage = message
//
//        }
//    }
//
//    fun sendCommand(command: String) {
//        currentTopic?.let { mqttManager.publish(it, command) }
//    }
//}




//class MqttViewModel : ViewModel() {
//
//    private val mqttManager = MqttManager()
//
//    // Última mensagem recebida do MQTT
//    var lastMessage by mutableStateOf("")
//        private set
//
//    // Tópico atual vindo do QR Code
//    var currentTopic by mutableStateOf<String?>(null)
//        private set
//
//    /**
//     * Conecta ao broker MQTT (CloudAMQP)
//     */
//    fun connect(onConnected: () -> Unit = {}) {
//        mqttManager.connect(onConnected)
//    }
//
//    /**
//     * Se inscreve no tópico do QR Code
//     */
//    fun subscribe(topic: String) {
//        currentTopic = topic
//        mqttManager.subscribe(topic) { message ->
//            lastMessage = message
//        }
//    }
//
//    /**
//     * Envia comando para o tópico atual
//     */
//    fun sendCommand(command: String) {
//        currentTopic?.let {
//            mqttManager.publish(it, command)
//        }
//
//
//    }}