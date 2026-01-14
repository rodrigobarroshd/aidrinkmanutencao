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
    private val client = MqttClient.builder()
        .useMqttVersion3()
        .identifier("android-client")
        .serverHost("jackal.rmq.cloudamqp.com")
        .serverPort(1883)
        .buildAsync()

    private val username = "svccxehy:svccxehy"
    private val password = "uK1j6eN--4CTWDy1CCqhPP8TVvIzrIMk"

    fun connect(onConnected: () -> Unit = {}) {
        val connectMessage = Mqtt3Connect.builder()
            .simpleAuth()
            .username(username)
            .password(password.toByteArray())
            .applySimpleAuth()
            .build()

        client.connect(connectMessage)
            .whenComplete { _, throwable ->
                if (throwable != null) throwable.printStackTrace()
                else onConnected()
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
    }

    fun publish(topic: String, payload: MqttPayload) {
        val json = gson.toJson(payload)

        client.publishWith()
            .topic(topic)
            .payload(json.toByteArray())
            .send()
    }
}