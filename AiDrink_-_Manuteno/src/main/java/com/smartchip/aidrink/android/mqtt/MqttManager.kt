package com.smartchip.aidrink.android.mqtt

import android.content.Context
import com.google.gson.Gson

import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.MqttGlobalPublishFilter
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client
import com.hivemq.client.mqtt.mqtt3.message.connect.Mqtt3Connect
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish


class MqttManager {

    private val gson = Gson()

    // Configuração do Cliente com Reconexão Automática
    private val client: Mqtt3AsyncClient = MqttClient.builder()
        .useMqttVersion3()
        // ID único evita que o broker derrube a conexão anterior ao tentar reconectar
        .identifier("app-manutencao-${System.currentTimeMillis()}")
        .serverHost("jackal.rmq.cloudamqp.com")
        .serverPort(1883)
        .automaticReconnectWithDefaultConfig() // Reconecta sozinho se o Wi-Fi ou Dispenser caírem
        .buildAsync()

    private val username = "gqwcgzqs:gqwcgzqs"
    private val password = "EjEWkdLbaXD388C2q0UmsKjhKgkM_BC2"

    // Verifica se está conectado no momento
    val isConnected: Boolean get() = client.state.isConnected

    fun connect(onConnected: () -> Unit = {}) {
        val connectMessage = Mqtt3Connect.builder()
            .simpleAuth()
            .username(username)
            .password(password.toByteArray())
            .applySimpleAuth()
            .cleanSession(true)
            .keepAlive(30)
            .build()

        client.connect(connectMessage)
            .whenComplete { _, throwable ->
                if (throwable != null) {
                    throwable.printStackTrace()
                } else {
                    onConnected()
                }
            }
    }

    fun subscribe(topic: String, onMessage: (String) -> Unit) {
        client.subscribeWith()
            .topicFilter(topic)
            .callback { publish: Mqtt3Publish ->
                val msg = publish.payloadAsBytes?.let { String(it) } ?: ""
                onMessage(msg)
            }
            .send()
            .whenComplete { _, throwable ->
                if (throwable != null) println("Falha ao assinar tópico: $topic")
            }
    }

    fun publish(topic: String, payload: MqttPayload) {
        val json = gson.toJson(payload)

        // Tenta publicar. Se estiver desconectado, o HiveMQ pode colocar na fila
        // ou falhar dependendo da configuração. Aqui forçamos a checagem.
        if (client.state.isConnected) {
            client.publishWith()
                .topic(topic)
                .payload(json.toByteArray())
                .send()
        } else {
            println("Erro: Comando não enviado. MQTT Desconectado.")
        }
    }
}

//class MqttManager {
//
//    private val gson = Gson()
//    private val client = MqttClient.builder()
//        .useMqttVersion3()
//        .identifier("android-client")
//        .serverHost("jackal.rmq.cloudamqp.com")
//        .serverPort(1883)
//        .buildAsync()
//
//    private val username = "svccxehy:svccxehy"
//    private val password = "uK1j6eN--4CTWDy1CCqhPP8TVvIzrIMk"
//
//    fun connect(onConnected: () -> Unit = {}) {
//        val connectMessage = Mqtt3Connect.builder()
//            .simpleAuth()
//            .username(username)
//            .password(password.toByteArray())
//            .applySimpleAuth()
//            .build()
//
////        client.connect(connectMessage)
////            .whenComplete { _, throwable ->
////                if (throwable != null) throwable.printStackTrace()
////                else onConnected()
////            }
//
//        client.connect(connectMessage)
//            .whenComplete { _, throwable ->
//                if (throwable != null) {
//                    throwable.printStackTrace()
//                } else {
//                    onConnected()
//                }
//            }
//
//        fun subscribe(topic: String, onMessage: (String) -> Unit) {
//            client.subscribeWith()
//                .topicFilter(topic)
//                .callback { publish: Mqtt3Publish ->
//                    val msg = publish.payloadAsBytes?.let { String(it) } ?: ""
//                    onMessage(msg)
//                }
//                .send()
//                .whenComplete { _, throwable ->
//                    if (throwable != null) {
//                        println("Erro ao assinar: ${throwable.message}")
//                    }
//                }
//        }
//
//        fun publish(topic: String, payload: MqttPayload) {
//            val json = gson.toJson(payload)
//
//            // 3. Verificação de segurança antes de publicar
//            if (client.state.isConnected) {
//                client.publishWith()
//                    .topic(topic)
//                    .payload(json.toByteArray())
//                    .send()
//            } else {
//                println("Impossível publicar: Cliente desconectado")
//                // Opcional: tentar reconectar aqui se estiver offline
//            }
//        }
//    }
//}

