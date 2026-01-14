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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MqttViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val mqttManager = MqttManager()
    private val subscribedTopics = mutableSetOf<String>()

    var isReady by mutableStateOf(false)
        private set

    var hasNavigated by mutableStateOf(false)

    var isMqttConnected by mutableStateOf(false)
        private set

    var lastMessage by mutableStateOf("")
        private set

    var currentTopic by mutableStateOf<String?>(null)
        private set

    var messages = mutableStateListOf<String>()
        private set

    init {
        restoreLastTopic()
        monitorConnection() // Inicia o monitor de conexão
    }

    // Loop que checa se o MQTT ainda está vivo (útil após o reboot)
    private fun monitorConnection() {
        viewModelScope.launch {
            while (true) {
                isMqttConnected = mqttManager.isConnected
                delay(2000) // Checa a cada 2 segundos
            }
        }
    }

    private fun restoreLastTopic() {
        viewModelScope.launch {
            val savedTopic = TopicPreferences.getLastTopic(context)
            currentTopic = savedTopic

            // Tentativa de conexão em background
            connect {
                savedTopic?.let { subscribe(it) }
            }

            delay(500) // 💡 Pequeno fôlego para garantir que a UI está pronta para ouvir
            isReady = true
            println("DEBUG: isReady agora é true")
        }
    }
    fun connect(onConnected: () -> Unit = {}) {
        mqttManager.connect(onConnected)
    }

    fun subscribe(topic: String) {
        // Remove bloqueios de duplicidade se quiser forçar a re-assinatura após queda
        if (topic.isBlank()) return
        if (subscribedTopics.contains(topic)) return
        currentTopic = topic
        viewModelScope.launch {
            TopicPreferences.saveTopic(context, topic)
        }

        mqttManager.subscribe(topic) { message ->
            lastMessage = message
            messages.add(message)
        }
    }

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
//class MqttViewModel(
//
//    application: Application
//
//) : AndroidViewModel(application) {
//
//
//
//    var isReady by mutableStateOf(false)
//        private set
//
//    private val subscribedTopics = mutableSetOf<String>()
//
//    private val context = application.applicationContext
//    private val mqttManager = MqttManager()
//
//    var lastMessage by mutableStateOf("")
//        private set
//
//    var currentTopic by mutableStateOf<String?>(null)
//        private set
//
//    var messages = mutableStateListOf<String>()
//        private set
//
//    var hasNavigated by mutableStateOf(false)
//
//
//
//
//
//    init {
//
//        restoreLastTopic()
//
//    }
//
//
//
//    private fun restoreLastTopic() {
//
//        viewModelScope.launch {
//
//            val savedTopic = TopicPreferences.getLastTopic(context)
//
//            currentTopic = savedTopic // define tópico antes
//            connect {
//
//                savedTopic?.let { subscribe(it) }
//
//            }
//
//
//
//// ⚠️ só aqui marca pronto, **depois do subscribe ser iniciado**
//
//            isReady = true
//
//        }
//
//    }
//
//
//
//    fun connect(onConnected: () -> Unit = {}) {
//
//        mqttManager.connect(onConnected)
//
//    }
//
//
//
//    fun subscribe(topic: String) {
//
//        if (subscribedTopics.contains(topic)) return // já inscrito
//
//        subscribedTopics.add(topic)
//
//
//
//        currentTopic = topic
//
//
//
//        viewModelScope.launch {
//
//            TopicPreferences.saveTopic(context, topic)
//
//        }
//
//
//
//        mqttManager.subscribe(topic) { message ->
//
//            lastMessage = message
//
//            messages.add(message) // histórico
//
//        }
//
//    }
//
//
//    fun clearSubscription() {
//
//        currentTopic = null
//
//        viewModelScope.launch {
//
//            TopicPreferences.clear(context)
//
//        }
//
//    }
//
//    fun sendCommand(payload: MqttPayload) {
//
//        currentTopic?.let { topic ->
//
//            mqttManager.publish(topic, payload)
//
//        }
//
//    }
//
//}
//class MqttViewModel(
//    application: Application
//) : AndroidViewModel(application) {
//
//    var isReady by mutableStateOf(false)
//        private set
//
//    private val subscribedTopics = mutableSetOf<String>()
//    private val context = application.applicationContext
//    private val mqttManager = MqttManager()
//
//    var lastMessage by mutableStateOf("")
//        private set
//
//    var currentTopic by mutableStateOf<String?>(null)
//        private set
//
//    var messages = mutableStateListOf<String>()
//        private set
//
//    var hasNavigated by mutableStateOf(false)
//
//
//    init {
//        restoreLastTopic()
//    }
//
//    private fun restoreLastTopic() {
//        viewModelScope.launch {
//            val savedTopic = TopicPreferences.getLastTopic(context)
//            currentTopic = savedTopic // define tópico antes
//
//            // conecta MQTT
//            connect {
//                savedTopic?.let { subscribe(it) }
//            }
//
//            // ⚠️ só aqui marca pronto, **depois do subscribe ser iniciado**
//            isReady = true
//        }
//    }
//
//    fun connect(onConnected: () -> Unit = {}) {
//        mqttManager.connect(onConnected)
//    }
//
//    fun subscribe(topic: String) {
//        if (subscribedTopics.contains(topic)) return // já inscrito
//        subscribedTopics.add(topic)
//
//        currentTopic = topic
//
//        viewModelScope.launch {
//            TopicPreferences.saveTopic(context, topic)
//        }
//
//        mqttManager.subscribe(topic) { message ->
//            lastMessage = message
//            messages.add(message) // histórico
//        }
//    }
//
//
//    fun clearSubscription() {
//        currentTopic = null
//        viewModelScope.launch {
//            TopicPreferences.clear(context)
//        }
//    }
//
//    fun sendCommand(payload: MqttPayload) {
//
//        currentTopic?.let { topic ->
//            mqttManager.publish(topic, payload)
//        }
//    }
//}