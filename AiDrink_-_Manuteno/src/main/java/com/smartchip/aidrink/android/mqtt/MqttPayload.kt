package com.smartchip.aidrink.android.mqtt

data class MqttPayload(
    val command: String,

    val coupler: Int? = null,
    val pressure_coupler: Int? = null,
    val pulse: Int? = null,

    val hour: Int? = null,
    val duration: Int? = null,

    val system: String? = null
)
