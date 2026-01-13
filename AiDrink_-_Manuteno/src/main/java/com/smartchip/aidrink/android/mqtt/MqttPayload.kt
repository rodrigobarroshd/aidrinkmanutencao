package com.smartchip.aidrink.android.mqtt

data class MqttPayload(
    val command: String,
    val coupler: Int? = null,
    val pressure_coupler: String? = null,
    val system: String? = null
)