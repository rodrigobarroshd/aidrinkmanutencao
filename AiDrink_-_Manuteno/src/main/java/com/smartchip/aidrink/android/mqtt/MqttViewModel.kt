package com.smartchip.aidrink.android.mqtt

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch



class MqttViewModel(
    application: Application
) : AndroidViewModel(application) {

    var isReady by mutableStateOf(false)
        private set

    private val subscribedTopics = mutableSetOf<String>()
    private val context = application.applicationContext
    private val mqttManager = MqttManager()

    var lastMessage by mutableStateOf("")
        private set

    var currentTopic by mutableStateOf<String?>(null)
        private set

    var messages = mutableStateListOf<String>()
        private set

    var hasNavigated by mutableStateOf(false)


    init {
        restoreLastTopic()
    }

    private fun restoreLastTopic() {
        viewModelScope.launch {
            val savedTopic = TopicPreferences.getLastTopic(context)
            currentTopic = savedTopic // define tópico antes

            // conecta MQTT
            connect {
                savedTopic?.let { subscribe(it) }
            }

            // ⚠️ só aqui marca pronto, **depois do subscribe ser iniciado**
            isReady = true
        }
    }

    fun connect(onConnected: () -> Unit = {}) {
        mqttManager.connect(onConnected)
    }

    fun subscribe(topic: String) {
        if (subscribedTopics.contains(topic)) return // já inscrito
        subscribedTopics.add(topic)

        currentTopic = topic

        viewModelScope.launch {
            TopicPreferences.saveTopic(context, topic)
        }

        mqttManager.subscribe(topic) { message ->
            lastMessage = message
            messages.add(message) // histórico
        }
    }

//    fun subscribe(topic: String) {
//        currentTopic = topic
//
//        viewModelScope.launch {
//            TopicPreferences.saveTopic(context, topic)
//        }
//
//        mqttManager.subscribe(topic) { message ->
//            lastMessage = message
//            messages.add(message) // 👈 guarda histórico
//        }
//    }

    fun clearSubscription() {
        currentTopic = null
        viewModelScope.launch {
            TopicPreferences.clear(context)
        }
    }

    fun sendCommand(payload: MqttPayload) {
        currentTopic?.let { topic ->
            mqttManager.publish(topic, payload)
        }
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
//    // 🔥 HISTÓRICO DE MENSAGENS
//    var messages = mutableStateListOf<String>()
//        private set
//
//    fun connect(onConnected: () -> Unit = {}) {
//        mqttManager.connect(onConnected)
//    }
//
//    fun subscribe(topic: String) {
//        currentTopic = topic
//        messages.clear() // limpa histórico ao trocar de QR / tópico
//
//        mqttManager.subscribe(topic) { message ->
//            lastMessage = message
//            messages.add(0, message) // adiciona no topo (mais recente primeiro)
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